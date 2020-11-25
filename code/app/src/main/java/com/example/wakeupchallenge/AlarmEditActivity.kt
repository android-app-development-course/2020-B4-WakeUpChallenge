package com.example.wakeupchallenge

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_alarm_edit.*
import java.util.*

class AlarmEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_alarm_edit)
        val time=Calendar.getInstance()
        val g=time.get(Calendar.AM_PM)
        var h=time.get(Calendar.HOUR)
        if (g==Calendar.PM)
            h+=12
        val m=time.get(Calendar.MINUTE)
        initTimePicker(h,m)
//        ll.setOnClickListener{
//            Log.d("bbb", "aaa")
//
//        }
        cancelBtn.setOnClickListener{
            val ret=Intent()
            setResult(Activity.RESULT_CANCELED,ret)
            finish()
        }
        okBtn.setOnClickListener{

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