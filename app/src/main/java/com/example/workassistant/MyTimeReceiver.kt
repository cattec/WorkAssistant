package com.example.workassistant

import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.*


class MyTimeReceiver : BroadcastReceiver() {

    var NOTIFY_ID: Int = 157
    val CHANNEL_ID : String= "INNC note"
    val CHANEL_NAME = "INNC Work Assistant"
    val CHANEL_DESC = "Оповещение отметка здоровья"
    val CHANEL_TEXT = "Пожулйста заполните ежедневную предрабочую карточку состояния здоровья!"

    override fun onReceive(context: Context, intent: Intent) {
        val msgStr = "Текущее время: " + SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Calendar.getInstance().time)
        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show()
        createINNCNotify(context, msgStr)
    }

    private fun createINNCNotify(context: Context, msgStr : String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val contentIntent = PendingIntent.getActivity(context,
                    0,
                    Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_CANCEL_CURRENT)

                val contentIntentYes = PendingIntent.getActivity(context,
                    0,
                    Intent(context, SupportActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_CANCEL_CURRENT)

                val contentIntentNo = PendingIntent.getActivity(context,
                    0,
                    Intent(context, HelpActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_CANCEL_CURRENT)

                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.arni))
                    .setContentTitle(CHANEL_NAME)
                    .setContentText(CHANEL_TEXT + " " + msgStr)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setColor(Color.GREEN)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(CHANEL_TEXT + " " + msgStr))
                    .setContentIntent(contentIntent)
                    .addAction(R.drawable.ic_baseline_message_24, "Support", contentIntentYes)
                    .addAction(R.drawable.ic_baseline_help_outline_24, "Help", contentIntentNo)

                val channel = NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = CHANEL_DESC
                }
                //val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(NOTIFY_ID++, builder.build())
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}