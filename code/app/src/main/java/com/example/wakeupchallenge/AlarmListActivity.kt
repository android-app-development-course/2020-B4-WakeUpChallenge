package com.example.wakeupchallenge

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import kotlinx.android.synthetic.main.activity_alarm_list.*
import kotlinx.android.synthetic.main.alarmlist_toolbar.*
import java.lang.Exception
import java.util.*

class AlarmListActivity : AppCompatActivity(),AlarmAdapter.InnerClickListener {
    private val alarmList = mutableListOf<AlarmInfo>()
    private val RESULT_EDITDEL = 1
    private val RESULT_CREAT = 2
    private lateinit var dbHelper:DatebaseHelper
    private var cacheAlarm:AlarmInfo? = null
    private lateinit var adapter:AlarmAdapter

    override fun click(v: View?) {
        val position = v?.tag
        val id = v?.id
        when(id){
            R.id.switch1 -> {
                val db = dbHelper.writableDatabase
                db.beginTransaction()
                try {
                    val ID = alarmList[position as Int].ID
                    val open = v.findViewById<SwitchCompat>(id).isChecked

                    db.update("Alarm", ContentValues().apply{put("open", open)}, "id = $ID", null)
                    db.setTransactionSuccessful()
                    alarmList[position].open = open
                }catch (e:Exception){
                    //.......................
                }finally {
                    db.endTransaction()
                }


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)
        supportActionBar?.hide()

        dbHelper = DatebaseHelper(this, "AlarmStore.db", 1)
        val db = dbHelper.writableDatabase

        initAlarm(db)

        listView.setOnItemClickListener { parent, view, position, id ->
            adapter.apply {
                selectedID = if (position == selectedID) -1 else position
                notifyDataSetChanged()
            }
            cacheAlarm = if (adapter.selectedID == -1) null else alarmList[position]
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
        }

        add.setOnClickListener{
            val intent = Intent(this, AlarmEditActivity::class.java)
            intent.putExtra("ACTION", "CREAT")
            startActivityForResult(intent, RESULT_CREAT)
        }

        edit.setOnClickListener{
            if (cacheAlarm != null){
                val intent = Intent(this, AlarmEditActivity::class.java)
                val bundle:Bundle = Bundle()
                bundle.putString("ACTION", "EDITDEL")
                cacheAlarm!!.apply {
                    bundle.putString("name", name)
                    bundle.putBoolean("repeat", repeat)
                    bundle.putBoolean("open", open)
                    bundle.putInt("hour", hour)
                    bundle.putInt("min", min)
                    bundle.putString("music", music)

                }
                intent.putExtras(bundle)
                startActivityForResult(intent, RESULT_EDITDEL)
            }
            else{
                val build = AlertDialog.Builder(this)
                build.setMessage("请先选择闹钟！")
                build.setPositiveButton("确认",null)
                build.show()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RESULT_CREAT -> if(resultCode == RESULT_OK){
                val bundle = data?.extras
                val name = bundle?.getString("name", "闹钟")?:"闹钟"
                val repeat=bundle?.getBoolean("repeat", false)?:false
                val open=bundle?.getBoolean("open", true)?:true
                val hour = bundle?.getInt("hour", 0)?:0
                val min = bundle?.getInt("min", 0)?:0
                val music = bundle?.getString("music", "Default")?:"Default"

                Log.d("Database", "$open")
                val values = ContentValues().apply{
                    put("name", name)
                    put("repeat", repeat)
                    put("open", open)
                    put("hour", hour)
                    put("min", min)
                    put("music", music)
                }

                val db = dbHelper.writableDatabase
                val id:Int
                db.beginTransaction()
                try {
                    db.insert("Alarm", null, values)
                    val cursor = db.rawQuery("select last_insert_rowid() from Alarm", null)
                    cursor.moveToFirst();
                    id = cursor.getInt(0)
                    cursor.close()
                    alarmList.add(AlarmInfo(id,name,repeat,open,hour,min,music))
                    adapter.sort(compareBy<AlarmInfo> { it.hour }.thenBy { it.min })
                    db.setTransactionSuccessful()
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    db.endTransaction()
                }
            }

            RESULT_EDITDEL -> if(resultCode == RESULT_OK){
                val bundle = data?.extras
                val DEL = bundle?.getString("IS_DEL")
                if (DEL == "TRUE"){
                    val db = dbHelper.writableDatabase
                    db.beginTransaction()
                    try {
                        db.delete("Alarm", "id = ${cacheAlarm!!.ID}", null)
                        alarmList.removeIf { it.ID == cacheAlarm!!.ID }
                        adapter.selectedID = -1; cacheAlarm = null
                        adapter.notifyDataSetChanged()
                        db.setTransactionSuccessful()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }finally {
                        db.endTransaction()
                    }

                }
                else
                    editOperation(bundle)
            }
        }
    }


    private fun initAlarm(db:SQLiteDatabase){
        alarmList.clear()
        val cursor = db.query("Alarm",null,null,null,null,null,null)
//        Log.d("Datebase", "Line 166")
        if (cursor.moveToFirst()){
            do{

                val ID = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val repeat = cursor.getInt(cursor.getColumnIndex("repeat")) != 0
                val open = cursor.getInt(cursor.getColumnIndex("open")) != 0
                val hour = cursor.getInt(cursor.getColumnIndex("hour"))
                val min = cursor.getInt(cursor.getColumnIndex("min"))
                val music = cursor.getString(cursor.getColumnIndex("music"))
//                Log.d("Datebase", "ID $ID")
//                Log.d("Datebase", "name $name")
//                Log.d("Datebase", "repeat $repeat")
//                Log.d("Datebase", "open $open")
//                Log.d("Datebase", "hour $hour")
//                Log.d("Datebase", "min $min")
//                Log.d("Datebase", "music $music")
                alarmList.add(AlarmInfo(ID, name, repeat, open, hour, min, music))
            }while(cursor.moveToNext())
            cursor.close()
        }
//        Log.d("Database", "Line 181")
        adapter = AlarmAdapter(this, R.layout.alarmlist_listview, alarmList)
        adapter.setMyListener(this)
        adapter.sort(compareBy<AlarmInfo> { it.hour }.thenBy { it.min })
        listView.adapter = adapter

    }

    private fun editOperation(bundle:Bundle?){
        val name = bundle?.getString("name", cacheAlarm!!.name)?:cacheAlarm!!.name
        val repeat = bundle?.getBoolean("repeat", cacheAlarm!!.repeat)?:cacheAlarm!!.repeat
        val open = bundle?.getBoolean("open", cacheAlarm!!.open)?:cacheAlarm!!.open
        val hour = bundle?.getInt("hour", cacheAlarm!!.hour)?:cacheAlarm!!.hour
        val min = bundle?.getInt("min", cacheAlarm!!.min)?:cacheAlarm!!.min
        val music = bundle?.getString("music", cacheAlarm!!.music)?:cacheAlarm!!.music
        
        val values = ContentValues().apply {
            put("name", name)
            put("repeat", repeat)
            put("open", open)
            put("hour", hour)
            put("min", min)
            put("music", music)
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.update("Alarm", values, "id = ${cacheAlarm!!.ID}", null)
            alarmList.find { it.ID == cacheAlarm!!.ID }?.change(values)
            adapter.notifyDataSetChanged()
            db.setTransactionSuccessful()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.endTransaction()
        }
    }
}




data class AlarmInfo(val ID:Int,
                     var name:String,
                     var repeat:Boolean,
                     var open:Boolean,
                     var hour:Int,
                     var min:Int,
                     var music:String
){
    fun change(values: ContentValues){
        name = values.getAsString("name")
        repeat = values.getAsBoolean("repeat")
        open = values.getAsBoolean("open")
        hour = values.getAsInteger("hour")
        min = values.getAsInteger("min")
        music = values.getAsString("music")
    }
}

class DatebaseHelper(val context:Context, name:String, version:Int):
    SQLiteOpenHelper(context, name, null, version){
    private val createAlarm = "create table Alarm("+
            "id integer primary key autoincrement,"+
            "name text,"+
            "repeat integer,"+
            "open integer,"+
            "hour integer,"+
            "min integer," +
            "music text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createAlarm)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}