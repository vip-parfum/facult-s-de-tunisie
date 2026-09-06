package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ai.CognitiveSchedulerEngine
import com.example.data.model.CognitiveFaculty
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive

@Composable
fun AddTaskDialog(
    profile: UserCognitiveProfile,
    dayOffset: Int,
    onDismiss: () -> Unit,
    onConfirm: (TaskItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFaculty by remember { mutableStateOf(CognitiveFaculty.LOGIC) }
    var cognitiveLoad by remember { mutableFloatStateOf(3f) }
    var startHour by remember { mutableIntStateOf(9) }
    var startMinute by remember { mutableIntStateOf(0) }
    var durationMinutes by remember { mutableIntStateOf(profile.attentionSpanMinutes) }

    val tempTask = TaskItem(
        title = title.ifBlank { "Aperçu" },
        faculty = selectedFaculty.name,
        cognitiveLoad = cognitiveLoad.toInt(),
        startTimeMinutes = startHour * 60 + startMinute,
        durationMinutes = durationMinutes,
        dayOffset = dayOffset
    )

    val alignmentScore = CognitiveSchedulerEngine.calculateTaskAlignment(tempTask, profile)

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
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nouvelle Tâche Cognitive",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de la tâche") },
                    placeholder = { Text("ex: Résolution problème algorithmique...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Détails / Objectif intellectuel") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Faculty Selector
                Text(
                    text = "Faculté intellectuelle sollicitée :",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CognitiveFaculty.values().toList()) { faculty ->
                        val isSelected = selectedFaculty == faculty
                        val facultyColor = Color(faculty.colorHex)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) facultyColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, facultyColor) else null,
                            modifier = Modifier
                                .clickable { selectedFaculty = faculty }
                                .padding(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(facultyColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = faculty.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Cognitive Load Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Effort / Charge cognitive :",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val loadDesc = when (cognitiveLoad.toInt()) {
                            1 -> "1/5 • Faible (Routine)"
                            2 -> "2/5 • Modéré"
                            3 -> "3/5 • Soutenu"
                            4 -> "4/5 • Très intense"
                            else -> "5/5 • Effort Maximal"
                        }
                        Text(
                            text = loadDesc,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (cognitiveLoad >= 4) AmberGaze else ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = cognitiveLoad,
                        onValueChange = { cognitiveLoad = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = if (cognitiveLoad >= 4) AmberGaze else ElectricCyan,
                            activeTrackColor = if (cognitiveLoad >= 4) AmberGaze else ElectricCyan
                        )
                    )
                }

                // Time selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Heure de début",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = "%02d".format(startHour),
                                onValueChange = {
                                    val v = it.toIntOrNull()
                                    if (v != null && v in 0..23) startHour = v
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Text(" : ", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = "%02d".format(startMinute),
                                onValueChange = {
                                    val v = it.toIntOrNull()
                                    if (v != null && v in 0..59) startMinute = v
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Durée (min)",
                            style = MaterialTheme.typography.labelSmall
                        )
                        OutlinedTextField(
                            value = durationMinutes.toString(),
                            onValueChange = {
                                val v = it.toIntOrNull()
                                if (v != null && v in 5..360) durationMinutes = v
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                // Real-time Cognitive Alignment Preview
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            alignmentScore >= 80 -> EmeraldGlow.copy(alpha = 0.15f)
                            alignmentScore >= 60 -> ElectricCyan.copy(alpha = 0.15f)
                            else -> AmberGaze.copy(alpha = 0.15f)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (alignmentScore >= 75) EmeraldGlow else AmberGaze,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Alignement cognitif : $alignmentScore%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val advice = when {
                                alignmentScore >= 85 -> "Excellent créneau pour cette intensité !"
                                alignmentScore >= 65 -> "Bon équilibre avec votre rythme."
                                else -> "Attention : charge élevée prévue pendant un creux d'énergie."
                            }
                            Text(
                                text = advice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(tempTask.copy(title = title.trim(), description = description.trim()))
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                modifier = Modifier.testTag("submit_task_button")
            ) {
                Text("Planifier la tâche", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
