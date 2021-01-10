package com.example.wakeupchallenge


import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator

import android.view.View


import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.gesturedemo.BaseActivity
import kotlinx.android.synthetic.main.activity_alarm_active.*
import java.util.*
import java.util.jar.Manifest

class AlarmActiveActivity:BaseActivity() {
    private val mediaPlayer = MediaPlayer()
    private lateinit var vibrator:Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_active)
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.VIBRATE),1)
        }else{
            startvibrator()
        }
        var imageView=findViewById(R.id.imageView) as ImageView
        Glide.with(this).load(R.drawable.fire).into(imageView)

        var cal=Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
        var year = cal.get(Calendar.YEAR).toString()
        var month = (cal.get(Calendar.MONDAY)+1).toString()
        var day= cal.get(Calendar.DATE).toString()
        var time1=year+'年'+month+'月'+day+'日'
        var hour=cal.get(Calendar.HOUR_OF_DAY)
        var minute = cal.get(Calendar.MINUTE)
        var time2=hour.toString()+':'+minute
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
        val intent = Intent(this,AlarmGameActivity::class.java)
        intent.putExtra("id",this.intent.getIntExtra("id",0))
        intent.putExtra("repeat",this.intent.getBooleanExtra("repeat",false))
        startActivity(intent)
        overridePendingTransition(R.anim.activity_right_to_left_enter, R.anim.activity_right_to_left_exit)
    }

    override fun pre(view: View?) {
        //继承自父类的左滑
        val intent = Intent(this,AlarmGameActivity::class.java)
        intent.putExtra("repeat",this.intent.getBooleanExtra("repeat",false))
        intent.putExtra("id",this.intent.getIntExtra("id",-1))
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
        vibrator.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startvibrator()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun startvibrator(){
        vibrator=this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        var patter=longArrayOf(1000,1000,2000,50)
        vibrator.vibrate(patter,0)
    }
}