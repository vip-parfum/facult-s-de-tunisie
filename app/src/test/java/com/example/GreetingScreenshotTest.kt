package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.UserCognitiveProfile
import com.example.ui.screens.FacultiesScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun app_faculties_screenshot() {
        val profile = UserCognitiveProfile(
            logicAnalyticalScore = 90,
            creativeLateralScore = 80,
            memoryLearningScore = 75,
            sustainedAttentionScore = 85,
            mentalEnduranceScore = 80,
            chronotype = "MORNING_LARK",
            attentionSpanMinutes = 45
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                FacultiesScreen(
                    profile = profile,
                    onOpenCalibration = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/faculties.png")
    }
}
