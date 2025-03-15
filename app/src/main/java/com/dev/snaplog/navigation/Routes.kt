package com.dev.snaplog.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Routes {
    @Serializable
    data object Home : Routes()

     @Serializable
     data object FullImageScreen : Routes()

    @Serializable
    data object WelcomeScreen : Routes()


}