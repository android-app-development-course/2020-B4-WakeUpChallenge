package com.example.gesturedemo

import android.app.Activity
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View

abstract class BaseActivity : Activity() {
    private var mGestureDetector: GestureDetector? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        //1 初始化  手势识别器
        mGestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                velocityY: Float
            ): Boolean { // e1: 第一次按下的位置   e2   当手离开屏幕 时的位置  velocityX  沿x 轴的速度  velocityY： 沿Y轴方向的速度
                //判断竖直方向移动的大小
                if (Math.abs(e1.rawY - e2.rawY) > 100) {
                    //Toast.makeText(getApplicationContext(), "动作不合法", 0).show();
                    return true
                }
                if (Math.abs(velocityX) < 150) {
                    //Toast.makeText(getApplicationContext(), "移动的太慢", 0).show();
                    return true
                }
                if (e1.rawX - e2.rawX > 200) { // 表示 向右滑动表示下一页
                    //显示下一页
                    next(null)
                    return true
                }
                if (e2.rawX - e1.rawX > 200) {  //向左滑动 表示 上一页
                    //显示上一页
                    pre(null)
                    return true //消费掉当前事件  不让当前事件继续向下传递
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }

    /**
     * 下一个页面
     * @param view
     */
    abstract fun next(view: View?)

    /**
     * 上一个页面
     * @param view
     */
    abstract fun pre(view: View?)

    //重写activity的触摸事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //2.让手势识别器生效
        mGestureDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}