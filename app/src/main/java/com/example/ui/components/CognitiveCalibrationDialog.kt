package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserCognitiveProfile
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive

@Composable
fun CognitiveCalibrationDialog(
    currentProfile: UserCognitiveProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (UserCognitiveProfile) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    var selectedChronotype by remember { mutableStateOf(currentProfile.chronotype) }
    var selectedAttentionSpan by remember { mutableIntStateOf(currentProfile.attentionSpanMinutes) }
    var primaryFaculty by remember { mutableStateOf("LOGIC") }
    var recoveryStyle by remember { mutableStateOf("BALANCED") }

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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Diagnostic des Facultés ($step/3)",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (step) {
                    1 -> {
                        // Chronotype Assessment
                        Text(
                            text = "1. Quel est votre rythme circadien & pic d'éveil ?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        Text(
                            text = "L'application adapte le placement de vos tâches selon la libération naturelle de vos neurotransmetteurs d'alerte.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.WbSunny,
                            title = "Alouette Matinale",
                            subtitle = "Clarté mentale maximale de 08h30 à 11h30. Creux net après le repas de midi.",
                            isSelected = selectedChronotype == "MORNING_LARK",
                            onClick = { selectedChronotype = "MORNING_LARK" }
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.Bolt,
                            title = "Rythme Équilibré",
                            subtitle = "Deux pics stables : matinée (10h - 12h30) et fin d'après-midi (16h - 18h30).",
                            isSelected = selectedChronotype == "INTERMEDIATE",
                            onClick = { selectedChronotype = "INTERMEDIATE" }
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.NightsStay,
                            title = "Hibou Nocturne",
                            subtitle = "Démarrage progressif le matin. Apogée de concentration entre 17h30 et 22h00.",
                            isSelected = selectedChronotype == "NIGHT_OWL",
                            onClick = { selectedChronotype = "NIGHT_OWL" }
                        )
                    }

                    2 -> {
                        // Attention Span
                        Text(
                            text = "2. Quelle est votre endurance de focus ininterrompu ?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Définit la longueur optimale des blocs de travail avant de suggérer un sas de décompression.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val spans = listOf(
                            25 to "25 minutes (Méthode Pomodoro / Cycles dynamiques)",
                            45 to "45 minutes (Standard neuro-optimal)",
                            60 to "60 minutes (Endurance soutenue)",
                            90 to "90 minutes (Cycle ultra-profond / Deep Work)"
                        )

                        spans.forEach { (mins, label) ->
                            CalibrationOptionCard(
                                icon = Icons.Default.Timer,
                                title = "$mins minutes",
                                subtitle = label,
                                isSelected = selectedAttentionSpan == mins,
                                onClick = { selectedAttentionSpan = mins }
                            )
                        }
                    }

                    3 -> {
                        // Dominant Cognitive Faculty
                        Text(
                            text = "3. Votre domaine intellectuel de prédilection :",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Ajuste les scores de base de votre radar cognitif personnel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.Psychology,
                            title = "Logique & Résolution de Problèmes",
                            subtitle = "Algorithmique, mathématiques, analyse critique, ingénierie.",
                            isSelected = primaryFaculty == "LOGIC",
                            onClick = { primaryFaculty = "LOGIC" }
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.Lightbulb,
                            title = "Créativité & Pensée Divergente",
                            subtitle = "Idéation, design, écriture, association libre d'idées.",
                            isSelected = primaryFaculty == "CREATIVITY",
                            onClick = { primaryFaculty = "CREATIVITY" }
                        )

                        CalibrationOptionCard(
                            icon = Icons.Default.AutoAwesome,
                            title = "Mémorisation & Synthèse Documentaire",
                            subtitle = "Lecture de fond, assimilation rapide, structuration de concepts.",
                            isSelected = primaryFaculty == "MEMORY",
                            onClick = { primaryFaculty = "MEMORY" }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step < 3) {
                        step += 1
                    } else {
                        // Generate updated profile
                        var logic = 75
                        var creative = 70
                        var memory = 70
                        var attention = 75
                        var endurance = 75

                        when (primaryFaculty) {
                            "LOGIC" -> {
                                logic = 90
                                attention = 85
                            }
                            "CREATIVITY" -> {
                                creative = 92
                                logic = 75
                            }
                            "MEMORY" -> {
                                memory = 90
                                endurance = 85
                            }
                        }

                        if (selectedAttentionSpan >= 60) {
                            attention += 8
                            endurance += 6
                        }

                        val updated = currentProfile.copy(
                            chronotype = selectedChronotype,
                            attentionSpanMinutes = selectedAttentionSpan,
                            logicAnalyticalScore = logic.coerceIn(10, 100),
                            creativeLateralScore = creative.coerceIn(10, 100),
                            memoryLearningScore = memory.coerceIn(10, 100),
                            sustainedAttentionScore = attention.coerceIn(10, 100),
                            mentalEnduranceScore = endurance.coerceIn(10, 100),
                            isCalibrated = true,
                            lastUpdated = System.currentTimeMillis()
                        )
                        onSaveProfile(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                modifier = Modifier.testTag("next_step_button")
            ) {
                Text(
                    text = if (step < 3) "Continuer" else "Finaliser le diagnostic",
                    color = Color(0xFF090D16),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            if (step > 1) {
                TextButton(onClick = { step -= 1 }) {
                    Text("Retour")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
}

@Composable
fun CalibrationOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) ElectricCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.8.dp, ElectricCyan) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ElectricCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
