package com.dev.snaplog.Db

import android.icu.text.CaseMap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ScreenshotData")
data class ScreenshotData(
    @PrimaryKey(autoGenerate = true,)
    val id : Int = 0,
    val screenshotPath: String,
    val title: String,
    val description: String,
)
