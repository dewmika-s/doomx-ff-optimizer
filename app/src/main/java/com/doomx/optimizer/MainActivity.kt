package com.doomx.optimizer

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "doomx_settings")
private val profileKey = stringPreferencesKey("profile")
private val bg = Color(0xFF08090B)
private val panel = Color(0xFF15171B)
private val red = Color(0xFFFF303D)
private val white = Color(0xFFF4F5F7)
private val muted = Color(0xFF9CA3AF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DoomxApp(this) }
    }
}

@Composable
private fun DoomxApp(context: Context) {
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf("SMART") }
    var tab by remember { mutableStateOf("HOME") }
    var lastAction by remember { mutableStateOf("System ready") }
    var history by remember { mutableStateOf(listOf("DOOMX initialized")) }
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        profile = context.dataStore.data.first()[profileKey] ?: "SMART"
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = bg, surface = panel, primary = red,
        onBackground = white, onSurface = white
    )) {
        Column(Modifier.fillMaxSize().background(bg)) {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF101114)).padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(42.dp).background(red, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text("DX", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("DOOMX", color = white, fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 2.sp)
                    Text("ADAPTIVE GAMING SYSTEM", color = muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
                }
                Text("● ONLINE", color = Color(0xFF5BE38D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("HOME", "PROFILES", "CLEANUP", "HISTORY").forEach { item ->
                    val selected = tab == item
                    Box(
                        Modifier.weight(1f).background(if (selected) red else panel, RoundedCornerShape(10.dp))
                            .clickable { tab = item }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, color = white, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (tab) {
                    "HOME" -> {
                        Spacer(Modifier.height(2.dp))
                        Text("GAMING DASHBOARD", color = muted, fontSize = 11.sp, letterSpacing = 2.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Column(Modifier.weight(1f)) {
                                Text("SYSTEM", color = white, fontSize = 28.sp, fontWeight = FontWeight.Black)
                                Text("Status & device telemetry", color = muted, fontSize = 12.sp)
                            }
                            Text("API ${Build.VERSION.SDK_INT}", color = red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard("DEVICE", "${Build.MANUFACTURER} ${Build.MODEL}", Modifier.weight(1f))
                            MetricCard("ANDROID", "Android ${Build.VERSION.RELEASE}", Modifier.weight(1f))
                        }
                        val mem = remember(refresh) { getMemory(context) }
                        val storage = remember(refresh) { getStorage() }
                        val battery = remember(refresh) { getBattery(context) }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard("AVAILABLE RAM", mem, Modifier.weight(1f))
                            MetricCard("FREE STORAGE", storage, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard("BATTERY", battery, Modifier.weight(1f))
                            MetricCard("THERMAL", thermalStatus(), Modifier.weight(1f))
                        }
                        CardBlock {
                            Text("ACTIVE PROFILE", color = muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
                            Spacer(Modifier.height(5.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(profile, color = red, fontSize = 23.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                                Text("● ACTIVE", color = Color(0xFF5BE38D), fontSize = 10.sp)
                            }
                            Text(profileDescription(profile), color = muted, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    lastAction = "Device metrics refreshed"
                                    refresh++
                                    history = listOf(stamp() + " · Metrics refreshed") + history
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = red),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("REFRESH DEVICE STATUS", fontWeight = FontWeight.Bold) }
                        }
                        CardBlock {
                            Text("QUICK ACTIONS", color = white, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            ActionRow("Run safe assessment", "Checks device state; no system settings changed") {
                                lastAction = "Safe assessment completed"
                                history = listOf(stamp() + " · Safe assessment") + history
                            }
                            ActionRow("Open game recommendations", "Graphics guidance only; no game files modified") {
                                lastAction = "Game recommendations: use supported in-game settings"
                            }
                            Text("Last action: $lastAction", color = muted, fontSize = 11.sp)
                        }
                    }
                    "PROFILES" -> {
                        Text("OPTIMIZATION PROFILES", color = white, fontSize = 23.sp, fontWeight = FontWeight.Black)
                        Text("Profiles adjust DOOMX recommendations. They do not unlock hidden CPU/GPU controls or guarantee higher FPS.", color = muted, fontSize = 12.sp)
                        listOf("SMART", "BALANCED", "PERFORMANCE", "THERMAL SAFE").forEach { p ->
                            CardBlock {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(p, color = if (profile == p) red else white, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                        Text(profileDescription(p), color = muted, fontSize = 11.sp)
                                    }
                                    if (profile == p) Text("ACTIVE", color = red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        profile = p
                                        scope.launch { context.dataStore.edit { it[profileKey] = p } }
                                        history = listOf(stamp() + " · Profile changed to $p") + history
                                        lastAction = "Profile changed to $p"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (profile == p) Color(0xFF34363B) else red),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text(if (profile == p) "SELECTED" else "ACTIVATE PROFILE") }
                            }
                        }
                    }
                    "CLEANUP" -> {
                        Text("STORAGE ASSISTANT", color = white, fontSize = 23.sp, fontWeight = FontWeight.Black)
                        Text("Safe app-owned cache cleanup. No root access required.", color = muted, fontSize = 12.sp)
                        val cacheBytes = remember(refresh) { context.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() } }
                        CardBlock {
                            Text("DOOMX CACHE", color = muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
                            Text(formatBytes(cacheBytes), color = red, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("Only files inside this app's cache directory are eligible.", color = muted, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { lastAction = "Scan complete: ${formatBytes(cacheBytes)} app cache"; tab = "CLEANUP" },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34363B)),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("SCAN APP CACHE") }
                            Button(
                                onClick = {
                                    val before = context.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
                                    context.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
                                    refresh++
                                    lastAction = "Cleared app cache (${formatBytes(before)})"
                                    history = listOf(stamp() + " · App cache cleared: " + formatBytes(before)) + history
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = red),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("CLEAR APP CACHE", fontWeight = FontWeight.Bold) }
                        }
                        CardBlock {
                            Text("SAFETY LIMITS", color = white, fontWeight = FontWeight.Bold)
                            Text("DOOMX will not scan or delete game data, user documents, system folders, or unknown directories. Cache deletion may be irreversible.", color = muted, fontSize = 12.sp)
                        }
                    }
                    else -> {
                        Text("AUDIT HISTORY", color = white, fontSize = 23.sp, fontWeight = FontWeight.Black)
                        Text("Recent actions in this session", color = muted, fontSize = 12.sp)
                        CardBlock {
                            history.forEach { entry ->
                                Text("›  $entry", color = white, fontSize = 12.sp, modifier = Modifier.padding(vertical = 5.dp))
                                HorizontalDivider(color = Color(0xFF2B2E33))
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { history = emptyList(); lastAction = "History cleared" },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("CLEAR HISTORY", color = red) }
                            Text("This starter stores history in memory for the current app session; durable audit export is not yet implemented.", color = muted, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = panel), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(label, color = muted, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(value, color = white, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}

@Composable
private fun CardBlock(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = panel),
        shape = RoundedCornerShape(16.dp)
    ) { Column(Modifier.padding(16.dp), content = content) }
}

@Composable
private fun ActionRow(title: String, subtitle: String, action: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = action).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = white, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = muted, fontSize = 10.sp)
        }
        Text("›", color = red, fontSize = 25.sp)
    }
    HorizontalDivider(color = Color(0xFF2B2E33))
}

private fun profileDescription(profile: String) = when (profile) {
    "SMART" -> "Conservative recommendations based on current device conditions."
    "BALANCED" -> "Prioritizes stability and sensible battery usage."
    "PERFORMANCE" -> "Performance guidance; supported hardware APIs vary by device."
    else -> "Thermal-first guidance. Reduce workload if the phone becomes hot."
}

private fun getMemory(context: Context): String = try {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val info = ActivityManager.MemoryInfo()
    am.getMemoryInfo(info)
    "${formatBytes(info.availMem)} free"
} catch (_: Exception) { "Unavailable" }

private fun getStorage(): String = try {
    val stat = StatFs(Environment.getDataDirectory().path)
    formatBytes(stat.availableBytes) + " free"
} catch (_: Exception) { "Unavailable" }

private fun getBattery(context: Context): String = try {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    if (level in 0..100) "$level%" else "Unavailable"
} catch (_: Exception) { "Unavailable" }

private fun thermalStatus(): String = if (Build.VERSION.SDK_INT >= 29) {
    "API available"
} else "Unsupported"

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble() / 1024.0
    var index = 0
    while (value >= 1024 && index < units.lastIndex) { value /= 1024; index++ }
    return String.format(Locale.US, "%.1f %s", value, units[index])
}

private fun stamp(): String = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())
