package com.doomx.optimizer

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(9, 10, 13)
    private val panel = Color.rgb(22, 24, 30)
    private val red = Color.rgb(255, 52, 59)
    private val white = Color.rgb(245, 246, 248)
    private val muted = Color.rgb(166, 171, 182)
    private lateinit var status: TextView
    private lateinit var info: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(22), dp(18), dp(28)) }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(label("DOOMX", 34f, red, true))
        root.addView(label("FF OPTIMIZER  /  EXTREME MODE", 12f, muted, true).apply { setPadding(0, 0, 0, dp(18)) })

        val hero = card()
        hero.addView(label("PREPARE FOR BATTLE", 21f, white, true))
        hero.addView(label("Rootless gaming preparation. Android retains control of system resources.", 13f, muted, false).apply { setPadding(0, dp(8), 0, dp(12)) })
        status = label("STATUS  •  READY", 13f, red, true)
        hero.addView(status)
        hero.addView(actionButton("BOOST & PLAY FREE FIRE") { launchGame() }, matchWrap())
        root.addView(hero, matchWrap().bottom(14))

        val analyzer = card()
        analyzer.addView(label("DEVICE ANALYZER", 16f, white, true))
        info = label("Reading device information…", 13f, muted, false).apply { setPadding(0, dp(8), 0, dp(10)) }
        analyzer.addView(info)
        analyzer.addView(actionButton("REFRESH STATUS") { refreshInfo(); setStatus("DEVICE STATUS UPDATED") }, matchWrap())
        root.addView(analyzer, matchWrap().bottom(14))

        val actions = card()
        actions.addView(label("MANUAL ASSISTANTS", 16f, white, true))
        actions.addView(label("Choose what to change in Android settings. DOOMX does not silently close apps or delete personal files.", 12f, muted, false).apply { setPadding(0, dp(8), 0, dp(12)) })
        actions.addView(actionButton("BACKGROUND APPS\nOpen Android app management") { openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS) }, matchWrap().bottom(8))
        actions.addView(actionButton("STORAGE ASSISTANT\nReview Android storage") { openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS) }, matchWrap().bottom(8))
        actions.addView(actionButton("NETWORK SETTINGS\nReview Wi-Fi and mobile data") { openSettings(Settings.ACTION_WIRELESS_SETTINGS) }, matchWrap().bottom(8))
        actions.addView(actionButton("RESTORE / NORMAL MODE\nNo system tweaks were applied") { setStatus("NORMAL MODE • NO SYSTEM TWEAKS APPLIED"); refreshInfo() }, matchWrap())
        root.addView(actions, matchWrap().bottom(14))

        val limits = card()
        limits.addView(label("IMPORTANT LIMITS", 15f, red, true))
        limits.addView(label("• Ordinary apps cannot force-stop every other app.\n• CPU/GPU clocks and thermal limits are controlled by Android and the device maker.\n• Display refresh rate is not actual game FPS.\n• No automatic Do Not Disturb or battery setting changes are made.\n• Use Free Fire's own supported graphics/FPS options and keep the phone cool.", 12f, muted, false).apply { setPadding(0, dp(8), 0, 0) })
        root.addView(limits, matchWrap())
        root.addView(label("DOOMX • LOCAL-ONLY • NO LOGIN", 10f, muted, true).apply { gravity = android.view.Gravity.CENTER; setPadding(0, dp(18), 0, 0) })
        refreshInfo()
    }

    override fun onResume() { super.onResume(); if (::info.isInitialized) refreshInfo() }

    private fun launchGame() {
        refreshInfo()
        for (pkg in listOf("com.dts.freefireth", "com.dts.freefiremax")) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                setStatus("PREPARED • OPENING FREE FIRE")
                startActivity(intent)
                return
            }
        }
        setStatus("FREE FIRE NOT FOUND")
        android.app.AlertDialog.Builder(this).setTitle("DOOMX FF Optimizer")
            .setMessage("Free Fire was not found. Install the official game, then try again.")
            .setPositiveButton("OK", null).show()
    }

    private fun refreshInfo() {
        if (!::info.isInitialized) return
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memory = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val stat = StatFs(filesDir.absolutePath)
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork
        val caps = if (net != null) cm.getNetworkCapabilities(net) else null
        val connection = when {
            caps == null -> "Offline / no active network"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi connected"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile data connected"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet connected"
            else -> "Network connected"
        }
        val hz = try { String.format(Locale.US, "%.0f Hz (display refresh rate)", windowManager.defaultDisplay.refreshRate) } catch (_: Exception) { "Unavailable" }
        val battery = (getSystemService(Context.BATTERY_SERVICE) as BatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        info.text = "Device: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\nRAM available: ${bytes(memory.availMem)}\nTotal RAM: ${bytes(memory.totalMem)}\nStorage free: ${bytes(stat.availableBytes)} / ${bytes(stat.totalBytes)}\nNetwork: $connection\nDisplay: $hz\nBattery: ${if (battery >= 0) "$battery%" else "Unavailable"}"
    }

    private fun openSettings(action: String) {
        try { startActivity(Intent(action)) } catch (_: Exception) {
            android.app.AlertDialog.Builder(this).setMessage("This settings page is not available on this Android version.").setPositiveButton("OK", null).show()
        }
    }
    private fun setStatus(value: String) { status.text = "STATUS  •  $value" }
    private fun card() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(16), dp(16), dp(16)); background = rounded(panel, dp(18)) }
    private fun label(value: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color); setLineSpacing(dp(3).toFloat(), 1f)
        if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    private fun actionButton(value: String, action: () -> Unit) = Button(this).apply {
        text = value; textSize = 13f; isAllCaps = false; setTextColor(white); background = rounded(red, dp(12)); setPadding(dp(12), dp(8), dp(12), dp(8)); minHeight = dp(48); stateListAnimator = null; setOnClickListener { action() }
    }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = radius.toFloat() }
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun LinearLayout.LayoutParams.bottom(value: Int): LinearLayout.LayoutParams { bottomMargin = dp(value); return this }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun bytes(value: Long): String {
        if (value < 1024) return "$value B"
        var n = value.toDouble(); val units = arrayOf("KB", "MB", "GB", "TB"); var i = -1
        do { n /= 1024.0; i++ } while (n >= 1024 && i < units.lastIndex)
        return String.format(Locale.US, "%.1f %s", n, units[i.coerceAtMost(units.lastIndex)])
    }
}
