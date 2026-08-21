package app.retribution.manager.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.retribution.manager.BuildConfig
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

        val initialScreen = when {
            deepLink?.scheme == "manager" && deepLink.host == "bundle" -> {
                val version = deepLink.path?.trim('/')?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                version?.let { DiscordVersion.fromVersionCode(it)?.let { v -> InstallerScreen(v, null) } } ?: HomeScreen()
            }

            deepLink != null && isModdedDiscordDeepLink(deepLink) -> {
                // The actual prompt is handled below in setContent
                HomeScreen()
            }

            else -> {
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

                version?.let { DiscordVersion.fromVersionCode(it)?.let { v -> InstallerScreen(v, customUrl) } } ?: HomeScreen()
            }
        }

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                0
            )
        }

        setContent {
            val previewUri = remember { deepLink }
            val showDeepLinkDialog = remember { mutableStateOf(previewUri != null && isModdedDiscordDeepLink(previewUri)) }

            RetributionManagerTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Navigator(initialScreen) {
                        SlideTransition(it)
                    }

                    if (showDeepLinkDialog.value && previewUri != null) {
                        val host = previewUri.host ?: previewUri.scheme
                        val url = previewUri.getQueryParameter("url") ?: previewUri.toString()

                        AlertDialog(
                            onDismissRequest = { finish() },
                            title = { Text("Open in modded Discord?") },
                            text = { Text("A $host deep link was received.\n\n$url") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDeepLinkDialog.value = false
                                    forwardToModdedDiscord(previewUri)
                                }) {
                                    Text("Open")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { finish() }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun isModdedDiscordDeepLink(uri: android.net.Uri): Boolean {
        return when (uri.scheme) {
            "plugin", "theme", "font" -> true
            "retribution" -> when (uri.host) {
                "bundle", "plugin", "theme", "font" -> true
                else -> false
            }
            else -> false
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
