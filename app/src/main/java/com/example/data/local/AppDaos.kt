package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyEnergyLog
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE dayOffset = :dayOffset ORDER BY startTimeMinutes ASC")
    fun getTasksForDay(dayOffset: Int): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks ORDER BY dayOffset ASC, startTimeMinutes ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks WHERE dayOffset = :dayOffset")
    suspend fun clearTasksForDay(dayOffset: Int)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_cognitive_profile WHERE id = 1")
    fun getProfile(): Flow<UserCognitiveProfile?>

    @Query("SELECT * FROM user_cognitive_profile WHERE id = 1")
    suspend fun getProfileSync(): UserCognitiveProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserCognitiveProfile)

    @Query("SELECT * FROM daily_energy_logs ORDER BY timestamp DESC LIMIT 30")
    fun getRecentEnergyLogs(): Flow<List<DailyEnergyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnergyLog(log: DailyEnergyLog)
}
