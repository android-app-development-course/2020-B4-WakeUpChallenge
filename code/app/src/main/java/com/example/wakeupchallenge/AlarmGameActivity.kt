package com.example.wakeupchallenge

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_alarm_game.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.min

class AlarmGameActivity : AppCompatActivity() {
    val update=1
    private var hasSelected=false
    val handler=object:Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                update->webView.loadUrl("file:///android_asset/index.html")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_game)
        // 若非重复闹钟，更新闹钟信息为关闭
        if(!intent.getBooleanExtra("repeat",false)){
            Log.d("gameOver", "id:${intent.getIntExtra("id",0)},repeat:${intent.getBooleanExtra("repeat",false)}")
            val dbHelper = DatebaseHelper(this, "AlarmStore.db", 1)
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("open",false)
            db.beginTransaction()
            try {
                db.update("Alarm",values,"id=${intent.getIntExtra("id",0)}",null)
                db.setTransactionSuccessful()
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                db.endTransaction()
            }
        }
        webView.settings.setJavaScriptEnabled(true)
        webView.addJavascriptInterface(this,"wv")
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/index.html");//加载asset文件夹下html
    }

    @JavascriptInterface
    public fun createAlertDialog(){//游戏失败创建对话框
        var flag = false
        hasSelected=false
        var aldg=AlertDialog.Builder(this)
        aldg.setTitle("游戏失败:")
        aldg.setMessage("是否立即重试,否则在三分钟后重试")
        aldg.setPositiveButton("确定",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            hasSelected=true
            val msg = Message()
            msg.what=update
            handler.sendMessage(msg)//游戏失败发送消息更新界面重新开始游戏
        })
        aldg.setNegativeButton("取消",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            hasSelected=true
            var cal= Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
            val id=intent.getIntExtra("id",0)
            val name=intent.getStringExtra("name")!!
            var hour=cal.get(Calendar.HOUR_OF_DAY)
            var minute = cal.get(Calendar.MINUTE)
            MyAlarmManager.setAlarm(this,(hour*100+minute+10)*(-1),hour,minute+3,false,name)//设置一个三分钟后的闹钟
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
            finish()
        })
        aldg.create().show()
        val timer=Timer()
        val context=this
        timer.schedule(timerTask {
            if (!hasSelected){
                var cal= Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
                val id=intent.getIntExtra("id",0)
                val name=intent.getStringExtra("name")!!
                var hour=cal.get(Calendar.HOUR_OF_DAY)
                var minute = cal.get(Calendar.MINUTE)
                MyAlarmManager.setAlarm(context,(hour*100+minute+10)*(-1),hour,minute+3,false,name)//设置一个三分钟后的闹钟
                finish()
            }
        },10*1000)
    }

    @JavascriptInterface//游戏成功时调用
    public fun ToMainActivity(){
        Log.d("gameWin", "Win")
        finish()
//        val intent = Intent(this,MainActivity::class.java)
//        startActivity(intent)
    }

}