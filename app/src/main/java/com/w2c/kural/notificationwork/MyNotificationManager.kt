package com.w2c.kural.notificationwork

import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.w2c.kural.R
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.utils.NUMBER
import com.w2c.kural.view.fragment.KuralDetailsArgs
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
                channelId,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val kuralNo = String.format(context.getString(R.string.kural_no), kural.number)
        val notification = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(context.getString(R.string.daily_one_kural))
            setContentText("$kuralNo\n${kural.line1}")
            setStyle(
                NotificationCompat.BigTextStyle().bigText("$kuralNo\n${kural.tamilTranslation}")
            )
            setSmallIcon(R.drawable.ic_notification)
            setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            color = context.getColor(androidx.core.R.color.notification_icon_bg_color)
            setTicker(context.getString(R.string.new_notification_from_kural))
            setAutoCancel(true)
            setDefaults(Notification.DEFAULT_SOUND)
            setContentIntent(getPendingIntent(context, kural.number))
        }
        notificationManager.notify(111, notification.build())
    }

    private fun getPendingIntent(context: Context, number: Int): PendingIntent {
        val bundle = Bundle().apply {
            putInt(NUMBER, number)
        }
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.my_navigation)
            .addDestination(R.id.kuralDetails)
            .setArguments(bundle)
            .createPendingIntent()
    }
}