package app.retribution.manager.installer.step.installing

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import app.retribution.manager.R
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.installer.step.Step
import app.retribution.manager.installer.step.StepGroup
import app.retribution.manager.installer.step.StepRunner
import org.koin.core.component.inject

/**
 * Opens the All Files Access settings for the patched Discord app, but only if it has not already
 * been granted.
 *
 * This is a non-blocking best-effort step: the install is considered successful even if the user
 * does not grant the permission. In that case the Xposed module falls back to its bundled asset or
 * a network download.
 */
class GrantStoragePermissionStep : Step() {

    private val context: Context by inject()
    private val preferences: PreferenceManager by inject()

    override val group = StepGroup.INSTALLING
    override val nameRes = R.string.step_grant_storage

    override suspend fun run(runner: StepRunner) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            runner.logger.i("All Files Access not needed on API ${Build.VERSION.SDK_INT}")
            return
        }

        val packageName = runner.packageName ?: preferences.packageName

        if (isExternalStorageManager(packageName)) {
            runner.logger.i("MANAGE_EXTERNAL_STORAGE already granted to $packageName; skipping prompt")
            return
        }

        runner.logger.i("Prompting user to grant MANAGE_EXTERNAL_STORAGE to $packageName")

        runCatching {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        }.onFailure {
            runner.logger.e("Failed to open storage permission settings", it)
        }
    }

    private fun isExternalStorageManager(packageName: String): Boolean {
        return runCatching {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

            val op = "android:manage_external_storage"

            // checkOpNoThrow is public since API 29 and returns the mode without side effects.
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.checkOpNoThrow(op, appInfo.uid, packageName)
            } else {
                AppOpsManager::class.java
                    .getMethod("checkOpNoThrow", String::class.java, Int::class.java, String::class.java)
                    .invoke(appOps, op, appInfo.uid, packageName) as Int
            }

            mode == AppOpsManager.MODE_ALLOWED
        }.getOrDefault(false)
    }
}
