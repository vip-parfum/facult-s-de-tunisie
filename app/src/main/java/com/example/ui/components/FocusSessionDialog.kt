package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive
import kotlinx.coroutines.delay

@Composable
fun FocusSessionDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onFinishAndLearn: (rating: Int, fatigue: Int) -> Unit
) {
    var isRunning by remember { mutableStateOf(true) }
    var secondsRemaining by remember { mutableIntStateOf(task.durationMinutes * 60) }
    var isCompletedStep by remember { mutableStateOf(false) }

    // Feedback states for continuous learning
    var userRating by remember { mutableIntStateOf(4) }
    var fatigueRating by remember { mutableFloatStateOf(2f) }

    // Breathing animation for focus pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(isRunning, secondsRemaining) {
        if (isRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        } else if (secondsRemaining <= 0 && !isCompletedStep) {
            isCompletedStep = true
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isCompletedStep) "Session Focus Cognitif" else "Apprentissage & Bilan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Quitter")
                }
            }
        },
        text = {
            if (!isCompletedStep) {
                // Active Timer Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Faculté : ${task.faculty} • Charge : ${task.cognitiveLoad}/5",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pulse Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(160.dp)
                            .scale(if (isRunning) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formattedTime,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = if (isRunning) "Concentration active" else "En pause",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isRunning = !isRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) MaterialTheme.colorScheme.surfaceVariant else ElectricCyan
                            )
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isRunning) MaterialTheme.colorScheme.onSurface else Color(0xFF090D16)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRunning) "Pause" else "Reprendre",
                                color = if (isRunning) MaterialTheme.colorScheme.onSurface else Color(0xFF090D16)
                            )
                        }

                        Button(
                            onClick = { isCompletedStep = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGlow)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Terminer", color = Color.White)
                        }
                    }
                }
            } else {
                // Post-task Continuous Learning & Feedback
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Entraînement du modèle cognitif",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "Votre ressenti permet à l'application d'ajuster la mesure de vos facultés intellectuelles et d'affiner vos créneaux futurs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Star Rating (Focus quality)
                    Column {
                        Text(
                            text = "Qualité de concentration / fluidité :",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = { userRating = i },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (i <= userRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "$i étoiles",
                                        tint = if (i <= userRating) AmberGaze else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Perceived Cognitive Fatigue
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fatigue mentale ressentie :",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            val fatigueText = when (fatigueRating.toInt()) {
                                1 -> "1/5 • Esprit très frais"
                                2 -> "2/5 • Légère fatigue normale"
                                3 -> "3/5 • Effort modéré"
                                4 -> "4/5 • Début de saturation"
                                else -> "5/5 • Épuisement cognitif"
                            }
                            Text(
                                text = fatigueText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (fatigueRating >= 4) AmberGaze else EmeraldGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = fatigueRating,
                            onValueChange = { fatigueRating = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = if (fatigueRating >= 4) AmberGaze else EmeraldGlow,
                                activeTrackColor = if (fatigueRating >= 4) AmberGaze else EmeraldGlow
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isCompletedStep) {
                Button(
                    onClick = {
                        onFinishAndLearn(userRating, fatigueRating.toInt())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    modifier = Modifier.testTag("submit_feedback_button")
                ) {
                    Text(
                        text = "Enregistrer & Mettre à jour l'IA",
                        color = Color(0xFF090D16),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isCompletedStep) "Passer" else "Fermer")
            }
        }
    )
}
