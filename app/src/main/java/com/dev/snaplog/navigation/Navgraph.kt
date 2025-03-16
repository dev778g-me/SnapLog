package com.dev.snaplog.navigation

import Welcome
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dev.snaplog.Db.ScreenshotData


import com.dev.snaplog.Presentaion.View
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import kotlinx.serialization.json.Json


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable

fun NavGraph(snapLogViewModel: SnapLogViewModel, screenshotFetchViewmodel: ScreenshotFetchViewmodel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.WelcomeScreen
    ) {
        composable<Routes.Home> {
            View(screenshotFetchViewmodel,snapLogViewModel,navController)
        }
        composable<Routes.FullImageScreen> { entry ->
//            val screenshotDataJson = entry.toRoute<Routes.FullImageScreen>().dataJson
//            FullImage(dataJson = screenshotDataJson,navController)
        }
        composable<Routes.WelcomeScreen> {
            Welcome(navController)
        }

    }
}