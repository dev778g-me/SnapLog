//package com.dev.snaplog.navigation
//
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import androidx.navigation.NavType
//import com.dev.snaplog.Db.ScreenshotData
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//object CustomNavtypes {
//    val data = object : NavType<ScreenshotData>(isNullableAllowed = false) {
//        override fun get(
//            bundle: Bundle,
//            key: String
//        ): ScreenshotData? {
//          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            return  bundle.getParcelable(key, ScreenshotData)
//          }
//        }
//
//        override fun parseValue(value: String): ScreenshotData {
//            return Json.decodeFromString(Uri.decode(value))
//        }
//
//        override fun put(
//            bundle: Bundle,
//            key: String,
//            value: ScreenshotData
//        ) {
//           bundle.putString(key, Json.encodeToString(value))
//        }
//
//        override fun serializeAsValue(value: ScreenshotData): String {
//            return Json.encodeToString(value)
//        }
//    }}