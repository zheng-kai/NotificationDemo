package com.example.a27299.notificationdemo

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {
    private lateinit var manager: NotificationManager
    private val mBinder = MyBinder()
    private var notificationId = 1001
    private val CHANNEL_ID = "service_channel"

    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        GlobalScope.launch {
            var num = 1
            while (true) { // 使用循环模拟通知更新
                manager.notify(notificationId, getNotification(num))
                delay(5000)
                manager.cancel(notificationId)
                num++
            }
        }
    }

    private fun getNotification(num: Int): Notification? {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon)
        val builder = getNotificationBuilder()
        return builder
                .setTicker("Ticker")
                .setContentTitle("ServiceTitle")
                .setContentText("Service $num")
                .setSubText("SubText")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(bitmap)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
                .build()
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "name",
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100L, 200L, 300L)
                description = "description"
                enableLights(true)
                setSound(null, null)
            }
            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.priority = NotificationManager.IMPORTANCE_DEFAULT
            }
            builder.setLights(Color.RED, 1000, 0)
        }
        return builder
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}