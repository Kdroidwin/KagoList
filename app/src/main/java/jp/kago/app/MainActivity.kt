package jp.kago.app

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.math.pow

private enum class AppTheme(val label: String) {
    LIGHT("ホワイト"), DARK("ダーク"), AMOLED("AMOLED")
}

private enum class AppTab(val label: String, val title: String) {
    STAPLES("定番購入品", "いつもの品"), LIST("買い物リスト", "買い物リスト"), PROFILE("マイページ", "マイページ")
}

private const val DefaultAccentCode = "#777777"
private val DefaultStores = listOf("Amazon", "イオン", "マツキヨ", "未指定")

private data class Palette(
    val background: Color,
    val surface: Color,
    val raised: Color,
    val text: Color,
    val secondaryText: Color,
    val outline: Color,
    val accent: Color,
    val accentSoft: Color,
    val positive: Color,
    val positiveSoft: Color,
    val danger: Color
)

private val LightPalette = Palette(
    background = Color(0xFFF6F2EC), surface = Color(0xFFFFFEFC), raised = Color(0xFFEAEFF2),
    text = Color(0xFF203145), secondaryText = Color(0xFF6B6C70), outline = Color(0xFFE0DCD5),
    accent = Color(0xFF777777), accentSoft = Color(0xFFE8E8E8), positive = Color(0xFF2E6B58),
    positiveSoft = Color(0xFFE1EEE7), danger = Color(0xFFB3261E)
)
private val DarkPalette = Palette(
    background = Color(0xFF151719), surface = Color(0xFF202326), raised = Color(0xFF2A3034),
    text = Color(0xFFF3F0EA), secondaryText = Color(0xFFB7B8B8), outline = Color(0xFF3C4245),
    accent = Color(0xFFB8BDC3), accentSoft = Color(0xFF323538), positive = Color(0xFF9DD2BA),
    positiveSoft = Color(0xFF243C33), danger = Color(0xFFFFB4AB)
)
private val AmoledPalette = DarkPalette.copy(
    background = Color(0xFF000000), surface = Color(0xFF090A0B), raised = Color(0xFF141618),
    outline = Color(0xFF303335), accentSoft = Color(0xFF252729), positiveSoft = Color(0xFF172820)
)
private val LocalPalette = staticCompositionLocalOf { LightPalette }

private data class ShoppingEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val store: String,
    val memo: String = "",
    val bought: Boolean = false,
    val quantity: Int = 1
)

private data class Staple(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val store: String,
    val memo: String = "",
    val quantity: Int = 1
)

private data class ImportedTransfer(
    val type: String,
    val entries: List<ShoppingEntry>,
    val staples: List<Staple>,
    val stores: List<String>,
    val theme: AppTheme? = null,
    val accentCode: String? = null,
    val showTabLabels: Boolean? = null,
    val showFabLabels: Boolean? = null
)

private fun parseAccentCode(value: String): Color? {
    val digits = value.trim().removePrefix("#")
    if (!digits.matches(Regex("[0-9a-fA-F]{6}"))) return null
    return runCatching { Color(android.graphics.Color.parseColor("#$digits")) }.getOrNull()
}

private fun accentCodeIsValid(value: String): Boolean = parseAccentCode(value) != null

private fun accentContentColor(color: Color): Color {
    fun linear(component: Float): Float = if (component <= 0.04045f) component / 12.92f else ((component + 0.055f) / 1.055f).pow(2.4f)
    val luminance = 0.2126f * linear(color.red) + 0.7152f * linear(color.green) + 0.0722f * linear(color.blue)
    val contrastWithBlack = (luminance + 0.05f) / 0.05f
    val contrastWithWhite = 1.05f / (luminance + 0.05f)
    return if (contrastWithBlack >= contrastWithWhite) Color.Black else Color.White
}

private fun entryArray(entries: List<ShoppingEntry>) = JSONArray().apply {
    entries.forEach { entry -> put(JSONObject().apply {
        put("id", entry.id); put("name", entry.name); put("store", entry.store)
        put("memo", entry.memo); put("bought", entry.bought); put("quantity", entry.quantity)
    }) }
}

private fun stapleArray(staples: List<Staple>) = JSONArray().apply {
    staples.forEach { staple -> put(JSONObject().apply {
        put("id", staple.id); put("name", staple.name); put("store", staple.store); put("memo", staple.memo); put("quantity", staple.quantity)
    }) }
}

private fun transferJson(
    type: String,
    entries: List<ShoppingEntry>,
    staples: List<Staple>,
    stores: List<String>,
    theme: AppTheme,
    accentCode: String,
    showTabLabels: Boolean,
    showFabLabels: Boolean
): String = JSONObject().apply {
    put("format", "kago.list.export")
    put("schemaVersion", 1)
    put("type", type)
    put("createdAt", System.currentTimeMillis())
    if (type == "backup" || type == "shopping_list") put("entries", entryArray(entries))
    if (type == "backup" || type == "staples") put("staples", stapleArray(staples))
    if (type == "backup") {
        put("stores", JSONArray(stores))
        put("theme", theme.name)
        put("accentCode", accentCode)
        put("showTabLabels", showTabLabels)
        put("showFabLabels", showFabLabels)
    }
}.toString(2)

