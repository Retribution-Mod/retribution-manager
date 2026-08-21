package app.retribution.manager.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import app.retribution.manager.ui.screen.installer.InstallerScreen
import app.retribution.manager.ui.theme.RetributionManagerTheme
import app.retribution.manager.utils.DiscordVersion
import app.retribution.manager.utils.Intents
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

class InstallActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val version = when (intent?.action) {
            Intents.Actions.INSTALL -> intent.getStringExtra(Intents.Extras.VERSION)
            else -> null
        }
        val customUrl = when (intent?.action) {
            Intents.Actions.INSTALL -> intent.getStringExtra(Intents.Extras.URL)
            else -> null
        }

        val initialScreen = version?.let {
            DiscordVersion.fromVersionCode(it)?.let { v -> InstallerScreen(v, customUrl) }
        }

        if (initialScreen == null) {
            finish()
            return
        }

        setContent {
            RetributionManagerTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Navigator(initialScreen) {
                        SlideTransition(it)
                    }
                }
            }
        }
    }

}
