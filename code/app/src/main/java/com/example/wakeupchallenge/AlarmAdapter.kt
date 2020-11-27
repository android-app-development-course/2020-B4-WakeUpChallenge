package com.example.wakeupchallenge

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarmlist_listview.view.*

class AlarmAdapter (activity:Activity, val resourceID:Int, data:List<AlarmInfo>):
ArrayAdapter<AlarmInfo>(activity, resourceID, data),View.OnClickListener
{
    inner class ViewHolder(val name:TextView, val time:TextView, val repeat:TextView, val switch:SwitchCompat)
    var selectedID = -1
    private lateinit var listener:InnerClickListener

    interface InnerClickListener{
        fun click(v:View?)
    }

    override fun onClick(v: View?) {
        listener.click(v)
    }

    fun setMyListener(listener:InnerClickListener){
        this.listener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val viewHolder:ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceID, parent, false)
            val name: TextView = view.findViewById(R.id.name)
            val time: TextView = view.findViewById(R.id.time)
            val repeat: TextView = view.findViewById(R.id.repeat)
            val switch: SwitchCompat = view.findViewById(R.id.switch1)
            viewHolder = ViewHolder(name, time, repeat, switch)
            view.tag = viewHolder
        }
        else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }


        view.setBackgroundResource(if (selectedID == position) R.color.pink else R.color.white)
        view.switch1.setOnClickListener(this)
        view.switch1.tag = position


        val cur = getItem(position)//获取当前项闹钟的实例
        if (cur != null){
            val timeString = String.format("%02d", cur.hour) + ":" + String.format("%02d", cur.min)
            val repeatString = "闹钟，" + if (cur.repeat) "重复" else "不重复"
            viewHolder.apply {
                name.text = cur.name
                repeat.text = repeatString
                switch.isChecked = cur.open
                time.text = timeString
            }
        }
        return view
    }
}