package com.webviewmini.app

import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.webviewmini.app.ui.theme.WebViewMiniTheme
import com.webviewmini.app.ui.screens.DigitalClockScreen
import com.webviewmini.app.ui.screens.WebViewBrowserScreen
import com.webviewmini.app.utils.AppManager
import com.webviewmini.app.utils.StorageUtils
import com.webviewmini.app.admin.DeviceAdminManager

class MainActivity : ComponentActivity() {
    private lateinit var deviceAdminManager: DeviceAdminManager
    private lateinit var appManager: AppManager
    private lateinit var storageUtils: StorageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceAdminManager = DeviceAdminManager(this)
        appManager = AppManager(this)
        storageUtils = StorageUtils(this)

        setContent {
            WebViewMiniTheme {
                MainScreen(
                    deviceAdminManager = deviceAdminManager,
                    appManager = appManager,
                    storageUtils = storageUtils
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    deviceAdminManager: DeviceAdminManager,
    appManager: AppManager,
    storageUtils: StorageUtils
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var isAdminEnabled by remember { mutableStateOf(false) }
    var browsers by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var storageInfo by remember { mutableStateOf<StorageInfo?>(null) }

    LaunchedEffect(Unit) {
        isAdminEnabled = deviceAdminManager.isDeviceAdminActive()
        browsers = appManager.getBrowserApps()
        storageInfo = storageUtils.getStorageInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebView Mini") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Web, contentDescription = "Tarayıcılar") },
                    label = { Text("Tarayıcılar") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Storage, contentDescription = "Depolama") },
                    label = { Text("Depolama") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = "Web") },
                    label = { Text("Web") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Saat") },
                    label = { Text("Saat") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Ayarlar") },
                    label = { Text("Ayarlar") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> BrowsersTab(browsers = browsers, appManager = appManager)
                1 -> StorageTab(storageInfo = storageInfo)
                2 -> WebViewBrowserScreen()
                3 -> DigitalClockScreen()
                4 -> SettingsTab(
                    isAdminEnabled = isAdminEnabled,
                    deviceAdminManager = deviceAdminManager,
                    onAdminStatusChanged = { isAdminEnabled = it }
                )
            }
        }
    }
}

@Composable
fun BrowsersTab(browsers: List<AppInfo>, appManager: AppManager) {
    val context = LocalContext.current
    var selectedBrowser by remember { mutableStateOf<AppInfo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Yüklü Tarayıcılar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (browsers.isEmpty()) {
            Text(
                "Tarayıcı bulunamadı",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp)
            )
        } else {
            browsers.forEach { browser ->
                BrowserCard(
                    app = browser,
                    onDisable = { appManager.disableBrowser(browser.packageName, context) },
                    onUninstall = { appManager.uninstallBrowser(browser.packageName, context) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BrowserCard(app: AppInfo, onDisable: () -> Unit, onUninstall: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                app.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                app.packageName,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDisable,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.warning
                    )
                ) {
                    Text("Devre Dışı Bırak")
                }
                Button(
                    onClick = onUninstall,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Kaldır")
                }
            }
        }
    }
}

@Composable
fun StorageTab(storageInfo: StorageInfo?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Depolama Bilgisi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (storageInfo != null) {
            StorageInfoCard(storageInfo)
        } else {
            Text("Depolama bilgisi yüklenemiyor")
        }
    }
}

@Composable
fun StorageInfoCard(info: StorageInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StorageInfoRow("Toplam Depolama", info.totalStorage)
            StorageInfoRow("Kullanılan", info.usedStorage)
            StorageInfoRow("Boş", info.freeStorage)
        }
    }
}

@Composable
fun StorageInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SettingsTab(
    isAdminEnabled: Boolean,
    deviceAdminManager: DeviceAdminManager,
    onAdminStatusChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Ayarlar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Cihaz Yöneticisi",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isAdminEnabled) "Etkinleştirildi" else "Devre dışı",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAdminEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                deviceAdminManager.requestDeviceAdmin()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Cihaz yöneticisi izinleri, tarayıcıları devre dışı bırakmak için gereklidir.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: String? = null
)

data class StorageInfo(
    val totalStorage: String,
    val usedStorage: String,
    val freeStorage: String
)
