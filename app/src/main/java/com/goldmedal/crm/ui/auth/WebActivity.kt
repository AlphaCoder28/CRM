package com.goldmedal.crm.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.goldmedal.crm.R

class WebActivity :AppCompatActivity() {
    companion object {
        const val PAGE_URL = "pageUrl"
        const val MAX_PROGRESS = 100

        fun start(context: Context, pageUrl: String){
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(PAGE_URL, pageUrl)
            context.startActivity(intent)
        }

    }

    private lateinit var pageUrl: String
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_detail)
        webView = findViewById(R.id.webView)
        // get pageUrl from String
        pageUrl = intent.getStringExtra(PAGE_URL)
            ?: throw IllegalStateException("field $PAGE_URL missing in Intent")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        initWebView()
        setWebClient()
        loadUrl(pageUrl)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.allowFileAccess = true


        webView.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.cancel()
            }
        }
    }
    private fun setWebClient() {
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                webview: WebView,
                url: String?
            ): Boolean {
                val docsUrl = "https://docs.google.com/viewerng/viewer?url="
                webView.loadUrl("$docsUrl$pageUrl")
                return true
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, exit the activity)
        return super.onKeyDown(keyCode, event)
    }


    private fun loadUrl(pageUrl: String) {
        val docsUrl = "https://docs.google.com/viewerng/viewer?url="
        webView.loadUrl("$docsUrl$pageUrl")
    }
}