package com.example.wakeupchallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_alarm_game.*

class AlarmGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_game)
        webView.settings.setJavaScriptEnabled(true)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/index.html");//加载asset文件夹下html
    }
}