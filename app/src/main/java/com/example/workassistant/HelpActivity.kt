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

        webView.webViewClient = object : WebViewClient() {
            // Handle API until level 21
            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                return getNewResponse(url)
            }
            // Handle API 21+
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val url = request.url.toString()
                return getNewResponse(url)
            }
            private fun getNewResponse(url: String): WebResourceResponse? {
                return try {
                    val httpClient = OkHttpClient()
                    val request: Request = Builder()
                        .url(url.trim { it <= ' ' })
                        .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
                        .build()
                    val response: Response = httpClient.newCall(request).execute()
                    WebResourceResponse(
                        null,
                        response.header("content-encoding", "utf-8"),
                        response.body?.byteStream()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }

        webView.loadUrl(apiCurURL + "/pages/newbiebook/")
    }

}