package com.dev.snaplog.Presentaion.navigation

import Welcome
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.snaplog.Db.ScreenshotData
import com.dev.snaplog.Presentaion.Screens.FullImage

import com.dev.snaplog.Presentaion.Screens.View
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import kotlinx.serialization.json.Json


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable



fun NavGraph(snapLogViewModel: SnapLogViewModel, screenshotFetchViewmodel: ScreenshotFetchViewmodel) {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.WelcomeScreen.route
    ) {
       composable(Routes.WelcomeScreen.route){
           Welcome(navController)
       }
        composable(Routes.Home.route){
            View(screenshotFetchViewmodel,snapLogViewModel,navController)
        }
        composable(Routes.FullImageScreen.route+"/{data}", arguments = listOf(
            navArgument("data") {
               type = NavType.StringType

            }
        )){
            val data = it.arguments?.getString("data")
            val json =data?.let { Uri.decode(it) }
            val finaldata = json?.let { Json.decodeFromString<ScreenshotData>(it) }
            finaldata?.let {
                FullImage(data = it,navController)
            }
        }

    }
}