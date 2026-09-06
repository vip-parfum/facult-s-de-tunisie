package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.CognitiveFaculty
import com.example.data.model.DailyEnergyLog
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CognitiveRepository(private val database: AppDatabase) {
    private val taskDao = database.taskDao()
    private val profileDao = database.userProfileDao()

    val profileFlow: Flow<UserCognitiveProfile?> = profileDao.getProfile()
    val recentEnergyLogsFlow: Flow<List<DailyEnergyLog>> = profileDao.getRecentEnergyLogs()

    fun getTasksForDay(dayOffset: Int): Flow<List<TaskItem>> = taskDao.getTasksForDay(dayOffset)

    suspend fun getProfileSync(): UserCognitiveProfile {
        return withContext(Dispatchers.IO) {
            val existing = profileDao.getProfileSync()
            if (existing != null) {
                existing
            } else {
                val initial = UserCognitiveProfile()
                profileDao.insertOrUpdateProfile(initial)
                seedInitialTasksIfEmpty()
                initial
            }
        }
    }

    suspend fun seedInitialTasksIfEmpty() {
        withContext(Dispatchers.IO) {
            val existing = profileDao.getProfileSync()
            if (existing == null) {
                profileDao.insertOrUpdateProfile(UserCognitiveProfile())
            }

            // Seed realistic tasks for Day 0 (Aujourd'hui) if none exist
            val initialTasks = listOf(
                TaskItem(
                    title = "Analyse algorithmique & Architecture système",
                    description = "Conception des modèles de données et résolution des contraintes techniques",
                    faculty = CognitiveFaculty.LOGIC.name,
                    cognitiveLoad = 5,
                    startTimeMinutes = 540, // 09:00
                    durationMinutes = 90,
                    dayOffset = 0
                ),
                TaskItem(
                    title = "Session de Brainstorming & Idéation Visuelle",
                    description = "Recherche d'inspiration et moodboard créatif pour le nouveau projet",
                    faculty = CognitiveFaculty.CREATIVITY.name,
                    cognitiveLoad = 3,
                    startTimeMinutes = 660, // 11:00
                    durationMinutes = 60,
                    dayOffset = 0
                ),
                TaskItem(
                    title = "Lecture d'articles scientifiques & Synthèse",
                    description = "Assimilation des dernières avancées en neurosciences appliquées",
                    faculty = CognitiveFaculty.MEMORY.name,
                    cognitiveLoad = 4,
                    startTimeMinutes = 840, // 14:00 (often post-lunch dip, good to show alignment alert!)
                    durationMinutes = 60,
                    dayOffset = 0
                ),
                TaskItem(
                    title = "Traitement des emails & Organisation administrative",
                    description = "Répondre aux messages en attente et archiver les documents",
                    faculty = CognitiveFaculty.ADMIN_LOW.name,
                    cognitiveLoad = 1,
                    startTimeMinutes = 930, // 15:30
                    durationMinutes = 45,
                    dayOffset = 0
                ),
                TaskItem(
                    title = "Revue de code & Débogage complexe",
                    description = "Examen minutieux des bugs critiques et tests unitaires",
                    faculty = CognitiveFaculty.LOGIC.name,
                    cognitiveLoad = 4,
                    startTimeMinutes = 1020, // 17:00
                    durationMinutes = 75,
                    dayOffset = 0
                )
            )
            taskDao.insertTasks(initialTasks)
        }
    }

    suspend fun saveProfile(profile: UserCognitiveProfile) {
        withContext(Dispatchers.IO) {
            profileDao.insertOrUpdateProfile(profile)
        }
    }

    suspend fun addTask(task: TaskItem): Long {
        return withContext(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    suspend fun updateTask(task: TaskItem) {
        withContext(Dispatchers.IO) {
            taskDao.updateTask(task)
        }
    }

    suspend fun deleteTask(task: TaskItem) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTask(task)
        }
    }

    suspend fun replaceTasksForDay(dayOffset: Int, tasks: List<TaskItem>) {
        withContext(Dispatchers.IO) {
            taskDao.clearTasksForDay(dayOffset)
            taskDao.insertTasks(tasks)
        }
    }

    suspend fun recordFocusFeedback(taskId: Long, rating: Int, fatigue: Int) {
        withContext(Dispatchers.IO) {
            val task = taskDao.getTaskById(taskId) ?: return@withContext
            val updatedTask = task.copy(
                isCompleted = true,
                completionRating = rating,
                fatigueAfterTask = fatigue
            )
            taskDao.updateTask(updatedTask)

            // Continual learning: update cognitive profile based on user performance
            val currentProfile = profileDao.getProfileSync() ?: UserCognitiveProfile()
            var logic = currentProfile.logicAnalyticalScore
            var creative = currentProfile.creativeLateralScore
            var memory = currentProfile.memoryLearningScore
            var attention = currentProfile.sustainedAttentionScore
            var endurance = currentProfile.mentalEnduranceScore

            // Adjust based on rating (1 to 5)
            val delta = (rating - 3) * 2 // -4, -2, 0, +2, +4
            when (task.faculty) {
                CognitiveFaculty.LOGIC.name -> logic = (logic + delta).coerceIn(20, 100)
                CognitiveFaculty.CREATIVITY.name -> creative = (creative + delta).coerceIn(20, 100)
                CognitiveFaculty.MEMORY.name -> memory = (memory + delta).coerceIn(20, 100)
                CognitiveFaculty.ATTENTION.name -> attention = (attention + delta).coerceIn(20, 100)
                else -> {}
            }

            // Endurance adaptation based on perceived fatigue
            // If fatigue was mild (1 or 2) after intense task (4 or 5), endurance is very high
            if (task.cognitiveLoad >= 4 && fatigue <= 2) {
                endurance = (endurance + 3).coerceIn(20, 100)
            } else if (fatigue >= 4) {
                // User felt exhausted; slight decrease or needs more recovery buffer
                endurance = (endurance - 2).coerceIn(20, 100)
            }

            val updatedProfile = currentProfile.copy(
                logicAnalyticalScore = logic,
                creativeLateralScore = creative,
                memoryLearningScore = memory,
                sustainedAttentionScore = attention,
                mentalEnduranceScore = endurance,
                totalSessionsCompleted = currentProfile.totalSessionsCompleted + 1,
                lastUpdated = System.currentTimeMillis()
            )
            profileDao.insertOrUpdateProfile(updatedProfile)
        }
    }

    suspend fun logDailyEnergy(energy: Int, fatigue: Int, note: String, dayOffset: Int) {
        withContext(Dispatchers.IO) {
            profileDao.insertEnergyLog(
                DailyEnergyLog(
                    dayOffset = dayOffset,
                    reportedEnergy = energy,
                    mentalFatigue = fatigue,
                    note = note
                )
            )
        }
    }
}
