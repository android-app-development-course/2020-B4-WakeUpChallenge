package com.example.wakeupchallenge

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        testAlarmEdit()
        val intent = Intent(this, AlarmListActivity::class.java)
        startActivity(intent)

    }

    private fun testAlarmEdit(){
        // 测试用
        val bundle=Bundle()
        bundle.putString("ACTION","CREATE")
        bundle.putString("name","test")
        bundle.putInt("hour",12)
        bundle.putInt("min",13)
        bundle.putString("music","testMusic")
        bundle.putBoolean("repeat",false)

        val intent=Intent(this,AlarmEditActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1->if (resultCode== Activity.RESULT_OK){
                val bundle=data?.extras
                if(bundle?.getString("IS_DEL")=="TRUE"){
                    Log.d("ActivityResult", "del")
                    return
                }
                else{
                    val name=bundle?.getString("name")
                    val h=bundle?.getInt("hour")
                    val m=bundle?.getInt("min")
                    val r=bundle?.getBoolean("repeat")
                    Log.d("ActivityResult", name!!)
                    Log.d("ActivityResult",h.toString())
                    Log.d("ActivityResult",m.toString())
                    Log.d("ActivityResult", r.toString())

                }
            }
        }
    }
}