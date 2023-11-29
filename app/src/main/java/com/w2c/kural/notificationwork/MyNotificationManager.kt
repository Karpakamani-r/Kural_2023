package com.w2c.kural.notificationwork

import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import com.w2c.kural.R
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.utils.IntentKeys
import com.w2c.kural.view.activity.KuralDetails
import java.util.*
import java.util.concurrent.Executors

object MyNotificationManager {
    fun displayNotification(context: Context) {
        val randomKuralNumber = Random().nextInt(1330)
        Executors.newSingleThreadExecutor().execute {
            val kural: Kural? = DatabaseController.getInstance(context).kuralDAO
                .getKural(randomKuralNumber)
            kural?.let {
                showNotification(kural, context)
            }
        }
    }

    private fun showNotification(kural: Kural, context: Context) {
        val channelId = "111"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId, "Thirukkural", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
        notification.setContentTitle("Daily Notification")
        notification.setContentText(kural.tamilTranslation)
        notification.setSmallIcon(R.drawable.notification_icon)
        notification.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        notification.color = context.getColor(androidx.core.R.color.notification_icon_bg_color)
        notification.setTicker("New notification from Thirukkural")
        notification.setAutoCancel(true)
        notification.setDefaults(Notification.DEFAULT_SOUND)
//        val detailIntent = Intent(context, KuralDetails::class.java)
//        detailIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        detailIntent.putExtra(IntentKeys.KURAL_NO, kural.number)
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            detailIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        notification.setContentIntent(pendingIntent)
        notificationManager.notify(111, notification.build())
    }
}