package com.webviewmini.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BrowserOption(
    val name: String,
    val url: String,
    val emoji: String,
    val description: String
)

@Composable
fun WebViewBrowserScreen() {
    val context = LocalContext.current
    val browsers = listOf(
        BrowserOption(
            name = "Opera GX",
            url = "https://www.operagx.com",
            emoji = "🎮",
            description = "Oyuncu odaklı tarayıcı - RAM ve CPU sınırlayıcı ile"
        ),
        BrowserOption(
            name = "Ecosia",
            url = "https://www.ecosia.org",
            emoji = "🌍",
            description = "Çevre dostu arama motoru - Her aramayla ağaç dikin"
        ),
        BrowserOption(
            name = "Google",
            url = "https://www.google.com",
            emoji = "🔍",
            description = "Popüler arama motoru"
        ),
        BrowserOption(
            name = "Bing",
            url = "https://www.bing.com",
            emoji = "💬",
            description = "Microsoft arama motoru"
        ),
        BrowserOption(
            name = "DuckDuckGo",
            url = "https://www.duckduckgo.com",
            emoji = "🦆",
            description = "Gizlilik odaklı arama motoru"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Web Tarayıcı",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            "Hızlı web erişimi için aşağıdaki siteleri ziyaret edin",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        browsers.forEach { browser ->
            BrowserOptionCard(
                browser = browser,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(browser.url))
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BrowserOptionCard(
    browser: BrowserOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            browser.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            browser.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        browser.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.OpenInBrowser,
                    contentDescription = "Aç",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Aç")
            }
        }
    }
}