private fun decodeTransfer(json: String): ImportedTransfer {
    if (json.length > 4_000_000) throw IllegalArgumentException("ファイルが大きすぎます。")
    val root = JSONObject(json)
    require(root.optString("format") == "kago.list.export") { "KagoListのリストファイルではありません。" }
    require(root.optInt("schemaVersion") == 1) { "このファイル形式には対応していません。" }
    val type = root.optString("type")
    require(type in setOf("backup", "shopping_list", "staples")) { "ファイルの種類を認識できません。" }
    fun readEntries(array: JSONArray?): List<ShoppingEntry> = array?.let { rows ->
        List(rows.length()) { index -> rows.getJSONObject(index).let { row ->
            ShoppingEntry(
                name = row.optString("name").trim(),
                store = row.optString("store", "未指定").ifBlank { "未指定" },
                memo = row.optString("memo"),
                bought = row.optBoolean("bought"),
                quantity = row.optInt("quantity", 1).coerceAtLeast(1)
            )
        } }.filter { it.name.isNotBlank() }
    }.orEmpty()
    fun readStaples(array: JSONArray?): List<Staple> = array?.let { rows ->
        List(rows.length()) { index -> rows.getJSONObject(index).let { row ->
            Staple(
                name = row.optString("name").trim(),
                store = row.optString("store", "未指定").ifBlank { "未指定" },
                memo = row.optString("memo"),
                quantity = row.optInt("quantity", 1).coerceAtLeast(1)
            )
        } }.filter { it.name.isNotBlank() }
    }.orEmpty()
    val theme = runCatching { AppTheme.valueOf(root.optString("theme")) }.getOrNull()
    val storesArray = root.optJSONArray("stores")
    val stores = storesArray?.let { array -> List(array.length()) { array.optString(it) }.filter(String::isNotBlank) }.orEmpty()
    return ImportedTransfer(
        type = type,
        entries = readEntries(root.optJSONArray("entries")),
        staples = readStaples(root.optJSONArray("staples")),
        stores = stores,
        theme = theme,
        accentCode = root.optString("accentCode").takeIf(::accentCodeIsValid),
        showTabLabels = if (root.has("showTabLabels")) root.optBoolean("showTabLabels") else null,
        showFabLabels = if (root.has("showFabLabels")) root.optBoolean("showFabLabels") else null
    )
}

private class LocalStorage(context: Context) {
    private val prefs = context.getSharedPreferences("kago_local_data", Context.MODE_PRIVATE)
    private val keyAlias = "jp.kago.app.local-data.v1"

    fun readEntries(): List<ShoppingEntry> = runCatching {
        val data = JSONArray(readEncrypted("entries") ?: "[]")
        List(data.length()) { index ->
            val row = data.getJSONObject(index)
            ShoppingEntry(
                id = row.optString("id", UUID.randomUUID().toString()),
                name = row.optString("name"),
                store = row.optString("store", "未指定"),
                memo = row.optString("memo"),
                bought = row.optBoolean("bought"),
                quantity = row.optInt("quantity", 1).coerceAtLeast(1)
            )
        }
    }.getOrDefault(emptyList())

    fun readStaples(): List<Staple> = runCatching {
        val data = JSONArray(readEncrypted("staples") ?: "[]")
        List(data.length()) { index ->
            val row = data.getJSONObject(index)
            Staple(
                id = row.optString("id", UUID.randomUUID().toString()),
                name = row.optString("name"),
                store = row.optString("store", "未指定"),
                memo = row.optString("memo"),
                quantity = row.optInt("quantity", 1).coerceAtLeast(1)
            )
        }
    }.getOrDefault(emptyList())

    fun saveEntries(entries: List<ShoppingEntry>) {
        if (entries.isEmpty()) {
            prefs.edit().remove("entries").apply()
            return
        }
        val data = JSONArray()
        entries.forEach { entry ->
            data.put(JSONObject().apply {
                put("id", entry.id); put("name", entry.name); put("store", entry.store)
                put("memo", entry.memo); put("bought", entry.bought); put("quantity", entry.quantity)
            })
        }
        writeEncrypted("entries", data.toString())
    }

    fun saveStaples(staples: List<Staple>) {
        if (staples.isEmpty()) {
            prefs.edit().remove("staples").apply()
            return
        }
        val data = JSONArray()
        staples.forEach { staple ->
            data.put(JSONObject().apply {
                put("id", staple.id); put("name", staple.name); put("store", staple.store); put("memo", staple.memo); put("quantity", staple.quantity)
            })
        }
        writeEncrypted("staples", data.toString())
    }

    fun theme(): AppTheme = runCatching { AppTheme.valueOf(prefs.getString("theme", AppTheme.LIGHT.name)!!) }
        .getOrDefault(AppTheme.LIGHT)

    fun showTabLabels(): Boolean = prefs.getBoolean("show_tab_labels", true)
    fun showFabLabels(): Boolean = prefs.getBoolean("show_fab_labels", true)
    fun accentCode(): String = prefs.getString("accent_code", DefaultAccentCode) ?: DefaultAccentCode
    fun stores(): List<String> = runCatching {
        val array = JSONArray(prefs.getString("stores", JSONArray(DefaultStores).toString()))
        List(array.length()) { array.getString(it) }.distinct().ifEmpty { listOf("未指定") }
    }.getOrDefault(DefaultStores)
    fun saveTheme(theme: AppTheme) = prefs.edit().putString("theme", theme.name).apply()
    fun saveShowTabLabels(show: Boolean) = prefs.edit().putBoolean("show_tab_labels", show).apply()
    fun saveShowFabLabels(show: Boolean) = prefs.edit().putBoolean("show_fab_labels", show).apply()
    fun saveAccentCode(code: String) = prefs.edit().putString("accent_code", code).apply()
    fun saveStores(stores: List<String>) = prefs.edit().putString("stores", JSONArray(stores).toString()).apply()
    fun clear() {
        prefs.edit().clear().apply()
        runCatching { KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(keyAlias) }
    }

