package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DailyEnergyLog
import com.example.data.model.Faculty
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile

@Database(
    entities = [
        Faculty::class,
        TaskItem::class,
        UserCognitiveProfile::class,
        DailyEnergyLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun facultyDao(): FacultyDao
    abstract fun taskDao(): TaskDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tunisia_faculties_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
