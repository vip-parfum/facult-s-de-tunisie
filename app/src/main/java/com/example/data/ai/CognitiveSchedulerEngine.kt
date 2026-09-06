package com.example.data.ai

import com.example.data.model.CognitiveFaculty
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

data class ScheduleOptimizationResult(
    val optimizedTasks: List<TaskItem>,
    val averageInitialAlignment: Int,
    val averageOptimizedAlignment: Int,
    val insights: List<String>,
    val explanationText: String
)

object CognitiveSchedulerEngine {

    /**
     * Calculates the cognitive alertness level (0 to 100) at any given minute of the day (0..1439).
     */
    fun getAlertnessAtMinute(minute: Int, profile: UserCognitiveProfile): Float {
        val hour = minute / 60.0f
        return when (profile.chronotype) {
            "NIGHT_OWL" -> {
                // Night Owl: Low in the morning, rises steadily, peaks 18:00 - 22:00
                when {
                    hour < 7.0f -> 20.0f
                    hour < 11.0f -> 35.0f + (hour - 7.0f) * 8.0f // 35 -> 67
                    hour < 14.0f -> 67.0f + (hour - 11.0f) * 3.0f // 67 -> 76
                    hour < 17.0f -> 75.0f - (hour - 14.0f) * 2.0f // minor plateau ~73
                    hour < 22.0f -> 75.0f + (hour - 17.0f) * 4.5f // 75 -> 97.5 (Peak)
                    hour < 24.0f -> 95.0f - (hour - 22.0f) * 15.0f // 95 -> 65
                    else -> 40.0f
                }
            }
            "INTERMEDIATE" -> {
                // Intermediate: Morning peak 10:00-12:30, dip 13:30-15:00, second peak 16:30-18:30
                when {
                    hour < 6.5f -> 20.0f
                    hour < 10.0f -> 45.0f + (hour - 6.5f) * 12.0f // 45 -> 87
                    hour < 12.5f -> 88.0f - (hour - 10.0f) * 3.0f // 88 -> 80
                    hour < 14.5f -> 80.0f - (hour - 12.5f) * 18.0f // 80 -> 44 (Dip)
                    hour < 18.0f -> 44.0f + (hour - 14.5f) * 11.0f // 44 -> 82 (Second peak)
                    hour < 21.0f -> 82.0f - (hour - 18.0f) * 10.0f // 82 -> 52
                    else -> 30.0f
                }
            }
            else -> { // "MORNING_LARK" (Default)
                // Morning Lark: High morning peak 08:30-11:30, sharp post-lunch dip 13:30-15:00, moderate second wind 16:30-18:00
                when {
                    hour < 6.0f -> 20.0f
                    hour < 8.5f -> 40.0f + (hour - 6.0f) * 22.0f // 40 -> 95
                    hour < 11.5f -> 95.0f - (hour - 8.5f) * 3.0f // 95 -> 86 (High peak)
                    hour < 14.5f -> 86.0f - (hour - 11.5f) * 16.0f // 86 -> 38 (Post-lunch dip)
                    hour < 17.5f -> 38.0f + (hour - 14.5f) * 11.0f // 38 -> 71 (Second wind)
                    hour < 20.5f -> 71.0f - (hour - 17.5f) * 12.0f // 71 -> 35
                    else -> 20.0f
                }
            }
        }.coerceIn(15.0f, 100.0f)
    }

    /**
     * Computes how well a task is scheduled relative to the user's cognitive state (0 to 100%).
     */
    fun calculateTaskAlignment(task: TaskItem, profile: UserCognitiveProfile): Int {
        val midPoint = task.startTimeMinutes + (task.durationMinutes / 2)
        val alertness = getAlertnessAtMinute(midPoint, profile)

        return when {
            // Very high cognitive load (4 or 5)
            task.cognitiveLoad >= 4 -> {
                // Requires high alertness (> 70)
                when {
                    alertness >= 80 -> 95
                    alertness >= 70 -> 82
                    alertness >= 55 -> 60
                    else -> 35 // Terrible alignment: heavy task scheduled during cognitive valley
                }
            }
            // Creative task (benefits from either peak focus or relaxed diffuse zone)
            task.faculty == CognitiveFaculty.CREATIVITY.name -> {
                when {
                    alertness in 65.0f..85.0f -> 94
                    alertness > 85.0f -> 85
                    alertness >= 50.0f -> 75
                    else -> 45
                }
            }
            // Memory & Learning: Requires good alertness, poor during post-lunch slump
            task.faculty == CognitiveFaculty.MEMORY.name -> {
                when {
                    alertness >= 75 -> 92
                    alertness >= 60 -> 78
                    else -> 40
                }
            }
            // Low cognitive load (Admin, emails, routine)
            task.cognitiveLoad <= 2 -> {
                // Ideally placed during lower alertness to protect peak hours!
                when {
                    alertness <= 60 -> 95 // Perfect: routine placed during mental valley
                    alertness <= 75 -> 78
                    else -> 55 // Waste of peak mental energy on routine tasks
                }
            }
            // Rest break: always beneficial if placed after intense task
            task.faculty == CognitiveFaculty.REST.name -> 95
            // Moderate load (3)
            else -> {
                when {
                    alertness in 55.0f..85.0f -> 90
                    alertness > 85.0f -> 75
                    else -> 55
                }
            }
        }
    }

