package app.retribution.manager.ui.widgets.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.retribution.manager.R
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.utils.DiscordVersion

@Composable
fun InstallChooserDialog(
    latestVersion: DiscordVersion,
    installableVersions: List<Pair<String, DiscordVersion>>,
    prefs: PreferenceManager,
    onDismiss: () -> Unit,
    onInstall: (version: DiscordVersion, packageName: String?, appName: String?) -> Unit
) {
    val oldVersions = installableVersions.filter { it.second < DiscordVersion(341, 0, DiscordVersion.Type.STABLE) }
    val newVersions = installableVersions.filter { it.second >= DiscordVersion(341, 0, DiscordVersion.Type.STABLE) }

    fun choose(version: DiscordVersion, pkg: String?, name: String?) {
        onInstall(version, pkg, name)
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.title_choose_install)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.msg_install_chooser),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                Button(
                    onClick = { choose(latestVersion, null, null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(stringResource(R.string.install_classic_new))
                        Text(
                            text = stringResource(R.string.channel_stable),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                val old = oldVersions.firstOrNull()
                if (old != null) {
                    Button(
                        onClick = { choose(old.second, null, null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(stringResource(R.string.install_classic_old))
                            Text(
                                text = old.first,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                val nextPkg = prefs.packageName + ".next"
                val nextName = prefs.appName + " Next"
                Button(
                    onClick = { choose(latestVersion, nextPkg, nextName) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(stringResource(R.string.install_next))
                        Text(
                            text = nextPkg,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    )
}
