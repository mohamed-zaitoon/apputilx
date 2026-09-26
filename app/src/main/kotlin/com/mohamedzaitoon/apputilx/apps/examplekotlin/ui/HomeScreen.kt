package com.mohamedzaitoon.apputilx.apps.examplekotlin.ui

import android.Manifest
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.mohamedzaitoon.apputilx.apps.examplekotlin.R
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.DarkMode
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.LocalGlassEffect
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.ThemeMode
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.glassAppBackground
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.glassContainer
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.resolveDarkMode
import com.mohamedzaitoon.apputilx.AppUtilX
import com.mohamedzaitoon.apputilx.app.AppInfo
import com.mohamedzaitoon.apputilx.app.AppState
import com.mohamedzaitoon.apputilx.app.Notification
import com.mohamedzaitoon.apputilx.app.Permission
import com.mohamedzaitoon.apputilx.app.Signature
import com.mohamedzaitoon.apputilx.content.Clipboard
import com.mohamedzaitoon.apputilx.content.Intent
import com.mohamedzaitoon.apputilx.hardware.Audio
import com.mohamedzaitoon.apputilx.hardware.Battery
import com.mohamedzaitoon.apputilx.hardware.Biometric
import com.mohamedzaitoon.apputilx.hardware.Device
import com.mohamedzaitoon.apputilx.hardware.Display
import com.mohamedzaitoon.apputilx.hardware.Vibration
import com.mohamedzaitoon.apputilx.io.Storage
import com.mohamedzaitoon.apputilx.io.File as AppFile
import com.mohamedzaitoon.apputilx.net.Network
import com.mohamedzaitoon.apputilx.security.Encryption
import com.mohamedzaitoon.apputilx.util.Time
import com.mohamedzaitoon.apputilx.util.Validation
import com.mohamedzaitoon.apputilx.view.Browser
import com.mohamedzaitoon.apputilx.view.Keyboard
import com.mohamedzaitoon.apputilx.view.Screen
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

