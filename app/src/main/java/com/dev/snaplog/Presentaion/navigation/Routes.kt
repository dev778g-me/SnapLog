package com.dev.snaplog.Presentaion.navigation

import androidx.compose.material3.SnackbarData
import com.dev.snaplog.Db.ScreenshotData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
sealed class Routes(val route: String) {
    @Serializable
    data object Home : Routes("Home")

     @Serializable
     data object FullImageScreen : Routes("FullView")

    @Serializable
    data object WelcomeScreen : Routes("Welcome")


}