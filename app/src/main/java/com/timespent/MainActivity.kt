package com.timespent

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fillStats()
    }

    private fun fillStats() {
        if (hasPermission()) {
            getStats()
        } else {
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MainActivity", "resultCode $resultCode")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS -> fillStats()
        }
    }

    private fun requestPermission() {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show()
        startActivityForResult(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS
        )
    }

    private fun hasPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
//        return ContextCompat.checkSelfPermission(this,
//                Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
    }

    private fun getStats() {
        val lUsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val lUsageStatsList = lUsageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            System.currentTimeMillis() - (1000*60*10),
            System.currentTimeMillis() + (1000*60*10)
        )
//        queryEvents(System.currentTimeMillis() - (1000*60*10),
//        System.currentTimeMillis())
//        queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY,
//            System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
//            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
//        )
        val lTextView = findViewById(R.id.usage_stats) as TextView
        val lStringBuilder = StringBuilder()
        for (lUsageStats in lUsageStatsList) {
            lStringBuilder.append(lUsageStats.packageName)
            lStringBuilder.append(" - ")
            lStringBuilder.append(lUsageStats.totalTimeInForeground)
            lStringBuilder.append("\r\n")
        }
        lTextView.text = lStringBuilder.toString()
    }
}
