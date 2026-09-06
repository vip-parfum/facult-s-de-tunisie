package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.CognitiveSchedulerEngine
import com.example.data.model.CognitiveFaculty
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import com.example.ui.components.CircadianEnergyGraph
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive

@Composable
fun ScheduleScreen(
    tasks: List<TaskItem>,
    profile: UserCognitiveProfile,
    selectedDayOffset: Int,
    onSelectDay: (Int) -> Unit,
    onAddTask: () -> Unit,
    onStartFocus: (TaskItem) -> Unit,
    onToggleTask: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onOptimizeSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("Aujourd'hui", "Demain", "Après-demain")

    val taskAlignments = tasks.map { CognitiveSchedulerEngine.calculateTaskAlignment(it, profile) }
    val avgAlignment = if (taskAlignments.isNotEmpty()) taskAlignments.average().toInt() else 100

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Day selector Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedDayOffset,
                    containerColor = Color.Transparent,
                    contentColor = ElectricCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    dayLabels.forEachIndexed { index, label ->
                        Tab(
                            selected = selectedDayOffset == index,
                            onClick = { onSelectDay(index) },
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (selectedDayOffset == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("day_tab_$index")
                        )
                    }
                }
            }

            // Circadian Energy Curve
            item {
                CircadianEnergyGraph(profile = profile)
            }

            // Cognitive Optimization Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Alignement neuro-cognitif : ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$avgAlignment%",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (avgAlignment >= 80) EmeraldGlow else AmberGaze
                                )
                            }
                            Text(
                                text = if (avgAlignment >= 80)
                                    "Vos tâches sont bien calées sur vos pics de concentration."
                                else
                                    "Certaines tâches lourdes tombent pendant votre creux d'énergie.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onOptimizeSchedule,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("optimize_schedule_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF090D16),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Optimiser",
                                color = Color(0xFF090D16),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Emploi du temps (${tasks.size} tâches)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Empty state
            if (tasks.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aucune tâche planifiée",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ajoutez une tâche pour voir comment elle s'aligne avec vos facultés intellectuelles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tasks List
            items(tasks, key = { it.id }) { task ->
                val alignment = CognitiveSchedulerEngine.calculateTaskAlignment(task, profile)
                TaskCard(
                    task = task,
                    alignmentScore = alignment,
                    onStartFocus = { onStartFocus(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }

        // Floating Action Button to add task
        FloatingActionButton(
            onClick = onAddTask,
            containerColor = ElectricCyan,
            contentColor = Color(0xFF090D16),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter une tâche cognitive")
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    alignmentScore: Int,
    onStartFocus: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val facultyEnum = CognitiveFaculty.values().firstOrNull { it.name == task.faculty } ?: CognitiveFaculty.LOGIC
    val facultyColor = Color(facultyEnum.colorHex)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: time, faculty badge, alignment badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Text(
                    text = "${task.getFormattedStartTime()} - ${task.getFormattedEndTime()} (${task.durationMinutes}m)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Faculty badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = facultyColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = facultyEnum.displayName.split("&").first().trim(),
                            style = MaterialTheme.typography.labelSmall,
                            color = facultyColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Alignment badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (alignmentScore >= 75) EmeraldGlow.copy(alpha = 0.2f) else AmberGaze.copy(alpha = 0.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (alignmentScore >= 75) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (alignmentScore >= 75) EmeraldGlow else AmberGaze,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$alignmentScore%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (alignmentScore >= 75) EmeraldGlow else AmberGaze,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Cognitive load dots & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cognitive Load Dots (1 to 5)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Effort : ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    for (i in 1..5) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i <= task.cognitiveLoad)
                                        (if (task.cognitiveLoad >= 4) AmberGaze else ElectricCyan)
                                    else
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!task.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ElectricCyan.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clickable(onClick = onStartFocus)
                                .testTag("start_focus_button_${task.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Focus",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = "Marquer comme fait",
                            tint = if (task.isCompleted) EmeraldGlow else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
