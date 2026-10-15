package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookmarkEntity::class, RecentSearchEntity::class], version = 1, exportSchema = false)
abstract class PoliceDatabase : RoomDatabase() {
    abstract fun policeDao(): PoliceDao

    companion object {
        @Volatile
        private var INSTANCE: PoliceDatabase? = null

        fun getDatabase(context: Context): PoliceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PoliceDatabase::class.java,
                    "hathras_police_gpl.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
