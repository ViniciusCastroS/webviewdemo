package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat

class WebViewActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_URL = "extra_url"

        fun start(ctx: Context, url: String) {
            val intent = Intent(ctx, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
            ctx.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(EXTRA_URL).orEmpty()
        if (url.isBlank()) {
            finish()
            return
        }

        // Cor da toolbar (definida em res/values/colors.xml)
        val toolbarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // Configura o Custom Tab sem APIs deprecated
        val colorParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor)
            .build()

        val builder = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorParams)
            .setUrlBarHidingEnabled(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setShowTitle(true)

        val customTabsIntent = builder.build()

        // Vincula ao serviço de Custom Tabs (Chrome ou similar)
        CustomTabsClient.getPackageName(this, null)?.let { pkg ->
            customTabsIntent.intent.`package` = pkg
            CustomTabsClient.bindCustomTabsService(
                this,
                pkg,
                object : CustomTabsServiceConnection() {
                    override fun onCustomTabsServiceConnected(
                        name: ComponentName,
                        client: CustomTabsClient
                    ) {
                        client.warmup(0L)
                    }
                    override fun onServiceDisconnected(name: ComponentName) {}
                }
            )
        }

        // Abre a Custom Tab
        customTabsIntent.launchUrl(this, Uri.parse(url))

        // Fecha esta Activity para que, ao fechar a aba, volte diretamente à MainActivity
        finish()
    }
}
