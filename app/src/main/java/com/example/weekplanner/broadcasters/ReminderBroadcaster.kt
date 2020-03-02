package com.example.weekplanner.broadcasters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weekplanner.R
import com.example.weekplanner.activities.AddingActivity
import com.example.weekplanner.activities.MainActivity
import com.example.weekplanner.services.NotifierIntentService

class ReminderBroadcaster : BroadcastReceiver() {

    private var idOfNotification = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {

            /*val sdkVersionCheck = intent
                ?.getBooleanExtra(SDK_VERSION_CHECK, false) ?: false*/

            //if (sdkVersionCheck) {
            val title = intent?.getStringExtra(AddingActivity.EXTRA_TITLE) ?: ""
            val date = intent?.getStringExtra(MainActivity.EXTRA_CHECK_DAY) ?: ""

            val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_access_alarm)
                .setContentTitle(date)
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            val notificationManager = NotificationManagerCompat.from(context)

            notificationManager.notify(idOfNotification++, builder.build())
            /* } else {
                 val intent1 = Intent(context, NotifierIntentService::class.java)
                 context.startService(intent1)
             }*/

        }
    }

}