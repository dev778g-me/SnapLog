package com.dev.snaplog.Db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScreenshotDao {
    @Insert
    suspend fun insertScreenshotData(screenshotData: ScreenshotData) // for insertion of data in the room database

    @Query("SELECT * FROM ScreenshotData")
   fun getAllScreenShotData() : LiveData<List<ScreenshotData>> // for getting data of the screenshot


}