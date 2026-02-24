package com.turjaun.serverstatuscookies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NotificationEntity::class, DeviceToken::class],   // added DeviceToken
    version = 2,                                                   // version increased
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun deviceTokenDao(): DeviceTokenDao                 // new DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notification_database"
                )
                .fallbackToDestructiveMigration()                  // will recreate DB
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}