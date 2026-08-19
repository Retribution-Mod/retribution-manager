package app.retribution.manager.installer.step.patching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import app.retribution.manager.R
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.installer.step.Step
import app.retribution.manager.installer.step.StepGroup
import app.retribution.manager.installer.step.StepRunner
import app.retribution.manager.installer.step.download.DownloadBaseStep
import app.retribution.manager.installer.utils.ArscUtil
import app.retribution.manager.installer.utils.ArscUtil.addColorResource
import app.retribution.manager.installer.utils.ArscUtil.getPackageChunk
import app.retribution.manager.installer.utils.ArscUtil.getMainArscChunk
import app.retribution.manager.installer.utils.ArscUtil.getResourceFileName
import app.retribution.manager.installer.utils.AxmlUtil
import app.retribution.manager.utils.DiscordVersion
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream
import java.io.File

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

        val (fgBytes, bgBytes) = try {
            val fg = context.assets.open("retribution_fg.png").use { it.readBytes() }
            val bg = context.assets.open("retribution_bg.png").use { it.readBytes() }
            runner.logger.i("Loaded Retribution icon layers successfully")
            fg to bg
        } catch (t: Throwable) {
            runner.logger.e("Failed to load Retribution icon layers: ${t.stackTraceToString()}")
            throw t
        }

        // Add dark red background color resource to ARSC
        val backgroundColor = arsc.getPackageChunk().addColorResource(
            name = "retribution_icon_bg",
            color = Color(0xFF150202)
        )

        // Process and scale foreground image for adaptive safe zone (108dp / 432px)
        val formattedFgBytes = scaleForeground(fgBytes, DRAWABLE_ICON_SIZE)

        // Overwrite standard Discord foreground drawables directly inside the ZIP
        val existingForegrounds = ZipReader(baseApk).use { zip ->
            zip.entryNames.filter { 
                (it.contains("ic_launcher") || it.contains("ic_brand")) && 
                it.contains("foreground") && 
                it.endsWith(".png") 
            }
        }

        ZipWriter(baseApk, /* append = */ true).use { zip ->
            for (fgPath in existingForegrounds) {
                runner.logger.i("Replacing foreground asset: $fgPath")
                zip.deleteEntry(fgPath)
                zip.writeEntry(fgPath, formattedFgBytes)
            }
        }

        // Overwrite legacy mipmap icons with fully composited FG + BG PNGs
        replaceMipmapIcons(baseApk, fgBytes, bgBytes)

        val postfix = when (preferences.channel) {
            DiscordVersion.Type.BETA -> "beta"
            DiscordVersion.Type.ALPHA -> "canary"
            else -> null
        }

        for (rscFile in setOf(squareIconFile, roundIconFile)) {
            val referencePath = if (postfix == null) rscFile else rscFile.replace("_$postfix.xml", ".xml")
            runner.logger.i("Patching adaptive icon ($rscFile <- $referencePath)")

            AxmlUtil.patchAdaptiveIcon(
                apk = baseApk,
                resourcePath = rscFile,
                referencePath = referencePath,
                backgroundColor = backgroundColor
            )
        }

        runner.logger.i("Writing and compiling resources.arsc")
        ZipWriter(baseApk, /* append = */ true).use {
            it.deleteEntry("resources.arsc")
            it.writeEntry("resources.arsc", arsc.toByteArray())
        }
    }

    private fun scaleForeground(fgBytes: ByteArray, targetSize: Int): ByteArray {
        val safeZoneSize = (targetSize * 66 / 108).coerceAtLeast(1)
        val sampled = decodeBitmap(fgBytes)
        val icon = Bitmap.createScaledBitmap(sampled, safeZoneSize, safeZoneSize, true)

        val canvasBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawBitmap(
            icon,
            (targetSize - icon.width) / 2f,
            (targetSize - icon.height) / 2f,
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )

        val out = compressPng(canvasBitmap)
        canvasBitmap.recycle()
        icon.recycle()
        sampled.recycle()
        return out
    }

    private fun createCompositedIcon(fgBytes: ByteArray, bgBytes: ByteArray, targetSize: Int): ByteArray {
        val safeZoneSize = (targetSize * 66 / 108).coerceAtLeast(1)
        
        val bgSampled = decodeBitmap(bgBytes)
        val bgScaled = Bitmap.createScaledBitmap(bgSampled, targetSize, targetSize, true)

        val fgSampled = decodeBitmap(fgBytes)
        val fgScaled = Bitmap.createScaledBitmap(fgSampled, safeZoneSize, safeZoneSize, true)

        val canvasBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvas.drawBitmap(bgScaled, 0f, 0f, paint)
        canvas.drawBitmap(
            fgScaled,
            (targetSize - fgScaled.width) / 2f,
            (targetSize - fgScaled.height) / 2f,
            paint
        )

        val out = compressPng(canvasBitmap)
        canvasBitmap.recycle()
        bgScaled.recycle()
        bgSampled.recycle()
        fgScaled.recycle()
        fgSampled.recycle()
        return out
    }

    private fun decodeBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Failed to decode image bytes")
    }

    private fun compressPng(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
    }

    private fun replaceMipmapIcons(baseApk: File, fgBytes: ByteArray, bgBytes: ByteArray) {
        val densitySizes = mapOf(
            "mipmap-ldpi" to 81,
            "mipmap-mdpi" to 108,
            "mipmap-hdpi" to 162,
            "mipmap-xhdpi" to 216,
            "mipmap-xxhdpi" to 324,
            "mipmap-xxxhdpi" to 432
        )

        val mipmapEntries = ZipReader(baseApk).use { zip ->
            zip.entryNames.filter { it.startsWith("res/mipmap") && it.endsWith(".png") }
        }

        val iconBySize = mutableMapOf<Int, ByteArray>()

        ZipWriter(baseApk, /* append = */ true).use { zip ->
            for (entryName in mipmapEntries) {
                val size = densitySizes.entries.find { entryName.contains(it.key) }?.value ?: 192

                val compositedBytes = iconBySize.getOrPut(size) {
                    createCompositedIcon(fgBytes, bgBytes, size)
                }

                zip.deleteEntry(entryName)
                zip.writeEntry(entryName, compositedBytes)
            }
        }
    }

    private companion object {
        const val DRAWABLE_ICON_SIZE = 432
    }
}

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

        val (fgBytes, bgBytes) = try {
            val fg = fetchRemoteOrAsset("retribution_fg.png", FOREGROUND_URL)
            val bg = fetchRemoteOrAsset("retribution_bg.png", BACKGROUND_URL)
            runner.logger.i("Loaded Retribution icon layers successfully")
            fg to bg
        } catch (t: Throwable) {
            runner.logger.e("Failed to load Retribution icon layers: ${t.stackTraceToString()}")
            throw t
        }

        // Add both layers as drawable resources in ARSC
        val fgDrawable = arsc.addDrawableResource(
            name = "retribution_icon_fg",
            path = "res/drawable/retribution_icon_fg.png"
        )
        val bgDrawable = arsc.addDrawableResource(
            name = "retribution_icon_bg",
            path = "res/drawable/retribution_icon_bg.png"
        )

        // Write both layer PNGs to res/drawable/ inside the target APK
        writeZipEntry(baseApk, "res/drawable/retribution_icon_fg.png", scaleForeground(fgBytes, DRAWABLE_ICON_SIZE))
        writeZipEntry(baseApk, "res/drawable/retribution_icon_bg.png", scaleBackground(bgBytes, DRAWABLE_ICON_SIZE))

        // Replace mipmap fallback icons with composited bitmaps (FG over BG)
        replaceMipmapIcons(baseApk, fgBytes, bgBytes)

        val postfix = when (preferences.channel) {
            DiscordVersion.Type.BETA -> "beta"
            DiscordVersion.Type.ALPHA -> "canary"
            else -> null
        }

        for (rscFile in setOf(squareIconFile, roundIconFile)) {
            val referencePath = if (postfix == null) rscFile else rscFile.replace("_$postfix.xml", ".xml")
            runner.logger.i("Patching adaptive icon ($rscFile <- $referencePath)")

            AxmlUtil.patchAdaptiveIcon(
                apk = baseApk,
                resourcePath = rscFile,
                referencePath = referencePath,
                backgroundColor = bgDrawable,
                foregroundIcon = fgDrawable
            )
        }

        runner.logger.i("Writing and compiling resources.arsc")
        ZipWriter(baseApk, /* append = */ true).use {
            it.deleteEntry("resources.arsc")
            it.writeEntry("resources.arsc", arsc.toByteArray())
        }
    }

    private fun writeZipEntry(apk: File, path: String, bytes: ByteArray) {
        val hasEntry = ZipReader(apk).use { path in it.entryNames }
        ZipWriter(apk, /* append = */ true).use { zip ->
            if (hasEntry) zip.deleteEntry(path)
            zip.writeEntry(path, bytes)
        }
    }

    private fun scaleForeground(fgBytes: ByteArray, targetSize: Int): ByteArray {
        val safeZoneSize = (targetSize * 66 / 108).coerceAtLeast(1)
        val sampled = decodeBitmap(fgBytes)
        val icon = Bitmap.createScaledBitmap(sampled, safeZoneSize, safeZoneSize, true)

        val canvasBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawBitmap(
            icon,
            (targetSize - icon.width) / 2f,
            (targetSize - icon.height) / 2f,
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )

        val out = compressPng(canvasBitmap)
        canvasBitmap.recycle()
        icon.recycle()
        sampled.recycle()
        return out
    }

    private fun scaleBackground(bgBytes: ByteArray, targetSize: Int): ByteArray {
        val sampled = decodeBitmap(bgBytes)
        val icon = Bitmap.createScaledBitmap(sampled, targetSize, targetSize, true)
        val out = compressPng(icon)
        icon.recycle()
        sampled.recycle()
        return out
    }

    private fun createCompositedIcon(fgBytes: ByteArray, bgBytes: ByteArray, targetSize: Int): ByteArray {
        val safeZoneSize = (targetSize * 66 / 108).coerceAtLeast(1)

        val bgSampled = decodeBitmap(bgBytes)
        val bgScaled = Bitmap.createScaledBitmap(bgSampled, targetSize, targetSize, true)

        val fgSampled = decodeBitmap(fgBytes)
        val fgScaled = Bitmap.createScaledBitmap(fgSampled, safeZoneSize, safeZoneSize, true)

        val canvasBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // Draw background texture first
        canvas.drawBitmap(bgScaled, 0f, 0f, paint)
        // Draw centered foreground icon over top
        canvas.drawBitmap(
            fgScaled,
            (targetSize - fgScaled.width) / 2f,
            (targetSize - fgScaled.height) / 2f,
            paint
        )

        val out = compressPng(canvasBitmap)
        canvasBitmap.recycle()
        bgScaled.recycle()
        bgSampled.recycle()
        fgScaled.recycle()
        fgSampled.recycle()
        return out
    }

    private fun decodeBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Failed to decode image bytes")
    }

    private fun compressPng(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
    }

    private fun replaceMipmapIcons(baseApk: File, fgBytes: ByteArray, bgBytes: ByteArray) {
        val densitySizes = mapOf(
            "mipmap-ldpi" to 81,
            "mipmap-mdpi" to 108,
            "mipmap-hdpi" to 162,
            "mipmap-xhdpi" to 216,
            "mipmap-xxhdpi" to 324,
            "mipmap-xxxhdpi" to 432
        )

        val mipmapEntries = ZipReader(baseApk).use { zip ->
            zip.entryNames.filter { it.startsWith("res/mipmap") && it.endsWith(".png") }
        }

        val iconBySize = mutableMapOf<Int, ByteArray>()

        ZipWriter(baseApk, /* append = */ true).use { zip ->
            for (entryName in mipmapEntries) {
                val size = densitySizes.entries.find { entryName.contains(it.key) }?.value ?: 192

                val compositedBytes = iconBySize.getOrPut(size) {
                    createCompositedIcon(fgBytes, bgBytes, size)
                }

                zip.deleteEntry(entryName)
                zip.writeEntry(entryName, compositedBytes)
            }
        }
    }

    private suspend fun fetchRemoteOrAsset(assetName: String, urlString: String): ByteArray = withContext(Dispatchers.IO) {
        try {
            context.assets.open(assetName).use { it.readBytes() }
        } catch (t: Throwable) {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            connection.connectTimeout = 30_000
            connection.readTimeout = 60_000
            connection.instanceFollowRedirects = true

            connection.inputStream.use { stream ->
                stream.readBytes().also { connection.disconnect() }
            }
        }
    }

    private companion object {
        const val DRAWABLE_ICON_SIZE = 432
        const val FOREGROUND_URL = "https://i.allyapp.cc/raw/SovwgtEQ.jpg"
        const val BACKGROUND_URL = "https://i.allyapp.cc/raw/DJHR8x3q.jpg"
    }
}