    private fun readEncrypted(name: String): String? {
        val packed = prefs.getString(name, null) ?: return null
        val bytes = Base64.decode(packed, Base64.NO_WRAP)
        require(bytes.isNotEmpty())
        val ivLength = bytes[0].toInt() and 0xff
        require(ivLength > 0 && bytes.size > ivLength + 1)
        val iv = bytes.copyOfRange(1, ivLength + 1)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(bytes, ivLength + 1, bytes.size - ivLength - 1), Charsets.UTF_8)
    }

    private fun writeEncrypted(name: String, value: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val payload = ByteArray(cipher.iv.size + encrypted.size + 1)
        payload[0] = cipher.iv.size.toByte()
        System.arraycopy(cipher.iv, 0, payload, 1, cipher.iv.size)
        System.arraycopy(encrypted, 0, payload, cipher.iv.size + 1, encrypted.size)
        prefs.edit().putString(name, Base64.encodeToString(payload, Base64.NO_WRAP)).apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(keyAlias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
        }.generateKey()
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KagoApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KagoApp() {
    val context = LocalContext.current
    val storage = remember { LocalStorage(context.applicationContext) }
    var entries by remember { mutableStateOf(storage.readEntries()) }
    var staples by remember { mutableStateOf(storage.readStaples()) }
    var storeLabels by remember { mutableStateOf(storage.stores()) }
    var theme by remember { mutableStateOf(storage.theme()) }
    var showTabLabels by remember { mutableStateOf(storage.showTabLabels()) }
    var showFabLabels by remember { mutableStateOf(storage.showFabLabels()) }
    var accentCode by remember { mutableStateOf(storage.accentCode()) }
    var selectedTab by remember { mutableStateOf(AppTab.LIST) }
    var selectedStore by remember { mutableStateOf("すべて") }
    var transferStore by remember { mutableStateOf("すべて") }
    var editor by remember { mutableStateOf<EditorState?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var pendingExport by remember { mutableStateOf<Pair<String, String>?>(null) }
    var pendingImport by remember { mutableStateOf<ImportedTransfer?>(null) }
    val availableStores = remember(storeLabels, entries, staples) {
        (storeLabels + entries.map { it.store } + staples.map { it.store })
            .filterNot { it == "未指定" }.distinct() + "未指定"
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val contents = pendingExport?.second
        pendingExport = null
        if (uri != null && contents != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8)?.use { it.write(contents) }
                    ?: error("保存先を開けませんでした。")
            }.onSuccess { Toast.makeText(context, "JSONファイルを保存しました。", Toast.LENGTH_SHORT).show() }
                .onFailure { Toast.makeText(context, "保存できませんでした。", Toast.LENGTH_LONG).show() }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                val contents = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    ?: error("ファイルを開けませんでした。")
                decodeTransfer(contents)
            }.onSuccess { pendingImport = it }
                .onFailure { error -> Toast.makeText(context, error.message ?: "読み込めませんでした。", Toast.LENGTH_LONG).show() }
        }
    }

    fun launchExport(type: String) {
        val listToExport = if (type == "shopping_list" && transferStore != "すべて") {
            entries.filter { it.store == transferStore }
        } else entries
        val baseName = when (type) {
            "backup" -> "KagoList-backup"
            "staples" -> "KagoList-staples"
            else -> if (transferStore == "すべて") "KagoList-shopping-list" else "KagoList-${fileSafeName(transferStore)}-list"
        }
        pendingExport = baseName to transferJson(type, listToExport, staples, storeLabels, theme, accentCode, showTabLabels, showFabLabels)
        exportLauncher.launch("$baseName-${fileTimestamp()}.json")
    }

    fun shareList(type: String) {
        val listToShare = if (type != "shopping_list" || transferStore == "すべて") entries else entries.filter { it.store == transferStore }
        val json = transferJson(type, listToShare, staples, storeLabels, theme, accentCode, showTabLabels, showFabLabels)
        runCatching {
            val shareDirectory = File(context.cacheDir, "shared_exports").apply { mkdirs() }
            val kind = if (type == "staples") "staples" else "shopping-list"
            val file = File(shareDirectory, "KagoList-$kind-${UUID.randomUUID()}.json").apply { writeText(json, Charsets.UTF_8) }
            val uri = FileProvider.getUriForFile(context, "jp.kago.app.fileprovider", file)
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                this.type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, if (type == "staples") "KagoList 定番品" else "KagoList 買い物リスト")
                clipData = ClipData.newUri(context.contentResolver, file.name, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "共有先を選択"))
        }.onFailure { Toast.makeText(context, it.message ?: "共有を開始できませんでした。", Toast.LENGTH_LONG).show() }
    }

    fun applyImport(imported: ImportedTransfer, replace: Boolean) {
        val newEntries = imported.entries.map { it.copy(id = UUID.randomUUID().toString()) }
        val newStaples = imported.staples.map { it.copy(id = UUID.randomUUID().toString()) }
        when (imported.type) {
            "backup" -> {
                entries = if (replace) newEntries else mergeEntries(entries, newEntries)
                staples = if (replace) newStaples else mergeStaples(staples, newStaples)
                val storesInData = imported.entries.map { it.store } + imported.staples.map { it.store }
                storeLabels = if (replace) (imported.stores + storesInData + "未指定").distinct()
                else (storeLabels + imported.stores + storesInData).distinct()
                if (replace) {
                    imported.theme?.let { theme = it }
                    imported.accentCode?.let { accentCode = it }
                    imported.showTabLabels?.let { showTabLabels = it }
                    imported.showFabLabels?.let { showFabLabels = it }
                }
            }
            "shopping_list" -> {
                entries = if (replace) newEntries else mergeEntries(entries, newEntries)
                storeLabels = (storeLabels + imported.entries.map { it.store } + "未指定").distinct()
            }
            "staples" -> {
                staples = if (replace) newStaples else mergeStaples(staples, newStaples)
                storeLabels = (storeLabels + imported.staples.map { it.store } + "未指定").distinct()
            }
        }
        selectedStore = "すべて"
        pendingImport = null
        Toast.makeText(context, if (replace) "ファイルの内容に置き換えました。" else "ファイルの内容を追加しました。", Toast.LENGTH_SHORT).show()
    }

    val basePalette = when (theme) {
        AppTheme.LIGHT -> LightPalette
        AppTheme.DARK -> DarkPalette
        AppTheme.AMOLED -> AmoledPalette
    }
    val accent = parseAccentCode(accentCode) ?: Color(0xFF777777)
    val palette = basePalette.copy(
        accent = accent,
        accentSoft = accent.copy(alpha = if (theme == AppTheme.LIGHT) 0.13f else 0.2f)
    )
    val accentOnColor = accentContentColor(accent)
    val isDark = theme != AppTheme.LIGHT

    LaunchedEffect(entries) { storage.saveEntries(entries) }
    LaunchedEffect(staples) { storage.saveStaples(staples) }
    LaunchedEffect(storeLabels) { storage.saveStores(storeLabels) }
    LaunchedEffect(theme) { storage.saveTheme(theme) }
    LaunchedEffect(showTabLabels) { storage.saveShowTabLabels(showTabLabels) }
    LaunchedEffect(showFabLabels) { storage.saveShowFabLabels(showFabLabels) }
    LaunchedEffect(accentCode) {
        if (accentCodeIsValid(accentCode)) {
            val digits = accentCode.trim().removePrefix("#").uppercase()
            storage.saveAccentCode("#$digits")
        }
    }

    SideEffect {
        val activity = context as? Activity ?: return@SideEffect
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }

    val scheme = if (isDark) darkColorScheme(
        primary = palette.accent, onPrimary = accentOnColor, secondary = palette.positive,
        background = palette.background, surface = palette.surface, onSurface = palette.text,
        onBackground = palette.text, outline = palette.outline, error = palette.danger
    ) else lightColorScheme(
        primary = palette.accent, onPrimary = accentOnColor, secondary = palette.positive,
        background = palette.background, surface = palette.surface, onSurface = palette.text,
        onBackground = palette.text, outline = palette.outline, error = palette.danger
    )

    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(colorScheme = scheme) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = palette.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(selectedTab.title, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = palette.text)
                                if (selectedTab == AppTab.LIST && entries.isNotEmpty()) {
                                    Text("未購入 ${entries.count { !it.bought }} 点", fontSize = 12.sp, color = palette.secondaryText)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = palette.surface,
                        contentColor = palette.text,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        AppTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                icon = {
                                    val icon = when (tab) {
                                        AppTab.STAPLES -> Icons.Outlined.Refresh
                                        AppTab.LIST -> Icons.AutoMirrored.Outlined.List
                                        AppTab.PROFILE -> Icons.Outlined.Person
                                    }
                                    Icon(icon, contentDescription = tab.label)
                                },
                                label = if (showTabLabels) ({ Text(tab.label, maxLines = 1, fontSize = 11.sp) }) else null,
                                alwaysShowLabel = showTabLabels,
                                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                    selectedIconColor = palette.accent,
                                    selectedTextColor = palette.text,
                                    indicatorColor = palette.accentSoft,
                                    unselectedIconColor = palette.secondaryText,
                                    unselectedTextColor = palette.secondaryText
                                )
                            )
                        }
                    }
                },
                floatingActionButton = {
                    when (selectedTab) {
                        AppTab.LIST -> AddFab("今回だけ追加", showFabLabels, palette, accentOnColor) { editor = EditorState.Current() }
                        AppTab.STAPLES -> AddFab("定番品を登録", showFabLabels, palette, accentOnColor) { editor = EditorState.StapleEditor() }
                        AppTab.PROFILE -> Unit
                    }
                }
            ) { contentPadding ->
                when (selectedTab) {
                    AppTab.LIST -> ShoppingListScreen(
                        entries = entries,
                        stores = availableStores,
                        selectedStore = selectedStore,
                        onSelectedStoreChange = { selectedStore = it },
                        onToggleBought = { id -> entries = entries.map { if (it.id == id) it.copy(bought = !it.bought) else it } },
                        onEdit = { entry -> editor = EditorState.Current(entry) },
                        onDelete = { id -> entries = entries.filterNot { it.id == id } },
                        onOpenStaples = { selectedTab = AppTab.STAPLES },
                        modifier = Modifier.padding(contentPadding)
                    )
                    AppTab.STAPLES -> StaplesScreen(
                        staples = staples,
                        entries = entries,
                        storeOrder = availableStores,
                        onAddToList = { staple -> entries = entries + ShoppingEntry(name = staple.name, store = staple.store, memo = staple.memo, quantity = staple.quantity) },
                        onEdit = { staple -> editor = EditorState.StapleEditor(staple) },
                        onDelete = { id -> staples = staples.filterNot { it.id == id } },
                        modifier = Modifier.padding(contentPadding)
                    )
                    AppTab.PROFILE -> SettingsScreen(
                        theme = theme,
                        showTabLabels = showTabLabels,
                        showFabLabels = showFabLabels,
                        accentCode = accentCode,
                        stores = availableStores,
                        transferStore = transferStore,
                        onTransferStoreChange = { transferStore = it },
                        onReorderStores = { from, to ->
                            val reordered = availableStores.toMutableList()
                            if (from in reordered.indices && to in reordered.indices) {
                                val moved = reordered.removeAt(from)
                                reordered.add(to.coerceIn(0, reordered.size), moved)
                                storeLabels = reordered.filterNot { it == "未指定" } + "未指定"
                            }
                        },
                        onThemeChange = { theme = it },
                        onTabLabelsChange = { showTabLabels = it },
                        onFabLabelsChange = { showFabLabels = it },
                        onAccentCodeChange = { accentCode = it },
                        onAddStore = { label ->
                            val clean = label.trim()
                            if (clean.isNotBlank() && clean != "すべて" && storeLabels.none { it.equals(clean, ignoreCase = true) }) {
                                storeLabels = storeLabels + clean
                                true
                            } else {
                                false
                            }
                        },
                        onDeleteStore = { label ->
                            if (label != "未指定") {
                                storeLabels = storeLabels.filterNot { it == label }
                                entries = entries.map { if (it.store == label) it.copy(store = "未指定") else it }
                                staples = staples.map { if (it.store == label) it.copy(store = "未指定") else it }
                                if (selectedStore == label) selectedStore = "すべて"
                                if (transferStore == label) transferStore = "すべて"
                            }
                        },
                        onExportBackup = { launchExport("backup") },
                        onImport = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        onExportShoppingList = { launchExport("shopping_list") },
                        onExportStaples = { launchExport("staples") },
                        onShareShoppingList = { shareList("shopping_list") },
                        onShareStaples = { shareList("staples") },
                        onClearData = { showDeleteConfirmation = true },
                        modifier = Modifier.padding(contentPadding)
                    )
                }
            }
        }
    }

    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(colorScheme = scheme) {
    when (val activeEditor = editor) {
        is EditorState.Current -> EntryEditorSheet(
            initialName = activeEditor.entry?.name.orEmpty(),
            initialStore = activeEditor.entry?.store ?: preferredStore(selectedStore, storeLabels),
            initialMemo = activeEditor.entry?.memo.orEmpty(),
            initialQuantity = activeEditor.entry?.quantity ?: 1,
            title = if (activeEditor.entry == null) "今回だけ追加" else "商品を編集",
            submitLabel = if (activeEditor.entry == null) "リストに追加" else "保存",
            stores = availableStores,
            onDismiss = { editor = null },
            onSubmit = { name, store, memo, quantity ->
                val old = activeEditor.entry
                entries = if (old == null) entries + ShoppingEntry(name = name, store = store, memo = memo, quantity = quantity)
                else entries.map { if (it.id == old.id) old.copy(name = name, store = store, memo = memo, quantity = quantity) else it }
                editor = null
            }
        )
        is EditorState.StapleEditor -> EntryEditorSheet(
            initialName = activeEditor.staple?.name.orEmpty(),
            initialStore = activeEditor.staple?.store ?: preferredStore(selectedStore, storeLabels),
            initialMemo = activeEditor.staple?.memo.orEmpty(),
            initialQuantity = activeEditor.staple?.quantity ?: 1,
            title = if (activeEditor.staple == null) "定番品を登録" else "定番品を編集",
            submitLabel = if (activeEditor.staple == null) "定番品に登録" else "保存",
            stores = availableStores,
            onDismiss = { editor = null },
            onSubmit = { name, store, memo, quantity ->
                val old = activeEditor.staple
                staples = if (old == null) staples + Staple(name = name, store = store, memo = memo, quantity = quantity)
                else staples.map { if (it.id == old.id) old.copy(name = name, store = store, memo = memo, quantity = quantity) else it }
                editor = null
            }
        )
        null -> Unit
    }

    pendingImport?.let { imported ->
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text("ファイルを読み込み") },
            text = { Text("${transferTypeLabel(imported.type)}を、現在のデータに追加するか置き換えるか選んでください。") },
            confirmButton = {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { applyImport(imported, replace = false) }) { Text("既存データに追加") }
                    Button(onClick = { applyImport(imported, replace = true) }) { Text("置き換えて読み込む") }
                    TextButton(onClick = { pendingImport = null }) { Text("キャンセル") }
                }
            },
            containerColor = palette.surface,
            titleContentColor = palette.text,
            textContentColor = palette.secondaryText
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("端末内のデータを削除しますか？") },
            text = { Text("買い物リスト、定番品、テーマ設定をすべて削除します。この操作は元に戻せません。") },
            confirmButton = {
                Button(
                    onClick = {
                        storage.clear()
                        entries = emptyList()
                        staples = emptyList()
                        storeLabels = DefaultStores
                        theme = AppTheme.LIGHT
                        showTabLabels = true
                        showFabLabels = true
                        accentCode = DefaultAccentCode
                        selectedStore = "すべて"
                        transferStore = "すべて"
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.danger)
                ) { Text("すべて削除") }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteConfirmation = false }) { Text("キャンセル") } },
            containerColor = palette.surface,
            titleContentColor = palette.text,
            textContentColor = palette.secondaryText
        )
    }
        }
    }
}

