package com.dev.snaplog.navigation

import androidx.compose.material3.SnackbarData
import com.dev.snaplog.Db.ScreenshotData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
sealed class Routes {
    @Serializable
    data object Home : Routes()

     @Serializable
     data class FullImageScreen(val dataJson: String) : Routes()

    @Serializable
    data object WelcomeScreen : Routes()


}