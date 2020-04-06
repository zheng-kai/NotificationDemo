package com.example.a27299.notificationdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import android.widget.Toast
import com.igexin.sdk.PushManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var manager: NotificationManager
    private var notification: Notification? = null
    private var notificationCustom: Notification? = null
    private val NOTIFICATION_ID = 3
    private val NOTIFICATION_CUSTOM_ID = 4
    private var receiver: MyReceiver? = null
    private val CHANNEL_ID = "channel4"
    private val NEXT_ACTION = 2
    private val STOP_ACTION = 1
    private val PREV_ACTION = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        register()
        initPushManager()
        val actionType = intent.getIntExtra("action", -1)
        parseAction(actionType)

        btn_start_service.setOnClickListener {
            val mIntent = Intent(this, MyService::class.java)
            application.applicationContext.startService(mIntent)

        }
        btn_show.setOnClickListener {
            show(NOTIFICATION_ID, notification)
        }
        btn_hide.setOnClickListener {
            hide(NOTIFICATION_ID)
        }
        btn_show_custom.setOnClickListener {
            show(NOTIFICATION_CUSTOM_ID, notificationCustom)
        }
        btn_send.setOnClickListener {
            val intentSend = Intent("com.example.a27299.notificationdemo.broadcast1")
            intentSend.component = ComponentName("com.example.a27299.notificationdemo", "com.example.a27299.notificationdemo.broadcast1")
            sendBroadcast(intentSend)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.cancel(NOTIFICATION_CUSTOM_ID)
        unregisterReceiver(receiver)
    }

    private fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.a27299.notificationdemo.broadcast1")
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        receiver = MyReceiver()
        registerReceiver(receiver, intentFilter)
    }

    private fun init() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification = getNotification()
        notificationCustom = getNotificationCustom()
    }

    /**
     * 个推 初始化
     */
    private fun initPushManager() {
        PushManager.getInstance().initialize(this)
    }

    private fun parseAction(action: Int) {
        val str = when (action) {

            NEXT_ACTION -> {
                notificationCustom?.apply {
                    contentView.setTextViewText(R.id.tv_title, "下一首")
                }
                "下一首"
            }
            STOP_ACTION -> {
                notificationCustom?.apply {
                    contentView.setTextViewText(R.id.tv_title, "暂停")
                }
                "暂停"
            }
            PREV_ACTION -> {
                notificationCustom?.apply {
                    contentView.setTextViewText(R.id.tv_title, "上一首")
                }
                "上一首"
            }
            else -> "null"
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

    }

    private fun hide(id: Int) {
        manager.cancel(id)
    }

    private fun show(id: Int, notification: Notification?) {
        manager.notify(id, notification)
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "name",
                    NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100L, 200L, 300L)
                description = "description"
                enableLights(true)
                lightColor = Color.RED
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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

    private fun getNotificationCustom(): Notification? {
        val builder = getNotificationBuilder()
        return builder
                .setTicker("Ticker")
                .setContentTitle("ContentTitle")
                .setContentText("ContentText")
                .setSubText("SubText")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon)
                .setOngoing(true) //不能滑动删除通知
                .setContentIntent(getPendingIntent())
//                .setAutoCancel(true)
                .setCustomContentView(getRemoteView())
                .build()
    }

    private fun getNotification(): Notification? {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon)
        val builder = getNotificationBuilder()
        return builder
                .setTicker("Ticker")
                .setContentTitle("ContentTitle")
                .setContentText("ContentText")
                .setSubText("SubText")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(bitmap)
//                .setOngoing(true) //不能滑动删除通知
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
//                .setCustomContentView(getRemoteView())
                .build()
    }

    private fun getRemoteView(): RemoteViews? {
        val remoteViews = RemoteViews(packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.tv_title, "固定标题")
        remoteViews.setOnClickPendingIntent(R.id.btn_prev, getPendingIntentPrev())
        remoteViews.setOnClickPendingIntent(R.id.btn_stop, getPendingIntentStop())
        remoteViews.setOnClickPendingIntent(R.id.btn_next, getPendingIntentNext())
        return remoteViews
    }

    private fun getPendingIntentNext(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("action", NEXT_ACTION)
        return PendingIntent.getActivity(this, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private fun getPendingIntentPrev(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("action", PREV_ACTION)
        return PendingIntent.getActivity(this, 202, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentStop(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("action", STOP_ACTION)
        return PendingIntent.getActivity(this, 203, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


}
