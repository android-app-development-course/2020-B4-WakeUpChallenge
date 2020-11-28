package com.example.wakeupchallenge

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.gesturedemo.BaseActivity
import kotlinx.android.synthetic.main.activity_alarm_active.*
import java.util.*

class AlarmActiveActivity : BaseActivity() {
    private val mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_active)

        var imageView=findViewById(R.id.imageView) as ImageView
        Glide.with(this).load(R.drawable.fire).into(imageView)

        var cal=Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
        var year = cal.get(Calendar.YEAR).toString()
        var month = (cal.get(Calendar.MONDAY)+1).toString()
        var day= cal.get(Calendar.DATE).toString()
        var time1=year+'年'+month+'月'+day+'日'
        var hour=cal.get(Calendar.HOUR).toString()
        if(cal.get(Calendar.AM_PM)==0){
            hour= (hour+12).toString()
        }
        var minute = cal.get(Calendar.MINUTE)
        var time2=hour+':'+minute
        var week = when(cal.get(Calendar.DAY_OF_WEEK)){
            cal.get(Calendar.MONDAY)->"星期一"
            cal.get(Calendar.TUESDAY)->"星期二"
            cal.get(Calendar.WEDNESDAY)->"星期三"
            cal.get(Calendar.THURSDAY)->"星期四"
            cal.get(Calendar.FEBRUARY)->"星期五"
            cal.get(Calendar.SATURDAY)->"星期六"
            else->"星期日"
        }
        ymdtextView.text=time1
        hmtextView.text=time2
        wtextView.text=week
    }

    override fun onResume() {
        super.onResume()
        initMediaPlayer()
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    override fun next(view: View?) {
        //继承自父类的右滑
        val intent = Intent(this,AlarmGameActivityKT::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_right_to_left_enter, R.anim.activity_right_to_left_exit)
    }

    override fun pre(view: View?) {
        //继承自父类的左滑
        val intent = Intent(this,AlarmGameActivityKT::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_left_to_right_enter, R.anim.activity_left_to_right_exit)
    }

    private fun initMediaPlayer(){
        //初始化音乐播放器
        val assetManager = assets
        val fd = assetManager.openFd("music.mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length)
        mediaPlayer.prepare()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}