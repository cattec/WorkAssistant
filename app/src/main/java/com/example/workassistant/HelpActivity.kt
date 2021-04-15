package com.example.workassistant

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Request.Builder


class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_screen)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar3)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Справка")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.getSettings().setJavaScriptEnabled(true)

        /*val headers = HashMap<String, String>()
        headers.put("Authorization", myToken.token_type + ' ' + myToken.access_token)
        webView.loadUrl(apiCurURL + "/pages/newbiebook/", headers)*/

        //переопределяем веб клиент с целью добавлять во все запросы наш токен
        webView.webViewClient = newWebViewWithToken()
        webView.loadUrl(apiCurURL + "/pages/newbiebook/")
    }

}