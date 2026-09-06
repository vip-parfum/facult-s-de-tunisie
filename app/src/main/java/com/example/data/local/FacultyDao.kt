package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Faculty
import kotlinx.coroutines.flow.Flow

@Dao
interface FacultyDao {

    @Query("SELECT * FROM faculties ORDER BY university ASC, name ASC")
    fun getAllFaculties(): Flow<List<Faculty>>

    @Query("SELECT * FROM faculties WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFaculties(): Flow<List<Faculty>>

    @Query("SELECT * FROM faculties WHERE id = :facultyId LIMIT 1")
    fun getFacultyById(facultyId: String): Flow<Faculty?>

    @Query("SELECT * FROM faculties WHERE id = :facultyId LIMIT 1")
    suspend fun getFacultyByIdDirect(facultyId: String): Faculty?

    @Query("SELECT COUNT(*) FROM faculties")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(faculties: List<Faculty>)

    @Update
    suspend fun updateFaculty(faculty: Faculty)

    @Query("UPDATE faculties SET isFavorite = :isFav WHERE id = :facultyId")
    suspend fun setFavorite(facultyId: String, isFav: Boolean)

    @Query("UPDATE faculties SET notes = :notes WHERE id = :facultyId")
    suspend fun updateNotes(facultyId: String, notes: String)
}
