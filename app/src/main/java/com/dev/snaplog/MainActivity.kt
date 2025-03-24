package com.dev.snaplog

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import com.dev.snaplog.Presentaion.navigation.NavGraph
import com.example.compose.SnapTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val SnapLogViewModel = SnapLogViewModel(context)
            val screenshotFetchViewmodel = ScreenshotFetchViewmodel()
            //val permissionViewmodel = PermissionViewmodel()
            SnapTheme {
                NavGraph(SnapLogViewModel,screenshotFetchViewmodel)
            }
        }
    }
}

