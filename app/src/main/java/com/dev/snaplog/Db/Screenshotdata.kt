package com.dev.snaplog.Db

import android.icu.text.CaseMap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@kotlinx.parcelize.Parcelize
@Serializable
@Entity(tableName = "ScreenshotData")
data class ScreenshotData(
    @PrimaryKey(autoGenerate = true,)
    val id : Int = 0,
    val screenshotPath: String?,
    val note : String?,
    val title: String?,
    val description: String?,
) : Parcelable
