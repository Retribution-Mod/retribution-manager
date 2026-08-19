package app.retribution.manager.ui.widgets

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import app.retribution.manager.BuildConfig
import app.retribution.manager.R
import app.retribution.manager.utils.DiscordVersion

@Composable
fun AppIcon(
    customIcon: Boolean,
    releaseChannel: DiscordVersion.Type,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageBitmap = remember(customIcon) {
        if (customIcon) {
            try {
                context.assets.open("retribution_icon.png").use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (t: Throwable) {
                null
            }
        } else {
            null
        }
    }

    if (customIcon && imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape)
                .background(Color(0xFF000000))
        )
    } else {
        val iconColor = remember(releaseChannel) {
            when (releaseChannel) {
                DiscordVersion.Type.ALPHA -> Color(BuildConfig.MODDED_APP_ICON_ALPHA)
                else -> Color(BuildConfig.MODDED_APP_ICON_OTHER)
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_discord_icon),
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape)
                .background(iconColor)
        )
    }
}
