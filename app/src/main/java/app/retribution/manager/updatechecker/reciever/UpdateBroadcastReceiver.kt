package app.retribution.manager.updatechecker.reciever

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import app.retribution.manager.R
import app.retribution.manager.ui.activity.InstallActivity
import app.retribution.manager.utils.Channels
import app.retribution.manager.utils.DiscordVersion
import app.retribution.manager.utils.Intents

class UpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val versionCode = intent.getStringExtra(Intents.Extras.VERSION) ?: return
        val version = DiscordVersion.fromVersionCode(versionCode) ?: return

        val notificationId = versionCode.toInt()

        val clickIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, InstallActivity::class.java).apply {
                action = Intents.Actions.INSTALL
                putExtra(Intents.Extras.VERSION, versionCode)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        NotificationCompat.Builder(context, Channels.UPDATE).apply {
            setSmallIcon(R.drawable.ic_update)
            setContentTitle(context.getString(R.string.title_update_available))
            setContentText(context.getString(R.string.action_tap_to_install, version.toString()))
            setContentIntent(clickIntent)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT

            nm.notify(notificationId, build())
        }
    }

}