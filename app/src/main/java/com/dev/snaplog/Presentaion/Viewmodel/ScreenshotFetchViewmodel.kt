package com.dev.snaplog.Presentaion.Viewmodel

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ScreenshotFetchViewmodel : ViewModel() {

    var  ScreenshotList = mutableStateListOf<String>()
    fun getAllScreenshot(context: Context) : List<String> {
        val screenshotPath = mutableStateListOf<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        //only fetch screenshot images from "Screenshot" folder
        val selector = MediaStore.Images.Media.DATA + " LIKE ?"
        val selectionArgs = arrayOf("%Screenshots%")

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,selector,selectionArgs,null
        )
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (it.moveToNext()) {
                val filePath = it.getString(columnIndex)
                screenshotPath.add(filePath)
            }
        }
        ScreenshotList = screenshotPath
        return screenshotPath
    }





}