private sealed interface EditorState {
    data class Current(val entry: ShoppingEntry? = null) : EditorState
    data class StapleEditor(val staple: Staple? = null) : EditorState
}

@Composable
private fun AddFab(label: String, showLabel: Boolean, palette: Palette, contentColor: Color, onClick: () -> Unit) {
    if (showLabel) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            containerColor = palette.accent,
            contentColor = contentColor,
            shape = RoundedCornerShape(20.dp),
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    } else {
        FloatingActionButton(
            onClick = onClick,
            containerColor = palette.accent,
            contentColor = contentColor,
            shape = RoundedCornerShape(18.dp)
        ) { Icon(Icons.Outlined.Add, contentDescription = label) }
    }
}

private fun preferredStore(selectedStore: String, stores: List<String>): String =
    if (selectedStore != "すべて") selectedStore else stores.firstOrNull { it != "未指定" } ?: "未指定"

private fun fileTimestamp(): String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())

private fun fileSafeName(value: String): String = value.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(30)

private fun transferTypeLabel(type: String): String = when (type) {
    "backup" -> "バックアップ"
    "shopping_list" -> "買い物リスト"
    else -> "定番品リスト"
}

private fun mergeEntries(existing: List<ShoppingEntry>, incoming: List<ShoppingEntry>): List<ShoppingEntry> =
    incoming.fold(existing) { result, next ->
        if (result.any { it.name.equals(next.name, ignoreCase = true) && it.store == next.store }) result else result + next
    }

