package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.CognitiveSchedulerEngine
import com.example.data.model.UserCognitiveProfile
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive
import java.util.Calendar

@Composable
fun CircadianEnergyGraph(
    profile: UserCognitiveProfile,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary

    // Current hour in minutes from midnight
    val cal = Calendar.getInstance()
    val currentMinute = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

    val chronotypeDescription = when (profile.chronotype) {
        "NIGHT_OWL" -> "Pic intellectuel décalé en fin de journée (18h - 22h)"
        "INTERMEDIATE" -> "Deux pics stables (10h - 12h30 et 16h - 18h30)"
        else -> "Pic matinal majeur (08h30 - 11h30), creux post-déjeuner"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Courbe d'Énergie & Clarté Cognitive",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
                Text(
                    text = chronotypeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 10.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startHour = 6
                val endHour = 22
                val totalHours = endHour - startHour

                // Draw horizontal guide lines
                val heights = listOf(0.25f, 0.5f, 0.75f)
                for (h in heights) {
                    val y = size.height * (1f - h)
                    drawLine(
                        color = outlineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }

                val curvePath = Path()
                val fillPath = Path()
                val samples = 60
                val points = mutableListOf<Offset>()

                for (i in 0..samples) {
                    val progress = i.toFloat() / samples
                    val minute = (startHour * 60 + progress * totalHours * 60).toInt()
                    val alertness = CognitiveSchedulerEngine.getAlertnessAtMinute(minute, profile) // 0..100
                    val normalizedY = 1f - (alertness / 100f)

                    val x = progress * size.width
                    val y = normalizedY * (size.height - 18.dp.toPx()) + 6.dp.toPx()

                    points.add(Offset(x, y))
                    if (i == 0) {
                        curvePath.moveTo(x, y)
                        fillPath.moveTo(x, size.height)
                        fillPath.lineTo(x, y)
                    } else {
                        curvePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                }
                fillPath.lineTo(size.width, size.height)
                fillPath.close()

                // Draw gradient under curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.35f),
                            VioletCognitive.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )

                // Draw smooth curve stroke
                drawPath(
                    path = curvePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(ElectricCyan, AmberGaze, VioletCognitive)
                    ),
                    style = Stroke(width = 3.5f)
                )

                // Draw current time vertical indicator if within range
                if (currentMinute in (startHour * 60)..(endHour * 60)) {
                    val currProgress = (currentMinute - startHour * 60).toFloat() / (totalHours * 60)
                    val currX = currProgress * size.width
                    val currAlertness = CognitiveSchedulerEngine.getAlertnessAtMinute(currentMinute, profile)
                    val currY = (1f - (currAlertness / 100f)) * (size.height - 18.dp.toPx()) + 6.dp.toPx()

                    drawLine(
                        color = AmberGaze,
                        start = Offset(currX, 0f),
                        end = Offset(currX, size.height),
                        strokeWidth = 1.8f
                    )
                    drawCircle(
                        color = AmberGaze,
                        radius = 4.5f,
                        center = Offset(currX, currY)
                    )
                }

                // Draw hour markers at bottom
                val hourSteps = listOf(6, 9, 12, 15, 18, 21)
                for (h in hourSteps) {
                    val prog = (h - startHour).toFloat() / totalHours
                    val x = prog * size.width
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = onSurface.copy(alpha = 0.6f).hashCode()
                            textSize = with(density) { 10.sp.toPx() }
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText("${h}h", x.coerceIn(12f, size.width - 12f), size.height, paint)
                    }
                }
            }
        }

        // Legend row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan)
                )
                Text(
                    text = " Clarté maximale",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AmberGaze)
                )
                Text(
                    text = " Maintenant",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VioletCognitive)
                )
                Text(
                    text = " Zone diffuse",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
