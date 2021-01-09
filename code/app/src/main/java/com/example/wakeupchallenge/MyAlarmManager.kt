package com.example.wakeupchallenge

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import java.util.*

object MyAlarmManager {
    public fun setAlarm(context: Context ,id:Int,hour:Int,minute:Int,repeat:Boolean){
        Log.d("context", "setAlarm: ${context.toString()}")
        //intent.action中的内容是自定义的
        //不同闹钟对应不同定时器，应对应不同的requestCode。否则只有最后一个定时器生效。
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "activity.AlarmActive.SINGLE_ACTION"
        intent.putExtra("id", id)
        intent.putExtra("repeat",repeat)
        val pending = PendingIntent.getBroadcast(context, id, intent, 0)

        //firstTime:获取当前系统时间
        //systemTime:获取 从(UTC +0)1970年1月1日00:00开始经过的毫秒数。
        val firstTime = SystemClock.elapsedRealtime()
        val systemTime = System.currentTimeMillis()

        //设置闹钟：设定需要设置几点几分几秒的闹钟
        //时区需要设置为+8，不然会有时差。
        //注意HOUR_OF_DAY才是24小时制，HOUR是12小时制。
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8"); //这里时区需要设置一下，不然会有8个小时的时间差
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        var selectTime = calendar.timeInMillis

        //  如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        //time:计算现在时间到设定时间的时间差
        //my_time:系统当前的时间+时间差
        var time = selectTime - systemTime
        var my_Time = firstTime + time

        //测试各个时间数据是否正常
        Log.d("testTime", "firstTime:$firstTime")
        Log.d("testTime", "systemTime:$systemTime")
        Log.d("testTime", "selectTime:$selectTime")
        Log.d("testTime", "time:$time")
        Log.d("testTime", "my_Time:$my_Time")

        //请把firstTime+10000更改为my_Time，否则是设置10s后的闹钟
        //进行闹铃注册，每天重复
        var am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if(repeat)
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, my_Time, AlarmManager.INTERVAL_DAY, pending)
        else
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, my_Time, pending)
//        Toast.makeText(this, "设置成功,$my_Time", Toast.LENGTH_SHORT).show()
    }

    public fun resetAlarm(context: Context,id:Int){
        Log.d("context", "resetAlarm: ${context.toString()}")
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.setAction("activity.AlarmActive.SINGLE_ACTION")
        intent.putExtra("id",id)
        val pending = PendingIntent.getBroadcast(context, id, intent, 0)
        am.cancel(pending)
        Log.d("resetAlarm", "resetAlarm")
//        Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show()
    }
}