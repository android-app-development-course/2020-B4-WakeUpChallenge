package com.example.wakeupchallenge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val id = intent?.getIntExtra("id", 0)
        val repeat=intent?.getBooleanExtra("repeat",false)
        val name=intent?.getStringExtra("name")
        val activeIntent = Intent(context, AlarmActiveActivity::class.java)
        activeIntent.putExtra("id",id)
        activeIntent.putExtra("repeat",repeat)
        activeIntent.putExtra("name",name)
        activeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Log.d("testOnReceive", "action:$action, id:$id")
        context?.startActivity(activeIntent)
    }
}