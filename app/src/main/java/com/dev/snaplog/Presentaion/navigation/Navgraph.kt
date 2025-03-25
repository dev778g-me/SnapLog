package com.dev.snaplog.Presentaion.navigation

import Welcome
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(snapLogViewModel: SnapLogViewModel, screenshotFetchViewmodel: ScreenshotFetchViewmodel) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val permissionList = listOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        },
        Manifest.permission.POST_NOTIFICATIONS
    )

    val allgranted = remember {
        permissionList.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    val startDestination = if (allgranted) Routes.Home.route else Routes.WelcomeScreen.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            Routes.WelcomeScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            Welcome(navController)
        }

        composable(
            Routes.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -300 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 300 }) + fadeOut() }
        ) {
            View(screenshotFetchViewmodel, snapLogViewModel, navController)
        }

        composable(
            Routes.FullImageScreen.route + "/{data}",
            arguments = listOf(navArgument("data") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            val data = it.arguments?.getString("data")
            val json = data?.let { Uri.decode(it) }
            val finaldata = json?.let { Json.decodeFromString<ScreenshotData>(it) }
            finaldata?.let {
                FullImage(data = it, navController)
            }
        }
    }
}
