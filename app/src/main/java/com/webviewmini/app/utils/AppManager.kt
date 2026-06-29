package com.webviewmini.app.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.webviewmini.app.MainActivity
import com.webviewmini.app.admin.DeviceAdminManager

class AppManager(private val context: Context) {
    private val packageManager = context.packageManager
    private val deviceAdminManager = DeviceAdminManager(context)

    private val knownBrowsers = listOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.google.android.apps.chrome",
        "org.mozilla.firefox",
        "org.mozilla.fenix",
        "com.opera.browser",
        "com.opera.gx",
        "com.sec.android.app.sbrowser",
        "com.brave.browser",
        "com.microsoft.emmx",
        "com.ecosia.android"
    )

    fun getBrowserApps(): List<AppInfo> {
        val installedApps = mutableListOf<AppInfo>()
        val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        installedPackages.forEach { app ->
            if (knownBrowsers.contains(app.packageName)) {
                val appName = packageManager.getApplicationLabel(app).toString()
                installedApps.add(AppInfo(appName, app.packageName))
            }
        }

        return installedApps
    }

    fun disableBrowser(packageName: String, context: Context) {
        try {
            if (deviceAdminManager.isDeviceAdminActive()) {
                deviceAdminManager.disableBrowser(packageName)
            } else {
                // Request device admin
                deviceAdminManager.requestDeviceAdmin()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uninstallBrowser(packageName: String, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    data class AppInfo(
        val name: String,
        val packageName: String
    )
}
