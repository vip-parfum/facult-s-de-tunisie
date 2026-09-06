package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserCognitiveProfile
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.VioletCognitive
import kotlin.math.cos
import kotlin.math.sin

data class RadarAxis(val label: String, val value: Float, val maxValue: Float = 100f)

@Composable
fun CognitiveRadarChart(
    profile: UserCognitiveProfile,
    modifier: Modifier = Modifier
) {
    val axes = listOf(
        RadarAxis("Logique", profile.logicAnalyticalScore.toFloat()),
        RadarAxis("Créativité", profile.creativeLateralScore.toFloat()),
        RadarAxis("Mémoire", profile.memoryLearningScore.toFloat()),
        RadarAxis("Attention", profile.sustainedAttentionScore.toFloat()),
        RadarAxis("Endurance", profile.mentalEnduranceScore.toFloat())
    )

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(profile) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(800))
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .height(240.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val numAxes = axes.size
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (minOf(size.width, size.height) / 2f) - 34.dp.toPx()

            // Draw concentric web polygons (levels: 25%, 50%, 75%, 100%)
            val levels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            for (level in levels) {
                val webPath = Path()
                for (i in 0 until numAxes) {
                    val angle = (2 * Math.PI / numAxes * i - Math.PI / 2).toFloat()
                    val r = radius * level
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) webPath.moveTo(x, y) else webPath.lineTo(x, y)
                }
                webPath.close()
                drawPath(
                    path = webPath,
                    color = outlineColor,
                    style = Stroke(width = if (level == 1.0f) 1.8f else 1.0f)
                )
            }

            // Draw spokes from center and text labels
            for (i in 0 until numAxes) {
                val angle = (2 * Math.PI / numAxes * i - Math.PI / 2).toFloat()
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                drawLine(
                    color = outlineColor,
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 1.2f
                )

                // Label positioning
                val labelDist = radius + 22.dp.toPx()
                val labelX = center.x + labelDist * cos(angle)
                val labelY = center.y + labelDist * sin(angle) + 4.dp.toPx()

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = textColor.hashCode()
                        textSize = with(density) { 11.sp.toPx() }
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    }
                    drawText("${axes[i].label} (${axes[i].value.toInt()}%)", labelX, labelY, paint)
                }
            }

            // Draw animated filled data polygon
            val dataPath = Path()
            val points = mutableListOf<Offset>()
            for (i in 0 until numAxes) {
                val angle = (2 * Math.PI / numAxes * i - Math.PI / 2).toFloat()
                val normalizedVal = (axes[i].value / axes[i].maxValue).coerceIn(0.1f, 1.0f) * animationProgress.value
                val r = radius * normalizedVal
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                val point = Offset(x, y)
                points.add(point)
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Draw semi-transparent gradient fill
            drawPath(
                path = dataPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.45f),
                        secondaryColor.copy(alpha = 0.20f)
                    ),
                    center = center,
                    radius = radius
                )
            )

            // Draw outer perimeter border
            drawPath(
                path = dataPath,
                color = ElectricCyan,
                style = Stroke(width = 3f)
            )

            // Draw vertex dots
            for (p in points) {
                drawCircle(
                    color = Color.White,
                    radius = 4.5f,
                    center = p
                )
                drawCircle(
                    color = ElectricCyan,
                    radius = 2.5f,
                    center = p
                )
            }
        }
    }
}