private fun mergeStaples(existing: List<Staple>, incoming: List<Staple>): List<Staple> =
    incoming.fold(existing) { result, next ->
        if (result.any { it.name.equals(next.name, ignoreCase = true) && it.store == next.store }) result else result + next
    }

@Composable
private fun ShoppingListScreen(
    entries: List<ShoppingEntry>,
    stores: List<String>,
    selectedStore: String,
    onSelectedStoreChange: (String) -> Unit,
    onToggleBought: (String) -> Unit,
    onEdit: (ShoppingEntry) -> Unit,
    onDelete: (String) -> Unit,
    onOpenStaples: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    var showBought by remember { mutableStateOf(false) }
    val storeOptions = remember(entries, stores) { (listOf("すべて") + stores + entries.map { it.store }).distinct() }
    val visibleEntries = entries.filter { selectedStore == "すべて" || it.store == selectedStore }
    val pending = visibleEntries.filterNot { it.bought }
    val bought = visibleEntries.filter { it.bought }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("購入先", fontSize = 12.sp, color = palette.secondaryText, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    storeOptions.forEach { store ->
                        FilterChip(
                            selected = selectedStore == store,
                            onClick = { onSelectedStoreChange(store) },
                            label = { Text(store, fontSize = 13.sp) },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, if (selectedStore == store) palette.text else palette.outline)
                        )
                    }
                }
            }
        }

        if (pending.isEmpty() && bought.isEmpty()) {
            item {
                EmptyShoppingState(
                    hasAnyEntries = entries.isNotEmpty(),
                    onOpenStaples = onOpenStaples,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 430.dp)
                )
            }
        } else {
            val groups = pending.groupBy { it.store }.toSortedMap(storeOrderComparator(stores))
            groups.forEach { (store, group) ->
                item(key = "heading:$store") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 3.dp, bottom = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(store, fontWeight = FontWeight.SemiBold, color = palette.text, fontSize = 15.sp)
                        Spacer(Modifier.width(9.dp))
                        Text("のこり ${group.size} 点", color = palette.secondaryText, fontSize = 12.sp)
                    }
                }
                item(key = "card:$store") {
                    EntryCard(
                        entries = group,
                        onToggle = onToggleBought,
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                }
            }

            if (pending.isNotEmpty() && bought.isNotEmpty()) {
                item {
                    Text("「買った」を押すと、ここに移動します", modifier = Modifier.fillMaxWidth(), color = palette.secondaryText, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            if (bought.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable { showBought = !showBought },
                        color = palette.positiveSoft,
                        border = BorderStroke(1.dp, palette.positive.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = palette.positive)
                            Spacer(Modifier.width(10.dp))
                            Text("今日買ったもの", color = palette.positive, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(8.dp))
                            Text("${bought.size} 点", color = palette.positive, fontSize = 12.sp)
                            Spacer(Modifier.weight(1f))
                            Icon(if (showBought) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = palette.positive)
                        }
                    }
                }
                if (showBought) {
                    val doneGroups = bought.groupBy { it.store }.toSortedMap(storeOrderComparator(stores))
                    doneGroups.forEach { (store, group) ->
                        item(key = "done:$store") { EntryCard(entries = group, onToggle = onToggleBought, onEdit = onEdit, onDelete = onDelete) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyShoppingState(hasAnyEntries: Boolean, onOpenStaples: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = CircleShape, color = palette.raised, modifier = Modifier.size(82.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = palette.text, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(if (hasAnyEntries) "この購入先に商品はありません" else "買い物リストは空です", color = palette.text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasAnyEntries) "別の購入先を選ぶか、商品を追加してください。"
            else "今回の買い物で買うものを並べておきましょう。\n定番品はいつもの品からすぐに追加できます。",
            color = palette.secondaryText,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (!hasAnyEntries) {
            Spacer(Modifier.height(20.dp))
            OutlinedButton(onClick = onOpenStaples, shape = RoundedCornerShape(16.dp)) {
                Text("定番品を見る", color = palette.text, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(7.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun EntryCard(
    entries: List<ShoppingEntry>,
    onToggle: (String) -> Unit,
    onEdit: (ShoppingEntry) -> Unit,
    onDelete: (String) -> Unit
) {
    val palette = LocalPalette.current
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = if (palette.background == LightPalette.background) 1.dp else 0.dp)
    ) {
        entries.forEachIndexed { index, entry ->
            EntryRow(entry = entry, onToggle = { onToggle(entry.id) }, onEdit = { onEdit(entry) }, onDelete = { onDelete(entry.id) })
            if (index != entries.lastIndex) {
                HorizontalDivider(color = palette.outline.copy(alpha = 0.8f), modifier = Modifier.padding(start = 72.dp))
            }
        }
    }
}

@Composable
private fun EntryRow(entry: ShoppingEntry, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductMark(entry.name, Modifier.size(48.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                entry.name,
                color = if (entry.bought) palette.secondaryText else palette.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (entry.bought) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (entry.quantity > 1) Text("×${entry.quantity}", color = palette.secondaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            if (entry.memo.isNotBlank()) Text(entry.memo, color = palette.secondaryText, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onToggle, modifier = Modifier.size(44.dp)) {
            if (entry.bought) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = "未購入に戻す", tint = palette.positive, modifier = Modifier.size(28.dp))
            } else {
                Box(Modifier.size(26.dp).border(2.dp, palette.text, CircleShape))
            }
        }
        EntryMenu(onEdit = onEdit, onDelete = onDelete)
    }
}

@Composable
private fun ProductMark(name: String, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    val tone = when {
        listOf("野菜", "菜", "レタス", "キャベツ", "ねぎ", "にんじん").any(name::contains) -> palette.positiveSoft
        listOf("洗", "歯", "シャンプー", "柔軟", "洗剤").any(name::contains) -> palette.raised
        else -> palette.accentSoft
    }
    Surface(modifier = modifier, shape = RoundedCornerShape(15.dp), color = tone) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = palette.text.copy(alpha = 0.84f), modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun EntryMenu(onEdit: () -> Unit, onDelete: () -> Unit) {
    val palette = LocalPalette.current
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Outlined.MoreVert, contentDescription = "商品メニュー", tint = palette.secondaryText, modifier = Modifier.size(19.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = palette.surface) {
            DropdownMenuItem(
                text = { Text("編集") }, leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                onClick = { expanded = false; onEdit() }
            )
            DropdownMenuItem(
                text = { Text("削除") }, leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = palette.danger) },
                onClick = { expanded = false; onDelete() }
            )
        }
    }
}

@Composable
private fun StaplesScreen(
    staples: List<Staple>,
    entries: List<ShoppingEntry>,
    storeOrder: List<String>,
    onAddToList: (Staple) -> Unit,
    onEdit: (Staple) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    val pendingKeys = entries.filterNot { it.bought }.map { it.name to it.store }.toSet()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("忘れずに買うものを登録", fontSize = 14.sp, color = palette.secondaryText)
                Text("定番品 ${staples.size} 点", fontSize = 12.sp, color = palette.secondaryText)
            }
        }
        if (staples.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 430.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(shape = CircleShape, color = palette.raised, modifier = Modifier.size(78.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Refresh, contentDescription = null, tint = palette.text, modifier = Modifier.size(34.dp))
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("いつもの品を登録しましょう", fontWeight = FontWeight.SemiBold, color = palette.text, fontSize = 17.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("定番品は、必要なときに買い物リストへ\nワンタップで追加できます。", color = palette.secondaryText, fontSize = 14.sp, lineHeight = 21.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(18.dp))
                    Text("右下の＋から登録できます", color = palette.secondaryText, fontSize = 13.sp)
                }
            }
        } else {
            staples.groupBy { it.store }.toSortedMap(storeOrderComparator(storeOrder)).forEach { (store, group) ->
                item(key = "staple-heading:$store") {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(store, color = palette.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("${group.size} 点", color = palette.secondaryText, fontSize = 12.sp)
                    }
                }
                item(key = "staple-card:$store") {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.surface),
                        border = BorderStroke(1.dp, palette.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (palette.background == LightPalette.background) 1.dp else 0.dp)
                    ) {
                        group.forEachIndexed { index, staple ->
                            val alreadyAdded = staple.name to staple.store in pendingKeys
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProductMark(staple.name, Modifier.size(48.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(staple.name, color = palette.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(
                                        "${staple.memo.ifBlank { staple.store }} · ${staple.quantity}個",
                                        color = palette.secondaryText,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = { if (!alreadyAdded) onAddToList(staple) }, enabled = !alreadyAdded) {
                                    Icon(
                                        if (alreadyAdded) Icons.Outlined.Check else Icons.Outlined.Add,
                                        contentDescription = if (alreadyAdded) "追加済み" else "買い物リストに追加",
                                        tint = if (alreadyAdded) palette.positive else palette.accent
                                    )
                                }
                                EntryMenu(onEdit = { onEdit(staple) }, onDelete = { onDelete(staple.id) })
                            }
                            if (index != group.lastIndex) HorizontalDivider(color = palette.outline, modifier = Modifier.padding(start = 72.dp))
                        }
                    }
                }
            }
            item {
                Text("追加済みの定番品にはチェックが表示されます", color = palette.secondaryText, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun SettingsScreen(
    theme: AppTheme,
    showTabLabels: Boolean,
    showFabLabels: Boolean,
    accentCode: String,
    stores: List<String>,
    transferStore: String,
    onTransferStoreChange: (String) -> Unit,
    onReorderStores: (Int, Int) -> Unit,
    onThemeChange: (AppTheme) -> Unit,
    onTabLabelsChange: (Boolean) -> Unit,
    onFabLabelsChange: (Boolean) -> Unit,
    onAccentCodeChange: (String) -> Unit,
    onAddStore: (String) -> Boolean,
    onDeleteStore: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImport: () -> Unit,
    onExportShoppingList: () -> Unit,
    onExportStaples: () -> Unit,
    onShareShoppingList: () -> Unit,
    onShareStaples: () -> Unit,
    onClearData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    var storeDraft by remember { mutableStateOf("") }
    val accentValid = accentCodeIsValid(accentCode)
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("表示", "見やすさを自分好みに")
            AppTheme.entries.forEach { option -> ThemeChoice(option, selected = theme == option, onClick = { onThemeChange(option) }) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("アクセントカラー", "16進カラーコードで指定")
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = parseAccentCode(accentCode) ?: Color.Transparent,
                        border = BorderStroke(1.dp, palette.outline),
                        modifier = Modifier.size(48.dp)
                    ) { Box(contentAlignment = Alignment.Center) { Text("＋", color = accentContentColor(parseAccentCode(accentCode) ?: palette.accent), fontSize = 22.sp, fontWeight = FontWeight.Bold) } }
                    OutlinedTextField(
                        value = accentCode,
                        onValueChange = onAccentCodeChange,
                        modifier = Modifier.weight(1f),
                        label = { Text("カラーコード") },
                        placeholder = { Text("#777777") },
                        singleLine = true,
                        isError = !accentValid,
                        supportingText = { Text(if (accentValid) "例：#777777 または #AABBCC" else "#RRGGBB 形式で入力") }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("ボタンとナビゲーション", "ラベルを個別に表示・非表示")
            PreferenceSwitch("タブのラベルを表示", if (showTabLabels) "アイコンと文字" else "アイコンのみ", showTabLabels, onTabLabelsChange)
            PreferenceSwitch("追加ボタンの文字を表示", if (showFabLabels) "「今回だけ追加」「定番品を登録」" else "＋アイコンのみ", showFabLabels, onFabLabelsChange)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("購入先ラベル", "上下矢印で並び替えできます")
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = storeDraft,
                            onValueChange = { storeDraft = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("新しい購入先") },
                            placeholder = { Text("例：近所のスーパー") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { if (onAddStore(storeDraft)) storeDraft = "" })
                        )
                        Button(onClick = { if (onAddStore(storeDraft)) storeDraft = "" }, enabled = storeDraft.isNotBlank()) { Text("追加") }
                    }
                    stores.forEachIndexed { index, label ->
                        if (index > 0) HorizontalDivider(color = palette.outline)
                        Row(modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(label, modifier = Modifier.weight(1f), color = palette.text, fontSize = 14.sp)
                            IconButton(
                                onClick = { onReorderStores(index, index - 1) },
                                enabled = index > 0 && label != "未指定"
                            ) {
                                Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "$label を上へ", tint = palette.secondaryText)
                            }
                            IconButton(
                                onClick = { onReorderStores(index, index + 1) },
                                enabled = label != "未指定" && index < stores.lastIndex && stores.getOrNull(index + 1) != "未指定"
                            ) {
                                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "$label を下へ", tint = palette.secondaryText)
                            }
                            if (label == "未指定") {
                                Text("必須", color = palette.secondaryText, fontSize = 11.sp)
                            } else {
                                IconButton(onClick = { onDeleteStore(label) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "$label を削除", tint = palette.secondaryText)
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("バックアップ", "アプリのデータをJSONファイルで保存・復元")
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onExportBackup, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Text("バックアップを書き出す")
                    }
                    OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("バックアップ・リストを読み込む") }
                    Text("読み込み時に、既存データへの追加か置き換えを選べます。", color = palette.secondaryText, fontSize = 12.sp)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("選んで共有・書き出し", "共有先はAndroidの共有シートで選択")
            Text("買い物リストの購入先", color = palette.secondaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (listOf("すべて") + stores).distinct().forEach { store ->
                    FilterChip(
                        selected = transferStore == store,
                        onClick = { onTransferStoreChange(store) },
                        label = { Text(store, fontSize = 13.sp) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.accentSoft,
                            selectedLabelColor = palette.text,
                            selectedLeadingIconColor = palette.accent,
                            selectedTrailingIconColor = palette.accent,
                            containerColor = palette.surface,
                            labelColor = palette.text
                        )
                    )
                }
            }
            TransferCard(
                title = "買い物リスト",
                subtitle = if (transferStore == "すべて") "すべての購入先" else "$transferStore の商品だけ",
                onExport = onExportShoppingList,
                onShare = onShareShoppingList
            )
            TransferCard("定番品", "登録した定番品だけ", onExportStaples, onShareStaples)
            Text("共有するデータは選んだリストだけです。書き出したJSONファイルは暗号化されないため、信頼できる相手とだけ共有してください。", color = palette.secondaryText, fontSize = 12.sp, lineHeight = 18.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("プライバシー", "アプリはリストを送信しません")
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        Surface(shape = CircleShape, color = palette.positiveSoft, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Lock, null, tint = palette.positive, modifier = Modifier.size(18.dp)) }
                        }
                        Text("端末内で暗号化して保存", color = palette.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                    Text("買い物リストは Android Keystore の鍵で AES-GCM 暗号化します。通信権限、広告、アクセス解析はなく、Androidのバックアップと端末間転送も無効です。共有はファイルを選んだときだけ開始します。", color = palette.secondaryText, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("データ管理", "いつでも端末から消去できます")
            OutlinedButton(
                onClick = onClearData,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, palette.danger.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.danger)
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("この端末のデータを削除")
            }
        }

        Text("KagoList  ·  1.2.1  ·  MIT License", modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 18.dp), color = palette.secondaryText, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun PreferenceSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val palette = LocalPalette.current
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Settings, contentDescription = null, tint = palette.secondaryText)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = palette.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = palette.secondaryText, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun TransferCard(title: String, subtitle: String, onExport: () -> Unit, onShare: () -> Unit) {
    val palette = LocalPalette.current
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = palette.surface), border = BorderStroke(1.dp, palette.outline)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = palette.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = palette.secondaryText, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onExport, modifier = Modifier.weight(1f), shape = RoundedCornerShape(13.dp)) { Text("ファイルに保存", maxLines = 1) }
                Button(onClick = onShare, modifier = Modifier.weight(1f), shape = RoundedCornerShape(13.dp)) { Text("共有", maxLines = 1) }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    val palette = LocalPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = palette.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = palette.secondaryText, fontSize = 12.sp)
    }
}

@Composable
private fun ThemeChoice(theme: AppTheme, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalPalette.current
    val swatch = when (theme) {
        AppTheme.LIGHT -> Color(0xFFF6F2EC)
        AppTheme.DARK -> Color(0xFF242729)
        AppTheme.AMOLED -> Color.Black
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) palette.accentSoft else palette.surface),
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) palette.accent.copy(alpha = 0.55f) else palette.outline)
    ) {
        Row(modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(10.dp), color = swatch, border = BorderStroke(1.dp, palette.outline), modifier = Modifier.size(38.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    if (theme == AppTheme.AMOLED) Box(Modifier.size(10.dp).background(palette.accent, CircleShape))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(theme.label, color = palette.text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(
                    when (theme) {
                        AppTheme.LIGHT -> "やわらかな白"
                        AppTheme.DARK -> "目にやさしい暗色"
                        AppTheme.AMOLED -> "黒を基調に省電力"
                    },
                    color = palette.secondaryText,
                    fontSize = 11.sp
                )
            }
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryEditorSheet(
    initialName: String,
    initialStore: String,
    initialMemo: String,
    initialQuantity: Int,
    title: String,
    submitLabel: String,
    stores: List<String>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Int) -> Unit
) {
    val palette = LocalPalette.current
    var name by remember(initialName, title) { mutableStateOf(initialName) }
    var store by remember(initialStore, title) { mutableStateOf(initialStore) }
    var memo by remember(initialMemo, title) { mutableStateOf(initialMemo) }
    var quantity by remember(initialQuantity, title) { mutableStateOf(initialQuantity.coerceIn(1, 999)) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = palette.surface,
        contentColor = palette.text,
        scrimColor = palette.background.copy(alpha = 0.62f),
        tonalElevation = 0.dp,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = palette.outline) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().imePadding().windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = palette.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    if (title == "今回だけ追加") Text("買い物が終わるときに整理できます", color = palette.secondaryText, fontSize = 12.sp)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, contentDescription = "閉じる", tint = palette.secondaryText) }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("商品名") },
                placeholder = { Text("例：たまご") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = palette.text,
                    unfocusedTextColor = palette.text,
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = palette.outline,
                    focusedLabelColor = palette.accent,
                    unfocusedLabelColor = palette.secondaryText,
                    focusedPlaceholderColor = palette.secondaryText,
                    unfocusedPlaceholderColor = palette.secondaryText,
                    cursorColor = palette.accent
                )
            )

            Text("購入先", color = palette.secondaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stores.distinct().forEach { option ->
                    FilterChip(
                        selected = store == option,
                        onClick = { store = option },
                        label = { Text(option, fontSize = 13.sp) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.accentSoft,
                            selectedLabelColor = palette.text,
                            selectedLeadingIconColor = palette.accent,
                            selectedTrailingIconColor = palette.accent,
                            containerColor = palette.surface,
                            labelColor = palette.text
                        )
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("個数", color = palette.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("1〜999個", color = palette.secondaryText, fontSize = 12.sp)
                }
                IconButton(onClick = { quantity = (quantity - 1).coerceAtLeast(1) }, enabled = quantity > 1) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = if (quantity > 1) palette.raised else palette.surface,
                        border = BorderStroke(1.dp, palette.outline)
                    ) { Box(contentAlignment = Alignment.Center) { Text("−", color = if (quantity > 1) palette.text else palette.secondaryText, fontSize = 22.sp) } }
                }
                Text(quantity.toString(), modifier = Modifier.width(42.dp), color = palette.text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                IconButton(onClick = { quantity = (quantity + 1).coerceAtMost(999) }, enabled = quantity < 999) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = if (quantity < 999) palette.raised else palette.surface,
                        border = BorderStroke(1.dp, palette.outline)
                    ) { Box(contentAlignment = Alignment.Center) { Text("＋", color = if (quantity < 999) palette.text else palette.secondaryText, fontSize = 20.sp) } }
                }
            }

            OutlinedTextField(
                value = memo,
                onValueChange = { memo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("メモ（任意）") },
                placeholder = { Text("サイズ、銘柄など") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (name.isNotBlank()) onSubmit(name.trim(), store, memo.trim(), quantity) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = palette.text,
                    unfocusedTextColor = palette.text,
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = palette.outline,
                    focusedLabelColor = palette.accent,
                    unfocusedLabelColor = palette.secondaryText,
                    focusedPlaceholderColor = palette.secondaryText,
                    unfocusedPlaceholderColor = palette.secondaryText,
                    cursorColor = palette.accent
                )
            )

            Button(
                onClick = { onSubmit(name.trim(), store, memo.trim(), quantity) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = accentContentColor(palette.accent),
                    disabledContainerColor = palette.raised,
                    disabledContentColor = palette.secondaryText
                )
            ) {
                Text(submitLabel, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun storeOrderComparator(order: List<String>): Comparator<String> =
    compareBy<String> { store -> order.indexOf(store).let { if (it < 0) Int.MAX_VALUE else it } }.thenBy { it }
