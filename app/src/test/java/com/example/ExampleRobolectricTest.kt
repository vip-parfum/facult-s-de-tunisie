package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.CognitiveSchedulerEngine
import com.example.data.model.CognitiveFaculty
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Agenda Cognitif", appName)
    }

    @Test
    fun `cognitive alignment reflects chronotype peak`() {
        val profile = UserCognitiveProfile(chronotype = "MORNING_LARK")
        val morningHeavyTask = TaskItem(
            title = "Algorithmique complexe",
            faculty = CognitiveFaculty.LOGIC.name,
            cognitiveLoad = 5,
            startTimeMinutes = 540, // 09:00
            durationMinutes = 90
        )
        val morningAlignment = CognitiveSchedulerEngine.calculateTaskAlignment(morningHeavyTask, profile)
        assertTrue("Morning heavy task should have high alignment for morning lark", morningAlignment >= 80)

        val afternoonHeavyTask = TaskItem(
            title = "Algorithmique complexe",
            faculty = CognitiveFaculty.LOGIC.name,
            cognitiveLoad = 5,
            startTimeMinutes = 840, // 14:00 (dip)
            durationMinutes = 90
        )
        val afternoonAlignment = CognitiveSchedulerEngine.calculateTaskAlignment(afternoonHeavyTask, profile)
        assertTrue("Afternoon heavy task during dip should have lower alignment", afternoonAlignment < morningAlignment)
    }

    @Test
    fun `schedule optimization improves alignment`() {
        val profile = UserCognitiveProfile(chronotype = "MORNING_LARK")
        val tasks = listOf(
            TaskItem(
                title = "Calcul complexe",
                faculty = CognitiveFaculty.LOGIC.name,
                cognitiveLoad = 5,
                startTimeMinutes = 840, // 14:00 (dip)
                durationMinutes = 90
            ),
            TaskItem(
                title = "Emails",
                faculty = CognitiveFaculty.ADMIN_LOW.name,
                cognitiveLoad = 1,
                startTimeMinutes = 540, // 09:00 (peak wasted on emails)
                durationMinutes = 45
            )
        )
        val result = CognitiveSchedulerEngine.optimizeSchedule(tasks, profile)
        assertTrue("Optimization should improve average alignment score", result.averageOptimizedAlignment > result.averageInitialAlignment)
    }
}
