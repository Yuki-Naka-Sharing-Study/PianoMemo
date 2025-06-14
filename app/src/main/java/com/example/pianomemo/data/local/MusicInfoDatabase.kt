package com.example.pianomemo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [MusicInfo::class], version = 1)
abstract class MusicInfoDatabase : RoomDatabase() {
    abstract fun musicInfoDao(): MusicInfoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: MusicInfoDatabase? = null

        fun getMusicInfoDatabase(context: Context): MusicInfoDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    MusicInfoDatabase::class.java, "music_info"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}