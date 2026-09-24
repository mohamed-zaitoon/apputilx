package com.mohamedzaitoon.example.ui

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import apputilx.Utils
import com.mohamedzaitoon.example.R
import com.mohamedzaitoon.example.theme.DarkMode
import com.mohamedzaitoon.example.theme.LocalGlassEffect
import com.mohamedzaitoon.example.theme.ThemeMode
import com.mohamedzaitoon.example.theme.glassAppBackground
import com.mohamedzaitoon.example.theme.glassContainer
import com.mohamedzaitoon.example.theme.resolveDarkMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.SmallTitle as MiuixSmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog as MiuixOverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference as MiuixArrowPreference
import java.io.File

/**
 * The main screen demonstrating all apputilx library helpers.
 */
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    themeMode: ThemeMode,
    darkMode: DarkMode
) {
    val context = LocalContext.current
    val isDark = resolveDarkMode(darkMode)
    var inputText by remember { mutableStateOf("Hello AppUtilX") }

    // Dialog result state
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    fun showResultText(title: String, rawMsg: String) {
        // Clean and format text nicely into structured lines
        val formattedMsg = rawMsg.lines().joinToString("\n") { line ->
            if (line.contains("=")) {
                line.split(" ").joinToString("\n") { part ->
                    if (part.contains("=")) "• " + part.replace("=", ": ") else part
                }
            } else {
                "• $line"
            }
        }
        dialogTitle = title
        dialogContent = formattedMsg
        showDialog = true
    }

    fun showResult(@StringRes titleResId: Int, msg: String) {
        showResultText(context.resources.getString(titleResId), msg)
    }

    when (themeMode) {
        ThemeMode.MATERIAL3 -> {
            Material3HomeScreen(
                inputText = inputText,
                onInputTextChange = { inputText = it },
                onOpenSettings = onOpenSettings,
                onShowResult = ::showResult,
                onShowResultText = ::showResultText
            )

            // Centered Material 3 AlertDialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = dialogTitle) },
                    text = {
                        Text(
                            text = dialogContent,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(text = stringResource(android.R.string.ok))
                        }
                    }
                )
            }
        }

        ThemeMode.XIAOMI -> {
            MiuixHomeScreen(
                inputText = inputText,
                onInputTextChange = { inputText = it },
                onOpenSettings = onOpenSettings,
                isDark = isDark,
                showDialog = showDialog,
                dialogTitle = dialogTitle,
                dialogContent = dialogContent,
                onDismissDialog = { showDialog = false },
                onShowResult = ::showResult,
                onShowResultText = ::showResultText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Material3HomeScreen(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onShowResult: (Int, String) -> Unit,
    onShowResultText: (String, String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.nav_settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                label = { Text(text = stringResource(R.string.home_input_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            SectionTitle(stringResource(R.string.section_network))
            M3CardContainer {
                M3Item(stringResource(R.string.action_network_state)) {
                    onShowResult(
                        R.string.action_network_state,
                        "Connected=${Utils.isConnected}\n" +
                            "Validated=${Utils.hasValidatedInternet()}\n" +
                            "Metered=${Utils.isConnectionMetered()}"
                    )
                }
                M3Item(stringResource(R.string.action_network_transport)) {
                    onShowResult(
                        R.string.action_network_transport,
                        "Transport=${Utils.activeNetworkTransport()}\n" +
                            "WiFi=${Utils.isWifiConnected()} Cellular=${Utils.isCellularConnected()}\n" +
                            "Ethernet=${Utils.isEthernetConnected()} VPN=${Utils.isVpnConnected()}"
                    )
                }
            }

            SectionTitle(stringResource(R.string.section_intents))
            M3CardContainer {
                M3Item(stringResource(R.string.action_open_url)) {
                    Utils.openUrl(context, "https://apputilx.mohamedzaitoon.com")
                }
                M3Item(stringResource(R.string.action_open_settings)) {
                    Utils.openAppSettings()
                }
                M3Item(stringResource(R.string.action_whatsapp)) {
                    Utils.openWhatsApp("201234567890", "Hello from AppUtilX")
                }
                M3Item(stringResource(R.string.action_dial)) {
                    Utils.dial("201234567890")
                }
                M3Item(stringResource(R.string.action_sms)) {
                    Utils.sendSms("201234567890", inputText.ifBlank { "Hello SMS" })
                }
                M3Item(stringResource(R.string.action_email)) {
                    Utils.sendEmail("test@example.com", "Hello from AppUtilX", inputText.ifBlank { "Message body" })
                }
                M3Item(stringResource(R.string.action_share_text)) {
                    Utils.shareText(inputText.ifBlank { "Shared from AppUtilX" })
                }
                M3Item(stringResource(R.string.action_share_file)) {
                    val file = File(context.cacheDir, "apputilx-demo.txt")
                    file.writeText(inputText.ifBlank { "Demo file content" })
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    Utils.shareFile(uri, "text/plain", "Share file")
                }
                M3Item(stringResource(R.string.action_map)) {
                    Utils.openMap(30.0444, 31.2357, "Cairo")
                }
                M3Item(stringResource(R.string.action_playstore)) {
                    Utils.openPlayStore()
                }
            }

            SectionTitle(stringResource(R.string.section_clipboard))
            M3CardContainer {
                M3Item(stringResource(R.string.action_copy)) {
                    Utils.copyText(inputText.ifBlank { "Copied text" })
                }
                M3Item(stringResource(R.string.action_paste)) {
                    onShowResult(R.string.action_paste, "Clipboard=${Utils.getCopiedText().orEmpty()}")
                }
                M3Item(stringResource(R.string.action_vibrate_short)) {
                    Utils.vibrate(200)
                }
                M3Item(stringResource(R.string.action_vibrate_pattern)) {
                    Utils.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                }
                M3Item(stringResource(R.string.action_block_capture)) {
                    Utils.blockCapture()
                }
                M3Item(stringResource(R.string.action_unblock_capture)) {
                    Utils.unblockCapture()
                }
            }

            SectionTitle(stringResource(R.string.section_notifications))
            M3CardContainer {
                M3Item(stringResource(R.string.action_show_notification)) {
                    Utils.createNotificationChannel("demo", "Demo channel")
                    Utils.showNotification(
                        channelId = "demo",
                        title = "AppUtilX",
                        text = inputText.ifBlank { "Hello Notification" },
                        iconResId = R.drawable.ic_launcher
                    )
                }
                M3Item(stringResource(R.string.action_cancel_notifications)) {
                    Utils.cancelAllNotifications()
                    Utils.deleteNotificationChannel("demo")
                }
                M3Item(stringResource(R.string.action_hide_keyboard)) {
                    Utils.hideKeyboard()
                }
            }

            SectionTitle(stringResource(R.string.section_device))
            M3CardContainer {
                M3Item(stringResource(R.string.action_device_info)) {
                    onShowResult(
                        R.string.action_device_info,
                        "Device=${Utils.deviceName()}\n" +
                            "Brand=${Utils.deviceBrand()} Manufacturer=${Utils.deviceManufacturer()}\n" +
                            "SDK=${Utils.androidSdk()} Android=${Utils.androidVersion()}\n" +
                            "Tablet=${Utils.isTablet()} Emulator=${Utils.isEmulator()}\n" +
                            "ABIs=${Utils.supportedAbis().joinToString()}"
                    )
                }
                M3Item(stringResource(R.string.action_app_info)) {
                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    onShowResult(
                        R.string.action_app_info,
                        "Name=${Utils.appName()}\n" +
                            "Package=${Utils.appPackageName()}\n" +
                            "Version=${Utils.appVersionName()} (${Utils.appVersionCode()})\n" +
                            "Debuggable=${Utils.isDebuggable()}\n" +
                            "Installer=${Utils.appInstallerPackageName() ?: "unknown"}\n" +
                            "WhatsApp_Installed=${Utils.isPackageInstalled("com.whatsapp")}\n" +
                            "Camera_Granted=${Utils.arePermissionsGranted(cameraPermissions)}"
                    )
                }
                M3Item(stringResource(R.string.action_battery_info)) {
                    onShowResult(
                        R.string.action_battery_info,
                        "Level=${Utils.getBatteryLevel()}%\nCharging=${Utils.isCharging()}\n" +
                            "Type=${Utils.getChargingType()}\nStatus=${Utils.getBatteryStatus()}\n" +
                            "Health=${Utils.getBatteryHealth()}\nPowerSave=${Utils.isPowerSaveMode()}"
                    )
                }
                M3Item(stringResource(R.string.action_app_state)) {
                    onShowResult(
                        R.string.action_app_state,
                        "Foreground=${Utils.isAppInForeground()}\nBackground=${Utils.isAppInBackground()}\n" +
                            "ScreenOn=${Utils.isScreenOn()}\nLowRam=${Utils.isLowRamDevice()}"
                    )
                }
            }

            SectionTitle(stringResource(R.string.section_data))
            M3CardContainer {
                M3Item(stringResource(R.string.action_time_now)) {
                    val now = Utils.now()
                    val formatted = Utils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
                    val ago = Utils.timeAgo(now - 5 * 60 * 1000)
                    onShowResult(R.string.action_time_now, "Now=$formatted\nFiveMinAgo=$ago")
                }
                M3Item(stringResource(R.string.action_validate)) {
                    val emailValid = Utils.isValidEmail("test@example.com")
                    val phoneValid = Utils.isValidPhone("201234567890")
                    val urlValid = Utils.isValidUrl("https://example.com")
                    onShowResult(
                        R.string.action_validate,
                        "Email_Valid=$emailValid\nPhone_Valid=$phoneValid\nUrl_Valid=$urlValid"
                    )
                }
                M3Item(stringResource(R.string.action_storage)) {
                    val free = Utils.getFreeStorage()
                    val total = Utils.getTotalStorage()
                    val used = Utils.getUsedStorage()
                    onShowResult(
                        R.string.action_storage,
                        "Free=${Utils.formatBytes(free)}\n" +
                            "Used=${Utils.formatBytes(used)}\n" +
                            "Total=${Utils.formatBytes(total)}"
                    )
                }
                M3Item(stringResource(R.string.action_write_file)) {
                    val content = inputText.ifBlank { "Hello File" }
                    Utils.writeFile("demo.txt", content)
                    Utils.appendFile("demo.txt", "\nAppended at ${Utils.formatTime(Utils.now(), "HH:mm:ss")}")
                    Utils.writeFileBytes("demo.bin", content.toByteArray())
                }
                M3Item(stringResource(R.string.action_read_file)) {
                    val text = Utils.readFile("demo.txt").orEmpty()
                    onShowResult(
                        R.string.action_read_file,
                        "Files=${Utils.listFiles().joinToString()}\n\nContent:\n$text"
                    )
                }
                M3Item(stringResource(R.string.action_delete_file)) {
                    Utils.deleteFile("demo.txt")
                    Utils.deleteFile("demo.bin")
                }
                M3Item(stringResource(R.string.action_sha256)) {
                    val text = inputText.ifBlank { "password" }
                    onShowResult(
                        R.string.action_sha256,
                        "SHA256=${Utils.sha256(text)}\n\nSHA512=${Utils.sha512(text)}"
                    )
                }
                M3Item(stringResource(R.string.action_base64)) {
                    val text = inputText.ifBlank { "Hello Base64" }
                    val encoded = Utils.base64Encode(text)
                    val decoded = Utils.base64Decode(encoded)
                    onShowResult(R.string.action_base64, "Base64=$encoded\nDecoded=$decoded")
                }
            }

            SectionTitle(stringResource(R.string.section_security))
            M3CardContainer {
                M3Item(stringResource(R.string.action_signatures)) {
                    val primary = Utils.getPrimarySignatureSHA1()
                    val count = Utils.getAppSignatures().size
                    onShowResult(R.string.action_signatures, "Primary_SHA1=$primary\nSignatures_Count=$count")
                }
                M3Item(stringResource(R.string.action_biometric)) {
                    val activity = context as? FragmentActivity
                    if (activity != null && Utils.canUseBiometric()) {
                        Utils.authenticateBiometric(
                            activity = activity,
                            title = "Biometric Authentication",
                            subtitle = "Authenticate to test AppUtilX",
                            onSuccess = { onShowResultText("Biometric", "• Status: Authentication Successful!") },
                            onError = { code, msg -> onShowResultText("Biometric Error", "• Error Code: $code\n• Message: $msg") },
                            onFailed = { onShowResultText("Biometric", "• Status: Authentication Failed") }
                        )
                    } else {
                        onShowResultText("Biometric", "• Status: Biometric authentication unavailable")
                    }
                }
                M3Item(stringResource(R.string.action_logger)) {
                    Utils.log("Demo", "Info log triggered")
                    Utils.logWarning("Demo", "Warning log triggered")
                    Utils.logError("Demo", "Error log triggered")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun M3CardContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun M3Item(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MiuixHomeScreen(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onOpenSettings: () -> Unit,
    isDark: Boolean,
    showDialog: Boolean,
    dialogTitle: String,
    dialogContent: String,
    onDismissDialog: () -> Unit,
    onShowResult: (Int, String) -> Unit,
    onShowResultText: (String, String) -> Unit
) {
    val context = LocalContext.current
    val cardColors = MiuixCardDefaults.defaultColors(color = Color.Transparent)
    val isGlass = LocalGlassEffect.current

    MiuixScaffold(
        topBar = {
            MiuixSmallTopAppBar(
                title = stringResource(R.string.home_title),
                color = if (isGlass) Color.Transparent else MiuixTheme.colorScheme.surface,
                modifier = if (isGlass) {
                    Modifier
                        .padding(horizontal = 12.dp)
                        .glassContainer(isDark = isDark, cornerRadius = 16.dp)
                } else Modifier,
                actions = {
                    MiuixIconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        MiuixIcon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.nav_settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(glassAppBackground(isDark))
                .verticalScroll(rememberScrollState())
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            // Input Field Card
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(16.dp)
            ) {
                MiuixTextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    label = stringResource(R.string.home_input_hint),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------------- Network ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_network))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_network_state),
                    onClick = {
                        onShowResult(
                            R.string.action_network_state,
                            "Connected=${Utils.isConnected}\n" +
                                "Validated=${Utils.hasValidatedInternet()}\n" +
                                "Metered=${Utils.isConnectionMetered()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_network_transport),
                    onClick = {
                        onShowResult(
                            R.string.action_network_transport,
                            "Transport=${Utils.activeNetworkTransport()}\n" +
                                "WiFi=${Utils.isWifiConnected()} Cellular=${Utils.isCellularConnected()}\n" +
                                "Ethernet=${Utils.isEthernetConnected()} VPN=${Utils.isVpnConnected()}"
                        )
                    }
                )
            }

            // ---------------- Intents ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_intents))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_open_url),
                    onClick = {
                        Utils.openUrl(context, "https://apputilx.mohamedzaitoon.com")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_open_settings),
                    onClick = {
                        Utils.openAppSettings()
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_whatsapp),
                    onClick = {
                        Utils.openWhatsApp("201234567890", "Hello from AppUtilX")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_dial),
                    onClick = {
                        Utils.dial("201234567890")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_sms),
                    onClick = {
                        Utils.sendSms("201234567890", inputText.ifBlank { "Hello SMS" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_email),
                    onClick = {
                        Utils.sendEmail("test@example.com", "Hello from AppUtilX", inputText.ifBlank { "Message body" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_share_text),
                    onClick = {
                        Utils.shareText(inputText.ifBlank { "Shared from AppUtilX" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_share_file),
                    onClick = {
                        val file = File(context.cacheDir, "apputilx-demo.txt")
                        file.writeText(inputText.ifBlank { "Demo file content" })
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        Utils.shareFile(uri, "text/plain", "Share file")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_map),
                    onClick = {
                        Utils.openMap(30.0444, 31.2357, "Cairo")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_playstore),
                    onClick = {
                        Utils.openPlayStore()
                    }
                )
            }

            // ---------------- Clipboard / Vibration / Screen ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_clipboard))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_copy),
                    onClick = {
                        Utils.copyText(inputText.ifBlank { "Copied text" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_paste),
                    onClick = {
                        onShowResult(R.string.action_paste, "Clipboard=${Utils.getCopiedText().orEmpty()}")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_vibrate_short),
                    onClick = {
                        Utils.vibrate(200)
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_vibrate_pattern),
                    onClick = {
                        Utils.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_block_capture),
                    onClick = {
                        Utils.blockCapture()
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_unblock_capture),
                    onClick = {
                        Utils.unblockCapture()
                    }
                )
            }

            // ---------------- Notifications / Keyboard ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_notifications))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_show_notification),
                    onClick = {
                        Utils.createNotificationChannel("demo", "Demo channel")
                        Utils.showNotification(
                            channelId = "demo",
                            title = "AppUtilX",
                            text = inputText.ifBlank { "Hello Notification" },
                            iconResId = R.drawable.ic_launcher
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_cancel_notifications),
                    onClick = {
                        Utils.cancelAllNotifications()
                        Utils.deleteNotificationChannel("demo")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_hide_keyboard),
                    onClick = {
                        Utils.hideKeyboard()
                    }
                )
            }

            // ---------------- Device & App ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_device))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_device_info),
                    onClick = {
                        onShowResult(
                            R.string.action_device_info,
                            "Device=${Utils.deviceName()}\n" +
                                "Brand=${Utils.deviceBrand()} Manufacturer=${Utils.deviceManufacturer()}\n" +
                                "SDK=${Utils.androidSdk()} Android=${Utils.androidVersion()}\n" +
                                "Tablet=${Utils.isTablet()} Emulator=${Utils.isEmulator()}\n" +
                                "ABIs=${Utils.supportedAbis().joinToString()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_app_info),
                    onClick = {
                        val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                        onShowResult(
                            R.string.action_app_info,
                            "Name=${Utils.appName()}\n" +
                                "Package=${Utils.appPackageName()}\n" +
                                "Version=${Utils.appVersionName()} (${Utils.appVersionCode()})\n" +
                                "Debuggable=${Utils.isDebuggable()}\n" +
                                "Installer=${Utils.appInstallerPackageName() ?: "unknown"}\n" +
                                "WhatsApp_Installed=${Utils.isPackageInstalled("com.whatsapp")}\n" +
                                "Camera_Granted=${Utils.arePermissionsGranted(cameraPermissions)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_battery_info),
                    onClick = {
                        onShowResult(
                            R.string.action_battery_info,
                            "Level=${Utils.getBatteryLevel()}%\nCharging=${Utils.isCharging()}\n" +
                                "Type=${Utils.getChargingType()}\nStatus=${Utils.getBatteryStatus()}\n" +
                                "Health=${Utils.getBatteryHealth()}\nPowerSave=${Utils.isPowerSaveMode()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_app_state),
                    onClick = {
                        onShowResult(
                            R.string.action_app_state,
                            "Foreground=${Utils.isAppInForeground()}\nBackground=${Utils.isAppInBackground()}\n" +
                                "ScreenOn=${Utils.isScreenOn()}\nLowRam=${Utils.isLowRamDevice()}"
                        )
                    }
                )
            }

            // ---------------- Data ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_data))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_time_now),
                    onClick = {
                        val now = Utils.now()
                        val formatted = Utils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
                        val ago = Utils.timeAgo(now - 5 * 60 * 1000)
                        onShowResult(R.string.action_time_now, "Now=$formatted\nFiveMinAgo=$ago")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_validate),
                    onClick = {
                        val emailValid = Utils.isValidEmail("test@example.com")
                        val phoneValid = Utils.isValidPhone("201234567890")
                        val urlValid = Utils.isValidUrl("https://example.com")
                        onShowResult(
                            R.string.action_validate,
                            "Email_Valid=$emailValid\nPhone_Valid=$phoneValid\nUrl_Valid=$urlValid"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_storage),
                    onClick = {
                        val free = Utils.getFreeStorage()
                        val total = Utils.getTotalStorage()
                        val used = Utils.getUsedStorage()
                        onShowResult(
                            R.string.action_storage,
                            "Free=${Utils.formatBytes(free)}\n" +
                                "Used=${Utils.formatBytes(used)}\n" +
                                "Total=${Utils.formatBytes(total)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_write_file),
                    onClick = {
                        val content = inputText.ifBlank { "Hello File" }
                        Utils.writeFile("demo.txt", content)
                        Utils.appendFile("demo.txt", "\nAppended at ${Utils.formatTime(Utils.now(), "HH:mm:ss")}")
                        Utils.writeFileBytes("demo.bin", content.toByteArray())
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_read_file),
                    onClick = {
                        val text = Utils.readFile("demo.txt").orEmpty()
                        onShowResult(
                            R.string.action_read_file,
                            "Files=${Utils.listFiles().joinToString()}\n\nContent:\n$text"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_delete_file),
                    onClick = {
                        Utils.deleteFile("demo.txt")
                        Utils.deleteFile("demo.bin")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_sha256),
                    onClick = {
                        val text = inputText.ifBlank { "password" }
                        onShowResult(
                            R.string.action_sha256,
                            "SHA256=${Utils.sha256(text)}\n\nSHA512=${Utils.sha512(text)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_base64),
                    onClick = {
                        val text = inputText.ifBlank { "Hello Base64" }
                        val encoded = Utils.base64Encode(text)
                        val decoded = Utils.base64Decode(encoded)
                        onShowResult(R.string.action_base64, "Base64=$encoded\nDecoded=$decoded")
                    }
                )
            }

            // ---------------- Security ----------------
            MiuixSmallTitle(text = stringResource(R.string.section_security))
            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 4.dp)
            ) {
                MiuixArrowPreference(
                    title = stringResource(R.string.action_signatures),
                    onClick = {
                        val primary = Utils.getPrimarySignatureSHA1()
                        val count = Utils.getAppSignatures().size
                        onShowResult(R.string.action_signatures, "Primary_SHA1=$primary\nSignatures_Count=$count")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_biometric),
                    onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null && Utils.canUseBiometric()) {
                            Utils.authenticateBiometric(
                                activity = activity,
                                title = "Biometric Authentication",
                                subtitle = "Authenticate to test AppUtilX",
                                onSuccess = { onShowResultText("Biometric", "• Status: Authentication Successful!") },
                                onError = { code, msg -> onShowResultText("Biometric Error", "• Error Code: $code\n• Message: $msg") },
                                onFailed = { onShowResultText("Biometric", "• Status: Authentication Failed") }
                            )
                        } else {
                            onShowResultText("Biometric", "• Status: Biometric authentication unavailable")
                        }
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_logger),
                    onClick = {
                        Utils.log("Demo", "Info log triggered")
                        Utils.logWarning("Demo", "Warning log triggered")
                        Utils.logError("Demo", "Error log triggered")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Xiaomi HyperOS Glass Bottom Overlay Dialog
        MiuixOverlayDialog(
            title = dialogTitle,
            show = showDialog,
            onDismissRequest = onDismissDialog
        ) {
            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(16.dp)
            ) {
                MiuixText(
                    text = dialogContent,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            MiuixButton(
                onClick = onDismissDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp)
            ) {
                MiuixText(text = "OK")
            }
        }
    }
}
