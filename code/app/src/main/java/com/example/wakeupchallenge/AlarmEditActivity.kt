package com.example.wakeupchallenge

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_alarm_edit.*
import java.util.*

class AlarmEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_alarm_edit)
        val inBun=intent.extras

        var h:Int
        var m:Int
        var name="闹钟"
        var music="铃声"

        if(inBun?.getString("ACTION")=="CREAT"){
            nameEdit.setText(name.toCharArray(),0,name.length)
            musicName.setText(music.toCharArray(),0,music.length)
            delBtn.visibility= View.INVISIBLE
            val time=Calendar.getInstance()
            val g=time.get(Calendar.AM_PM)
            h=time.get(Calendar.HOUR)
            if (g==Calendar.PM)
                h+=12
            m=time.get(Calendar.MINUTE)
            repeatSwitch.isChecked=false
        }
        else{
            h=inBun!!.getInt("hour")
            m=inBun.getInt("min")
            name = inBun.getString("name")!!
            nameEdit.setText(name.toCharArray(),0,name.length)
            repeatSwitch.isChecked=inBun.getBoolean("repeat")
        }

        initTimePicker(h,m)
//        ll.setOnClickListener{
//            Log.d("bbb", "aaa")
//
//        }

        val ret=Intent()
        val retBun=Bundle()

        cancelBtn.setOnClickListener{
            setResult(Activity.RESULT_CANCELED,ret)
            finish()
        }
        okBtn.setOnClickListener{
            retBun.putInt("hour",hourPick.value)
            retBun.putInt("min",minutePick.value)
            retBun.putBoolean("repeat",repeatSwitch.isChecked)
            retBun.putString("name",nameEdit.text.toString())
            retBun.putString("IS_DEL","FALSE")

            ret.putExtras(retBun)
            setResult(Activity.RESULT_OK,ret)
            finish()
        }
        delBtn.setOnClickListener{
            retBun.putString("IS_DEL","TRUE")

            ret.putExtras(retBun)
            setResult(Activity.RESULT_OK,ret)
            finish()
        }
    }
    private fun initTimePicker(h:Int,m:Int){
        hourPick.maxValue=23
        minutePick.maxValue=59
        hourPick.descendantFocusability= NumberPicker.FOCUS_BLOCK_DESCENDANTS
        minutePick.descendantFocusability= NumberPicker.FOCUS_BLOCK_DESCENDANTS
        hourPick.value=h
        minutePick.value=m
    }
}