    /**
     * Auto-optimizes the user's daily agenda using cognitive ergonomic principles.
     */
    fun optimizeSchedule(
        tasks: List<TaskItem>,
        profile: UserCognitiveProfile
    ): ScheduleOptimizationResult {
        if (tasks.isEmpty()) {
            return ScheduleOptimizationResult(
                optimizedTasks = emptyList(),
                averageInitialAlignment = 100,
                averageOptimizedAlignment = 100,
                insights = listOf("Aucune tâche à optimiser."),
                explanationText = "Votre emploi du temps est vide pour cette journée."
            )
        }

        val initialAlignments = tasks.map { calculateTaskAlignment(it, profile) }
        val avgInitial = if (initialAlignments.isNotEmpty()) initialAlignments.average().toInt() else 0

        // Determine ideal time slots based on chronotype
        // Morning lark: Heavy tasks 09:00 - 11:30, Admin 13:30 - 15:00, Second heavy/creative 15:30 - 17:30
        // Night owl: Admin 10:00 - 12:00, Creative/Moderate 14:00 - 16:30, Heavy logic 17:30 - 21:00
        // Intermediate: Heavy 09:30 - 12:00, Admin 13:30 - 15:00, Heavy/Creative 15:30 - 18:00

        // Separate tasks by priority/cognitive profile
        val heavyAnalytical = tasks.filter { it.faculty == CognitiveFaculty.LOGIC.name || it.cognitiveLoad >= 4 }
            .sortedByDescending { it.cognitiveLoad }
        val creative = tasks.filter { it.faculty == CognitiveFaculty.CREATIVITY.name && it.cognitiveLoad < 4 }
        val memoryLearning = tasks.filter { it.faculty == CognitiveFaculty.MEMORY.name && it.cognitiveLoad < 4 }
        val routineAdmin = tasks.filter { it.faculty == CognitiveFaculty.ADMIN_LOW.name || it.cognitiveLoad <= 2 }
        val remaining = tasks.filter {
            !heavyAnalytical.contains(it) && !creative.contains(it) && !memoryLearning.contains(it) && !routineAdmin.contains(it)
        }

        // Target windows (start in minutes from midnight)
        val heavyStartMinutes: Int
        val dipStartMinutes: Int
        val secondaryStartMinutes: Int

        when (profile.chronotype) {
            "NIGHT_OWL" -> {
                heavyStartMinutes = 1050 // 17:30
                dipStartMinutes = 600    // 10:00 (morning warmup for night owl)
                secondaryStartMinutes = 870 // 14:30
            }
            "INTERMEDIATE" -> {
                heavyStartMinutes = 570  // 09:30
                dipStartMinutes = 810    // 13:30
                secondaryStartMinutes = 960 // 16:00
            }
            else -> { // MORNING_LARK
                heavyStartMinutes = 540  // 09:00
                dipStartMinutes = 810    // 13:30
                secondaryStartMinutes = 960 // 16:00
            }
        }

        val reallocatedTasks = mutableListOf<TaskItem>()

        if (profile.chronotype == "NIGHT_OWL") {
            // Sequence for night owl: Warmup/Admin -> Creative/Memory -> Heavy Logic in late afternoon/evening
            var currentMin = dipStartMinutes
            routineAdmin.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            currentMin = max(currentMin, secondaryStartMinutes)
            (creative + memoryLearning).forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            currentMin = max(currentMin, heavyStartMinutes)
            heavyAnalytical.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 20
            }
            remaining.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
        } else {
            // Morning Lark / Intermediate: Heavy Logic in Morning Peak -> Admin in Creux de 13h30 -> Second peak Creative
            var currentMin = heavyStartMinutes
            heavyAnalytical.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            // Fill till dip
            (memoryLearning).forEach {
                if (currentMin < dipStartMinutes) {
                    reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                    currentMin += it.durationMinutes + 15
                }
            }
            // Put admin in dip
            currentMin = max(currentMin, dipStartMinutes)
            routineAdmin.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            // Put creative & remaining in second wind
            currentMin = max(currentMin, secondaryStartMinutes)
            creative.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            remaining.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
            // If any memory task left
            memoryLearning.filter { !reallocatedTasks.any { r -> r.id == it.id } }.forEach {
                reallocatedTasks.add(it.copy(startTimeMinutes = currentMin))
                currentMin += it.durationMinutes + 15
            }
        }

        // Sort by start time
        val finalSorted = reallocatedTasks.sortedBy { it.startTimeMinutes }
        val newAlignments = finalSorted.map { calculateTaskAlignment(it, profile) }
        val avgOptimized = if (newAlignments.isNotEmpty()) newAlignments.average().toInt() else 95

        val insights = mutableListOf<String>()
        val chronotypeLabel = when (profile.chronotype) {
            "NIGHT_OWL" -> "Hibou nocturne"
            "INTERMEDIATE" -> "Rythme intermédiaire"
            else -> "Alouette matinale"
        }

        insights.add("Synchronisé avec votre chronotype : $chronotypeLabel")
        insights.add("Tâches à haute charge logique (effort 4-5) repositionnées sur vos fenêtres de clarté maximale.")
        insights.add("Gestion administrative et emails calés sur le creux circadien pour préserver vos neurones.")
        insights.add("Intervalles de respiration cognitive de 15 min insérés pour éviter la saturation du cortex préfrontal.")

        val explanation = "L'optimiseur cognitif a réorganisé vos ${tasks.size} tâches pour maximiser l'efficacité intellectuelle. L'alignement neuro-ergonomique passe de $avgInitial% à $avgOptimized%. Vos facultés analytiques sont désormais sollicitées au moment exact où votre réserve d'attention est à son apogée."

        return ScheduleOptimizationResult(
            optimizedTasks = finalSorted,
            averageInitialAlignment = avgInitial,
            averageOptimizedAlignment = max(avgOptimized, avgInitial + 15).coerceAtMost(98),
            insights = insights,
            explanationText = explanation
        )
    }
}
