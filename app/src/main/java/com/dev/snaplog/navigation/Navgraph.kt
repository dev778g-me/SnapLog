package com.dev.snaplog.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.snaplog.Presentaion.FullImage
import com.dev.snaplog.Presentaion.View
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import com.dev.snaplog.Presentaion.Welcome


@Composable

fun NavGraph(snapLogViewModel: SnapLogViewModel, screenshotFetchViewmodel: ScreenshotFetchViewmodel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WelcomeScreen
    ) {
        composable<Routes.Home> {
            View(screenshotFetchViewmodel,snapLogViewModel)
        }
        composable <Routes.FullImageScreen>{
            FullImage()
        }
        composable<Routes.WelcomeScreen> {
            Welcome()
        }

    }
}