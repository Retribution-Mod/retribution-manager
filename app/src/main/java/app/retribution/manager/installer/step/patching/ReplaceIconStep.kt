package app.retribution.manager.installer.step.patching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import app.retribution.manager.BuildConfig
import app.retribution.manager.R
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.installer.step.Step
import app.retribution.manager.installer.step.StepGroup
import app.retribution.manager.installer.step.StepRunner
import app.retribution.manager.installer.step.download.DownloadBaseStep
import app.retribution.manager.installer.utils.ArscUtil
import app.retribution.manager.installer.utils.ArscUtil.addColorResource
import app.retribution.manager.installer.utils.ArscUtil.addDrawableResource
import app.retribution.manager.installer.utils.ArscUtil.getMainArscChunk
import app.retribution.manager.installer.utils.ArscUtil.getPackageChunk
import app.retribution.manager.installer.utils.ArscUtil.getResourceFileName
import app.retribution.manager.installer.utils.AxmlUtil
import app.retribution.manager.utils.DiscordVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Replaces the existing app icons with the Retribution icon
 */
class ReplaceIconStep : Step() {

    private val preferences: PreferenceManager by inject()

    val context: Context by inject()

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_change_icon

    override suspend fun run(runner: StepRunner) {
        val baseApk = runner.getCompletedStep<DownloadBaseStep>().workingCopy

        runner.logger.i("Reading resources.arsc")
        val arsc = ArscUtil.readArsc(baseApk)

        val iconRscIds = AxmlUtil.readManifestIconInfo(baseApk)
        val squareIconFile = arsc.getMainArscChunk().getResourceFileName(iconRscIds.squareIcon, "anydpi-v26")
        val roundIconFile = arsc.getMainArscChunk().getResourceFileName(iconRscIds.roundIcon, "anydpi-v26")

        runner.logger.i("Patching icon assets (squareIcon=$squareIconFile, roundIcon=$roundIconFile)")

        val iconBytes = try {
            loadIcon()
        } catch (t: Throwable) {
            runner.logger.i("Failed to load Retribution icon, falling back to color: ${t.message}")
            null
        }

        val (background, foreground) = if (iconBytes != null) {
            // Add the icon as a drawable resource and use a transparent foreground
            val iconDrawable = arsc.addDrawableResource(
                name = "retribution_icon",
                path = "res/drawable/retribution_icon.png",
            )
            val transparent = arsc.getPackageChunk().addColorResource("retribution_transparent", Color(0x00000000))

            // Write the icon into the APK as a drawable at the max adaptive icon size
            val drawablePath = "res/drawable/retribution_icon.png"
            val hasDrawable = ZipReader(baseApk).use { drawablePath in it.entryNames }
            val drawableBytes = scaleIcon(iconBytes, DRAWABLE_ICON_SIZE)
            ZipWriter(baseApk, /* append = */ true).use {
                if (hasDrawable) it.deleteEntry(drawablePath)
                it.writeEntry(drawablePath, drawableBytes)
            }

            // Replace all mipmap PNGs with scaled versions of the icon
            replaceMipmapIcons(baseApk, iconBytes)

            iconDrawable to transparent
        } else {
            // Fallback to the old color-only icon
            val color = arsc.getPackageChunk().addColorResource("brand", Color(BuildConfig.MODDED_APP_ICON))
            color to null
        }

        val postfix = when (preferences.channel) {
            DiscordVersion.Type.BETA -> "beta"
            DiscordVersion.Type.ALPHA -> "canary"
            else -> null
        }

        for (rscFile in setOf(squareIconFile, roundIconFile)) { // setOf to not possibly patch same file twice
            val referencePath = if (postfix == null) rscFile else {
                rscFile.replace("_$postfix.xml", ".xml")
            }

            runner.logger.i("Patching adaptive icon ($rscFile <- $referencePath)")

            AxmlUtil.patchAdaptiveIcon(
                apk = baseApk,
                resourcePath = rscFile,
                referencePath = referencePath,
                backgroundColor = background,
                foregroundIcon = foreground,
            )
        }

        runner.logger.i("Writing and compiling resources.arsc")
        ZipWriter(baseApk, /* append = */ true).use {
            it.deleteEntry("resources.arsc")
            it.writeEntry("resources.arsc", arsc.toByteArray())
        }
    }

    private fun scaleIcon(iconBytes: ByteArray, targetSize: Int): ByteArray {
        // 108dp adaptive icon with a 72dp safe-zone foreground
        val safeZoneIconSize = (targetSize * 72 / 108).coerceAtLeast(1)

        // Decode the original bounds first to pick a good inSampleSize
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size, boundsOptions)
        val (srcWidth, srcHeight) = boundsOptions.run { outWidth to outHeight }

        var inSampleSize = 1
        while (srcWidth / (inSampleSize * 2) >= safeZoneIconSize && srcHeight / (inSampleSize * 2) >= safeZoneIconSize) {
            inSampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val sampled = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size, decodeOptions)
            ?: error("Failed to decode Retribution icon")

        val icon = Bitmap.createScaledBitmap(sampled, safeZoneIconSize, safeZoneIconSize, true)
        val canvas = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        Canvas(canvas).drawBitmap(
            icon,
            (targetSize - icon.width) / 2f,
            (targetSize - icon.height) / 2f,
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )

        val out = ByteArrayOutputStream().use { stream ->
            canvas.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }

        canvas.recycle()
        icon.recycle()
        sampled.recycle()
        return out
    }

    private suspend fun loadIcon(): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Try the bundled asset first; this is more reliable than a network download.
            context.assets.open("retribution_icon.png").use { return@withContext it.readBytes() }
        } catch (t: Throwable) {
            // Fallback to the remote URL if the asset is missing or fails to load.
            val url = URL(BuildConfig.MODDED_APP_ICON_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
            connection.connectTimeout = 30_000
            connection.readTimeout = 60_000
            connection.instanceFollowRedirects = true

            connection.inputStream.use { return@withContext it.readBytes() }.also {
                connection.disconnect()
            }
        }
    }

    private fun replaceMipmapIcons(baseApk: File, iconBytes: ByteArray) {
        // 108dp adaptive-icon canvas with 72dp safe-zone content
        val densitySizes = mapOf(
            "mipmap-ldpi" to 81,
            "mipmap-mdpi" to 108,
            "mipmap-hdpi" to 162,
            "mipmap-xhdpi" to 216,
            "mipmap-xxhdpi" to 324,
            "mipmap-xxxhdpi" to 432,
        )

        val mipmapEntries = ZipReader(baseApk).use { zip ->
            zip.entryNames.filter { it.startsWith("res/mipmap") && it.endsWith(".png") }
        }

        val iconBySize = mutableMapOf<Int, ByteArray>()

        ZipWriter(baseApk, /* append = */ true).use { zip ->
            for (entryName in mipmapEntries) {
                val size = densitySizes.entries.find { entryName.contains(it.key) }?.value ?: 192

                val scaledBytes = iconBySize.getOrPut(size) {
                    scaleIcon(iconBytes, size)
                }

                zip.deleteEntry(entryName)
                zip.writeEntry(entryName, scaledBytes)
            }
        }
    }

    private companion object {
        const val DRAWABLE_ICON_SIZE = 432
    }
}
