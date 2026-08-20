package app.retribution.manager.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.retribution.manager.BuildConfig
// import app.retribution.manager.R
import app.retribution.manager.ui.screen.home.HomeScreen
import app.retribution.manager.ui.screen.installer.InstallerScreen
import app.retribution.manager.ui.theme.RetributionManagerTheme
import app.retribution.manager.utils.DiscordVersion
import app.retribution.manager.utils.Intents
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val deepLink = if (intent?.action == Intent.ACTION_VIEW) intent.data else null

        when (deepLink?.scheme) {
            "manager" -> {
                if (deepLink.host == "bundle") {
                    val version = deepLink.path?.trim('/')?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                    if (version != null) {
                        val screen = DiscordVersion.fromVersionCode(version)?.let {
                            InstallerScreen(it, null)
                        } ?: HomeScreen()
                        setContent {
                            RetributionManagerTheme {
                                Navigator(screen) {
                                    SlideTransition(it)
                                }
                            }
                        }
                    } else {
                        finish()
                    }
                    return
                }
            }
            "plugin", "theme", "font" -> {
                forwardToModdedDiscord(deepLink)
                return
            }
            "retribution" -> {
                when (deepLink.host) {
                    "bundle", "plugin", "theme", "font" -> {
                        forwardToModdedDiscord(deepLink)
                        return
                    }
                }
            }
        }

        val version = when (intent?.action) {
            Intents.Actions.INSTALL -> intent.getStringExtra(Intents.Extras.VERSION)
            Intent.ACTION_VIEW -> deepLink?.getQueryParameter("version")
            else -> null
        }
        val customUrl = when (intent?.action) {
            Intents.Actions.INSTALL -> intent.getStringExtra(Intents.Extras.URL)
            Intent.ACTION_VIEW -> deepLink?.getQueryParameter("url")
            else -> null
        }

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                0
            )
        }

        val screen = if (version != null) {
            DiscordVersion.fromVersionCode(version)?.let { InstallerScreen(it, customUrl) } ?: HomeScreen()
        } else {
            HomeScreen()
        }

        setContent {
            RetributionManagerTheme {
                Navigator(screen) {
                    SlideTransition(it)
                }
            }
        }
    }

    private fun forwardToModdedDiscord(deepLink: android.net.Uri) {
        val launch = packageManager.getLaunchIntentForPackage(BuildConfig.MODDED_APP_PACKAGE_NAME)
        if (launch != null) {
            launch.data = deepLink
            startActivity(launch)
        } else {
            Toast.makeText(this, "Modded Discord not installed. Install it through the manager first.", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}
