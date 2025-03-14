package com.dev.snaplog.Db

import android.content.Context
import androidx.compose.ui.graphics.GraphicsContext
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ScreenshotData::class], version = 1)
abstract class ScreenShotDb : RoomDatabase() {
    abstract fun getScreenshotDao () : ScreenshotDao
    companion object{
        @Volatile
        private var INSTANCE : ScreenShotDb ? = null

        fun getDatabase(context: Context) : ScreenShotDb {
            return INSTANCE ?: synchronized (this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScreenShotDb::class.java,
                    "screenshot_Db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}