@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    themeMode: ThemeMode,
    darkMode: DarkMode
) {
    var inputText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }

    val isDark = resolveDarkMode(darkMode)

    fun onShowResult(title: String, content: String) {
        dialogTitle = title
        dialogContent = content
        showDialog = true
    }

    if (themeMode == ThemeMode.XIAOMI) {
        MiuixHomeScreen(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onOpenSettings = onOpenSettings,
            isDark = isDark,
            showDialog = showDialog,
            dialogTitle = dialogTitle,
            dialogContent = dialogContent,
            onDismissDialog = { showDialog = false },
            onShowResult = { title, content -> onShowResult(title, content) }
        )
    } else {
        Material3HomeScreen(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onOpenSettings = onOpenSettings,
            showDialog = showDialog,
            dialogTitle = dialogTitle,
            dialogContent = dialogContent,
            onDismissDialog = { showDialog = false },
            onShowResult = { title, content -> onShowResult(title, content) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Material3HomeScreen(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onOpenSettings: () -> Unit,
    showDialog: Boolean,
    dialogTitle: String,
    dialogContent: String,
    onDismissDialog: () -> Unit,
    onShowResult: (String, String) -> Unit
) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(dialogTitle) },
            text = { Text(dialogContent) },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                label = { Text(stringResource(R.string.home_input_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle(stringResource(R.string.section_network))
            M3CardContainer {
                M3Item(stringResource(R.string.action_network_state)) {
                    onShowResult(
                        "Network State",
                        "Connected=${Network.isConnected}\n" +
                            "Validated=${Network.hasValidatedInternet()}\n" +
                            "Metered=${Network.isConnectionMetered()}"
                    )
                }
                M3Item(stringResource(R.string.action_network_transport)) {
                    onShowResult(
                        "Network Transport",
                        "Transport=${Network.activeTransport()}\n" +
                            "WiFi=${Network.isWifiConnected()} Cellular=${Network.isCellularConnected()}\n" +
                            "Ethernet=${Network.isEthernetConnected()} VPN=${Network.isVpnConnected()}"
                    )
                }
            }

            SectionTitle(stringResource(R.string.section_intents))
            M3CardContainer {
                M3Item(stringResource(R.string.action_open_url)) {
                    Browser.openUrl("https://apputilx.mohamedzaitoon.com")
                }
                M3Item(stringResource(R.string.action_open_settings)) {
                    Intent.openAppSettings()
                }
                M3Item(stringResource(R.string.action_whatsapp)) {
                    Intent.openWhatsApp("201234567890", "Hello from AppUtilX")
                }
                M3Item(stringResource(R.string.action_dial)) {
                    Intent.dial("201234567890")
                }
                M3Item(stringResource(R.string.action_sms)) {
                    Intent.sendSms("201234567890", inputText.ifBlank { "Hello SMS" })
                }
                M3Item(stringResource(R.string.action_email)) {
                    Intent.sendEmail("test@example.com", "Hello from AppUtilX", inputText.ifBlank { "Message body" })
                }
                M3Item(stringResource(R.string.action_share_text)) {
                    Intent.shareText(inputText.ifBlank { "Shared from AppUtilX" })
                }
                M3Item(stringResource(R.string.action_share_file)) {
                    val file = File(context.cacheDir, "apputilx-demo.txt")
                    file.writeText(inputText.ifBlank { "Demo file content" })
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    Intent.shareFile(uri, "text/plain", "Share file")
                }
                M3Item(stringResource(R.string.action_map)) {
                    Intent.openMap(30.0444, 31.2357, "Cairo")
                }
                M3Item(stringResource(R.string.action_playstore)) {
                    Intent.openPlayStore()
                }
            }

            SectionTitle(stringResource(R.string.section_clipboard))
            M3CardContainer {
                M3Item(stringResource(R.string.action_copy)) {
                    Clipboard.copyText(inputText.ifBlank { "Copied text" })
                }
                M3Item(stringResource(R.string.action_paste)) {
                    onShowResult("Clipboard", "Clipboard=${Clipboard.getText().orEmpty()}")
                }
                M3Item(stringResource(R.string.action_vibrate_short)) {
                    Vibration.vibrate(200)
                }
                M3Item(stringResource(R.string.action_vibrate_pattern)) {
                    Vibration.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                }
                M3Item(stringResource(R.string.action_block_capture)) {
                    Screen.blockCapture()
                }
                M3Item(stringResource(R.string.action_unblock_capture)) {
                    Screen.unblockCapture()
                }
            }

            SectionTitle(stringResource(R.string.section_notifications))
            M3CardContainer {
                M3Item(stringResource(R.string.action_show_notification)) {
                    Notification.createChannel("demo", "Demo channel")
                    Notification.showNotification(
                        channelId = "demo",
                        title = "AppUtilX",
                        text = inputText.ifBlank { "Hello Notification" },
                        iconResId = R.drawable.ic_launcher
                    )
                }
                M3Item(stringResource(R.string.action_cancel_notifications)) {
                    Notification.cancelAll()
                    Notification.deleteChannel("demo")
                }
                M3Item(stringResource(R.string.action_hide_keyboard)) {
                    Keyboard.hideKeyboard()
                }
            }

            SectionTitle(stringResource(R.string.section_device))
            M3CardContainer {
                M3Item(stringResource(R.string.action_device_info)) {
                    onShowResult(
                        "Device Info",
                        "Device=${Device.deviceName()}\n" +
                            "Brand=${Device.brand()} Manufacturer=${Device.manufacturer()}\n" +
                            "SDK=${Device.sdk()} Android=${Device.androidVersion()}\n" +
                            "Tablet=${Device.isTablet()} Emulator=${Device.isEmulator()}\n" +
                            "ABIs=${Device.supportedAbis().joinToString()}"
                    )
                }
                M3Item(stringResource(R.string.action_app_info)) {
                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    onShowResult(
                        "App Info",
                        "Name=${AppInfo.appName()}\n" +
                            "Package=${AppInfo.packageName()}\n" +
                            "Version=${AppInfo.versionName()} (${AppInfo.versionCode()})\n" +
                            "Debuggable=${AppInfo.isDebuggable()}\n" +
                            "Installer=${AppInfo.installerPackageName() ?: "unknown"}\n" +
                            "WhatsApp_Installed=${AppInfo.isPackageInstalled("com.whatsapp")}\n" +
                            "Camera_Granted=${Permission.areGranted(cameraPermissions)}"
                    )
                }
                M3Item(stringResource(R.string.action_battery_info)) {
                    onShowResult(
                        "Battery Info",
                        "Level=${Battery.getBatteryLevel()}%\nCharging=${Battery.isCharging()}\n" +
                            "Type=${Battery.getChargingType()}\nStatus=${Battery.getBatteryStatus()}\n" +
                            "Health=${Battery.getBatteryHealth()}\nPowerSave=${Battery.isPowerSaveMode()}"
                    )
                }
                M3Item(stringResource(R.string.action_app_state)) {
                    onShowResult(
                        "App State",
                        "Foreground=${AppState.isAppInForeground()}\nBackground=${AppState.isAppInBackground()}\n" +
                            "ScreenOn=${AppState.isScreenOn()}\nLowRam=${AppState.isLowRamDevice()}"
                    )
                }
            }

            SectionTitle(stringResource(R.string.section_data))
            M3CardContainer {
                M3Item(stringResource(R.string.action_time_now)) {
                    val now = Time.now()
                    val formatted = Time.format(now, "yyyy-MM-dd HH:mm:ss")
                    val ago = Time.timeAgo(now - 5 * 60 * 1000)
                    onShowResult("Time", "Now=$formatted\nFiveMinAgo=$ago")
                }
                M3Item(stringResource(R.string.action_validate)) {
                    val emailValid = Validation.isValidEmail("test@example.com")
                    val phoneValid = Validation.isValidPhone("201234567890")
                    val urlValid = Validation.isValidUrl("https://example.com")
                    onShowResult(
                        "Validation",
                        "Email_Valid=$emailValid\nPhone_Valid=$phoneValid\nUrl_Valid=$urlValid"
                    )
                }
                M3Item(stringResource(R.string.action_storage)) {
                    val free = Storage.getFreeInternalStorage()
                    val total = Storage.getTotalInternalStorage()
                    val used = Storage.getUsedInternalStorage()
                    onShowResult(
                        "Storage",
                        "Free=${Storage.formatBytes(free)}\n" +
                            "Used=${Storage.formatBytes(used)}\n" +
                            "Total=${Storage.formatBytes(total)}"
                    )
                }
                M3Item(stringResource(R.string.action_write_file)) {
                    val content = inputText.ifBlank { "Hello File" }
                    AppFile.writeText("demo.txt", content)
                    AppFile.appendText("demo.txt", "\nAppended at ${Time.format(Time.now(), "HH:mm:ss")}")
                    AppFile.writeBytes("demo.bin", content.toByteArray())
                }
                M3Item(stringResource(R.string.action_read_file)) {
                    val text = AppFile.readText("demo.txt").orEmpty()
                    onShowResult(
                        "File Read",
                        "Files=${AppFile.list().joinToString()}\n\nContent:\n$text"
                    )
                }
                M3Item(stringResource(R.string.action_delete_file)) {
                    AppFile.delete("demo.txt")
                    AppFile.delete("demo.bin")
                }
                M3Item(stringResource(R.string.action_sha256)) {
                    val text = inputText.ifBlank { "password" }
                    onShowResult(
                        "Crypto",
                        "SHA256=${Encryption.sha256(text)}\n\nSHA512=${Encryption.sha512(text)}"
                    )
                }
                M3Item(stringResource(R.string.action_base64)) {
                    val text = inputText.ifBlank { "Hello Base64" }
                    val encoded = Encryption.base64Encode(text)
                    val decoded = Encryption.base64Decode(encoded)
                    onShowResult("Base64", "Base64=$encoded\nDecoded=$decoded")
                }
            }

            SectionTitle(stringResource(R.string.section_security))
            M3CardContainer {
                M3Item(stringResource(R.string.action_signatures)) {
                    val primary = Signature.getAppPrimarySignatureSHA1()
                    val count = Signature.getAppSignatures().size
                    onShowResult("Signatures", "Primary_SHA1=$primary\nSignatures_Count=$count")
                }
                M3Item(stringResource(R.string.action_biometric)) {
                    val activity = context as? FragmentActivity
                    if (activity != null && Biometric.canAuthenticate()) {
                        Biometric.authenticate(
                            activity = activity,
                            title = "Biometric Authentication",
                            subtitle = "Authenticate to test AppUtilX",
                            onSuccess = { onShowResult("Biometric", "• Status: Authentication Successful!") },
                            onError = { code: Int, msg: CharSequence -> onShowResult("Biometric Error", "• Error Code: $code\n• Message: $msg") },
                            onFailed = { onShowResult("Biometric", "• Status: Authentication Failed") }
                        )
                    } else {
                        onShowResult("Biometric", "• Status: Biometric authentication unavailable")
                    }
                }
                M3Item(stringResource(R.string.action_logger)) {
                    com.mohamedzaitoon.apputilx.AppUtilX.log("Demo", "Info log triggered")
                    com.mohamedzaitoon.apputilx.AppUtilX.logWarning("Demo", "Warning log triggered")
                    com.mohamedzaitoon.apputilx.AppUtilX.logError("Demo", "Error log triggered")
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
    onShowResult: (String, String) -> Unit
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
                            "Network State",
                            "Connected=${Network.isConnected}\n" +
                                "Validated=${Network.hasValidatedInternet()}\n" +
                                "Metered=${Network.isConnectionMetered()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_network_transport),
                    onClick = {
                        onShowResult(
                            "Network Transport",
                            "Transport=${Network.activeTransport()}\n" +
                                "WiFi=${Network.isWifiConnected()} Cellular=${Network.isCellularConnected()}\n" +
                                "Ethernet=${Network.isEthernetConnected()} VPN=${Network.isVpnConnected()}"
                        )
                    }
                )
            }

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
                        Browser.openUrl("https://apputilx.mohamedzaitoon.com")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_open_settings),
                    onClick = {
                        Intent.openAppSettings()
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_whatsapp),
                    onClick = {
                        Intent.openWhatsApp("201234567890", "Hello from AppUtilX")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_dial),
                    onClick = {
                        Intent.dial("201234567890")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_sms),
                    onClick = {
                        Intent.sendSms("201234567890", inputText.ifBlank { "Hello SMS" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_email),
                    onClick = {
                        Intent.sendEmail("test@example.com", "Hello from AppUtilX", inputText.ifBlank { "Message body" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_share_text),
                    onClick = {
                        Intent.shareText(inputText.ifBlank { "Shared from AppUtilX" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_share_file),
                    onClick = {
                        val file = File(context.cacheDir, "apputilx-demo.txt")
                        file.writeText(inputText.ifBlank { "Demo file content" })
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        Intent.shareFile(uri, "text/plain", "Share file")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_map),
                    onClick = {
                        Intent.openMap(30.0444, 31.2357, "Cairo")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_playstore),
                    onClick = {
                        Intent.openPlayStore()
                    }
                )
            }

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
                        Clipboard.copyText(inputText.ifBlank { "Copied text" })
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_paste),
                    onClick = {
                        onShowResult("Clipboard", "Clipboard=${Clipboard.getText().orEmpty()}")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_vibrate_short),
                    onClick = {
                        Vibration.vibrate(200)
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_vibrate_pattern),
                    onClick = {
                        Vibration.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_block_capture),
                    onClick = {
                        Screen.blockCapture()
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_unblock_capture),
                    onClick = {
                        Screen.unblockCapture()
                    }
                )
            }

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
                        Notification.createChannel("demo", "Demo channel")
                        Notification.showNotification(
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
                        Notification.cancelAll()
                        Notification.deleteChannel("demo")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_hide_keyboard),
                    onClick = {
                        Keyboard.hideKeyboard()
                    }
                )
            }

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
                            "Device Info",
                            "Device=${Device.deviceName()}\n" +
                                "Brand=${Device.brand()} Manufacturer=${Device.manufacturer()}\n" +
                                "SDK=${Device.sdk()} Android=${Device.androidVersion()}\n" +
                                "Tablet=${Device.isTablet()} Emulator=${Device.isEmulator()}\n" +
                                "ABIs=${Device.supportedAbis().joinToString()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_app_info),
                    onClick = {
                        val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                        onShowResult(
                            "App Info",
                            "Name=${AppInfo.appName()}\n" +
                                "Package=${AppInfo.packageName()}\n" +
                                "Version=${AppInfo.versionName()} (${AppInfo.versionCode()})\n" +
                                "Debuggable=${AppInfo.isDebuggable()}\n" +
                                "Installer=${AppInfo.installerPackageName() ?: "unknown"}\n" +
                                "WhatsApp_Installed=${AppInfo.isPackageInstalled("com.whatsapp")}\n" +
                                "Camera_Granted=${Permission.areGranted(cameraPermissions)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_battery_info),
                    onClick = {
                        onShowResult(
                            "Battery Info",
                            "Level=${Battery.getBatteryLevel()}%\nCharging=${Battery.isCharging()}\n" +
                                "Type=${Battery.getChargingType()}\nStatus=${Battery.getBatteryStatus()}\n" +
                                "Health=${Battery.getBatteryHealth()}\nPowerSave=${Battery.isPowerSaveMode()}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_app_state),
                    onClick = {
                        onShowResult(
                            "App State",
                            "Foreground=${AppState.isAppInForeground()}\nBackground=${AppState.isAppInBackground()}\n" +
                                "ScreenOn=${AppState.isScreenOn()}\nLowRam=${AppState.isLowRamDevice()}"
                        )
                    }
                )
            }

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
                        val now = Time.now()
                        val formatted = Time.format(now, "yyyy-MM-dd HH:mm:ss")
                        val ago = Time.timeAgo(now - 5 * 60 * 1000)
                        onShowResult("Time", "Now=$formatted\nFiveMinAgo=$ago")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_validate),
                    onClick = {
                        val emailValid = Validation.isValidEmail("test@example.com")
                        val phoneValid = Validation.isValidPhone("201234567890")
                        val urlValid = Validation.isValidUrl("https://example.com")
                        onShowResult(
                            "Validation",
                            "Email_Valid=$emailValid\nPhone_Valid=$phoneValid\nUrl_Valid=$urlValid"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_storage),
                    onClick = {
                        val free = Storage.getFreeInternalStorage()
                        val total = Storage.getTotalInternalStorage()
                        val used = Storage.getUsedInternalStorage()
                        onShowResult(
                            "Storage",
                            "Free=${Storage.formatBytes(free)}\n" +
                                "Used=${Storage.formatBytes(used)}\n" +
                                "Total=${Storage.formatBytes(total)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_write_file),
                    onClick = {
                        val content = inputText.ifBlank { "Hello File" }
                        AppFile.writeText("demo.txt", content)
                        AppFile.appendText("demo.txt", "\nAppended at ${Time.format(Time.now(), "HH:mm:ss")}")
                        AppFile.writeBytes("demo.bin", content.toByteArray())
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_read_file),
                    onClick = {
                        val text = AppFile.readText("demo.txt").orEmpty()
                        onShowResult(
                            "File Read",
                            "Files=${AppFile.list().joinToString()}\n\nContent:\n$text"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_delete_file),
                    onClick = {
                        AppFile.delete("demo.txt")
                        AppFile.delete("demo.bin")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_sha256),
                    onClick = {
                        val text = inputText.ifBlank { "password" }
                        onShowResult(
                            "Crypto",
                            "SHA256=${Encryption.sha256(text)}\n\nSHA512=${Encryption.sha512(text)}"
                        )
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_base64),
                    onClick = {
                        val text = inputText.ifBlank { "Hello Base64" }
                        val encoded = Encryption.base64Encode(text)
                        val decoded = Encryption.base64Decode(encoded)
                        onShowResult("Base64", "Base64=$encoded\nDecoded=$decoded")
                    }
                )
            }

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
                        val primary = Signature.getAppPrimarySignatureSHA1()
                        val count = Signature.getAppSignatures().size
                        onShowResult("Signatures", "Primary_SHA1=$primary\nSignatures_Count=$count")
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_biometric),
                    onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null && Biometric.canAuthenticate()) {
                            Biometric.authenticate(
                                activity = activity,
                                title = "Biometric Authentication",
                                subtitle = "Authenticate to test AppUtilX",
                                onSuccess = { onShowResult("Biometric", "• Status: Authentication Successful!") },
                                onError = { code: Int, msg: CharSequence -> onShowResult("Biometric Error", "• Error Code: $code\n• Message: $msg") },
                                onFailed = { onShowResult("Biometric", "• Status: Authentication Failed") }
                            )
                        } else {
                            onShowResult("Biometric", "• Status: Biometric authentication unavailable")
                        }
                    }
                )
                MiuixArrowPreference(
                    title = stringResource(R.string.action_logger),
                    onClick = {
                        com.mohamedzaitoon.apputilx.AppUtilX.log("Demo", "Info log triggered")
                        com.mohamedzaitoon.apputilx.AppUtilX.logWarning("Demo", "Warning log triggered")
                        com.mohamedzaitoon.apputilx.AppUtilX.logError("Demo", "Error log triggered")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

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
