package mz.example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import hrm.widget.SwipeRefreshLayout

class ExampleActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        webView = findViewById(R.id.webView)

        // إعدادات WebView الأساسية
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        // عند السحب لإعادة التحميل -> فقط reload
        swipeRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                // إذا لم تكن جاهزة للخروج من الـ spinner فابدأ إعادة التحميل
                if (::webView.isInitialized) {
                    webView.reload()
                } else {
                    // تأكد من إيقافه في حال وجود خطأ نادر
                    try { swipeRefresh.setRefreshing(false) } catch (ignored: Exception) {}
                }
            }
        })

        // WebViewClient لتحميل الروابط داخل الـ WebView وإعلامنا ببداية/نهاية التحميل
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.let { view?.loadUrl(it.toString()) }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // بدء الـ spinner عند بداية تحميل الصفحة
                try {
                    swipeRefresh.setRefreshing(true)
                } catch (e: Exception) {
                    // تجاهل أي استثناء (حماية)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // إيقاف الـ spinner عند اكتمال التحميل
                try {
                    swipeRefresh.setRefreshing(false)
                } catch (e: Exception) {
                    // تجاهل
                }
            }
        }

        // WebChromeClient لمزامنة التقدّم (اختياري: إخفاء عندما يصل 100%)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                try {
                    if (newProgress >= 100) {
                        // تحميل تام -> إيقاف spinner
                        swipeRefresh.setRefreshing(false)
                    } else {
                        // أثناء التحميل -> اجعل spinner ظاهراً
                        swipeRefresh.setRefreshing(true)
                    }
                } catch (e: Exception) {
                    // تجاهل
                }
            }
        }

        // تحميل URL افتراضي (أرفع الـ spinner قبل التحميل الأولي)
        val startUrl = intent?.dataString ?: "https://www.google.com"
        try {
            swipeRefresh.setRefreshing(true)
        } catch (e: Exception) {
            // تجاهل
        }
        webView.loadUrl(startUrl)
    }

    // دعم زر العودة للتنقّل داخل WebView
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && ::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        super.onDestroy()
    }
}