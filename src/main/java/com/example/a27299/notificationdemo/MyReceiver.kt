package com.example.a27299.notificationdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, intent?.action ?: "null", Toast.LENGTH_SHORT).show()
        val mIntent = Intent(context, MyService::class.java)
        intent?.let {
            when (it.action) {
                Intent.ACTION_BOOT_COMPLETED -> {
                    context?.startService(mIntent)
                    Toast.makeText(context, "开机", Toast.LENGTH_SHORT).show()
                    Log.d("mReceiver", "ACTION_BOOT_COMPLETED")
                }

                else -> {
                    Toast.makeText(context, "其他", Toast.LENGTH_SHORT).show()
                    context?.startService(mIntent)

                    Log.d("mReceiver", "Else")
                }
            }
        }
    }
}