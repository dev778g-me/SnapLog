package com.dev.snaplog.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dev.snaplog.repo.SnapLogRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Core : Service() {

    private val notificationId = 1
    private val channelId = "snaplog"
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service started")

        val imagelist = intent?.getStringArrayListExtra("imagelist") ?: arrayListOf()

        // 🔥 1️⃣ Start foreground immediately with an initial notification
        startForeground(notificationId, createInitialNotification())

        // 🔥 2️⃣ Start coroutine after foreground service is initialized
        CoroutineScope(Dispatchers.IO).launch {
            SnapLogRepo.getDescriptionForAllImages(imagelist, this@Core) { progress ->
                updateProgressNotification(progress, imagelist.size)
            }
            stopSelf() // Stop service after task completes
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Snaplog Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createInitialNotification() =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle("Snaplog")
            .setContentText("Initializing scan...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()

    private fun createProgressNotification(progress: Int, max: Int) =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle("Snaplog")
            .setContentText("Scanning screenshots ($progress/$max)")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(max, progress, false)
            .setOngoing(true)
            .build()

    private fun updateProgressNotification(progress: Int, max: Int) {
        val notification = createProgressNotification(progress, max)
        notificationManager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(notificationId)
    }
}
