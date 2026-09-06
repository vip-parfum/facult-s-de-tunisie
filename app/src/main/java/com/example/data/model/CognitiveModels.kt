package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The user's cognitive profile capturing their intellectual faculties,
 * chronotype, attention span, and fatigue recovery rates.
 */
@Entity(tableName = "user_cognitive_profile")
data class UserCognitiveProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Utilisateur",
    // Chronotype: "MORNING_LARK" (Alouette matinale), "NIGHT_OWL" (Hibou nocturne), "INTERMEDIATE" (Intermédiaire / Équilibré)
    val chronotype: String = "MORNING_LARK",
    // Optimal attention span before cognitive fatigue kicks in (in minutes: 25, 45, 60, 90)
    val attentionSpanMinutes: Int = 45,
    // Core Intellectual Faculties (0 - 100 scale)
    val logicAnalyticalScore: Int = 85,
    val creativeLateralScore: Int = 72,
    val memoryLearningScore: Int = 78,
    val sustainedAttentionScore: Int = 80,
    val mentalEnduranceScore: Int = 70,
    // Morning alertness level (1 - 5)
    val morningEnergy: Int = 4,
    // Afternoon alertness level (1 - 5)
    val afternoonEnergy: Int = 2,
    // Evening alertness level (1 - 5)
    val eveningEnergy: Int = 3,
    // Has the user completed initial calibration questionnaire
    val isCalibrated: Boolean = true,
    // Total cognitive focus sessions completed (used for continual learning)
    val totalSessionsCompleted: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class CognitiveFaculty(val displayName: String, val description: String, val colorHex: Long) {
    LOGIC("Logique & Analyse", "Résolution de problèmes complexes, calcul, stratégie, programmation", 0xFF3B82F6),
    CREATIVITY("Créativité & Idéation", "Conception, écriture, brainstorming, pensée divergente", 0xFFF59E0B),
    MEMORY("Mémoire & Apprentissage", "Assimilation de connaissances, lecture de fond, révision", 0xFF10B981),
    ATTENTION("Attention & Focus Soutenu", "Travail profond ininterrompu, synthèse sans distraction", 0xFF8B5CF6),
    ADMIN_LOW("Gestion & Routine Légère", "Emails, organisation, tâches à faible charge cognitive", 0xFF64748B),
    REST("Pause Cognitive", "Régénération neuronale, marche, respiration, déconnexion", 0xFF059669)
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    // Which cognitive faculty is primarily required
    val faculty: String = CognitiveFaculty.LOGIC.name,
    // Cognitive load / Mental effort required: 1 (Léger), 2 (Modéré), 3 (Intense), 4 (Très intense), 5 (Épuisant)
    val cognitiveLoad: Int = 3,
    // Start time in minutes from midnight (e.g. 540 = 09:00, 840 = 14:00)
    val startTimeMinutes: Int = 540,
    // Duration in minutes
    val durationMinutes: Int = 60,
    // Day offset (0 = Today, 1 = Tomorrow, etc.)
    val dayOffset: Int = 0,
    val isCompleted: Boolean = false,
    // User feedback after completing: 1 (Mauvaise concentration) to 5 (Excellente fluidité)
    val completionRating: Int = 0,
    // Perceived cognitive fatigue after task (1 = Frais, 5 = Épuisé)
    val fatigueAfterTask: Int = 0
) {
    fun getFormattedStartTime(): String {
        val hours = startTimeMinutes / 60
        val mins = startTimeMinutes % 60
        return "%02d:%02d".format(hours, mins)
    }

    fun getFormattedEndTime(): String {
        val end = startTimeMinutes + durationMinutes
        val hours = (end / 60) % 24
        val mins = end % 60
        return "%02d:%02d".format(hours, mins)
    }
}

@Entity(tableName = "daily_energy_logs")
data class DailyEnergyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dayOffset: Int = 0,
    // 1 to 5
    val reportedEnergy: Int = 3,
    val mentalFatigue: Int = 2,
    val note: String = ""
)
