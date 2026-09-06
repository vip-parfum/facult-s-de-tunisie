package com.example.data.repository

import com.example.data.local.FacultyDao
import com.example.data.model.Faculty
import com.example.data.model.TunisiaFacultiesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FacultyRepository(
    private val facultyDao: FacultyDao
) {
    val allFaculties: Flow<List<Faculty>> = facultyDao.getAllFaculties()
    val favoriteFaculties: Flow<List<Faculty>> = facultyDao.getFavoriteFaculties()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (facultyDao.getCount() == 0) {
                facultyDao.insertAll(TunisiaFacultiesData.allFaculties)
            }
        }
    }

    fun getFaculty(id: String): Flow<Faculty?> = facultyDao.getFacultyById(id)

    suspend fun toggleFavorite(facultyId: String, currentStatus: Boolean) {
        facultyDao.setFavorite(facultyId, !currentStatus)
    }

    suspend fun saveNotes(facultyId: String, notes: String) {
        facultyDao.updateNotes(facultyId, notes)
    }

    suspend fun refreshData() {
        if (facultyDao.getCount() == 0) {
            facultyDao.insertAll(TunisiaFacultiesData.allFaculties)
        }
    }
}
