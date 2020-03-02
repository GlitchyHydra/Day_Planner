package com.example.weekplanner.services

import android.R
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.weekplanner.activities.MainActivity


class NotifierIntentService {
    private val NOTIFICATION_ID = 3

    /*fun MyNewIntentService() {
        super("MyNewIntentService")
    }

    protected fun onHandleIntent(intent: Intent?) {
        val builder  = Notification.Builder(this, "")
        builder.setContentTitle("My Title")
        builder.setContentText("This is the Body")
        builder.setSmallIcon(R.drawable.whatever)
        val notifyIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent)
        val notificationCompat: Notification = builder.build()
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(NOTIFICATION_ID, notificationCompat)
    }*/
}