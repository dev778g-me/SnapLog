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

class Core : Service(){


    //ON BIND foreground service
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    //ON CREATE service
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    //ON START foreground service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("service started")
        val imagelist = intent?.getStringArrayListExtra("imagelist") ?: arrayListOf()
        // launch corotine
        CoroutineScope(Dispatchers.IO).launch {
            SnapLogRepo.getDescriptionForAllImages(imagelist,this@Core)
        }

        return START_STICKY
    }
        
    //ON DESTROY
    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startForegroundService(){
        val notificationId = "snaplog"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val channel =   NotificationChannel(
                notificationId,
                "Snaplog",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "snaplog").setContentTitle("Snaplog")
            .setContentText("scanning screenshots").build()
        startForeground(1, notification)
    }





}