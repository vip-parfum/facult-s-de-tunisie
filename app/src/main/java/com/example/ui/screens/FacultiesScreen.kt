package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserCognitiveProfile
import com.example.ui.components.CognitiveRadarChart
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.VioletCognitive

@Composable
fun FacultiesScreen(
    profile: UserCognitiveProfile,
    onOpenCalibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Calibration trigger
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cartographie Cognitive",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Modèle apprenant de vos facultés intellectuelles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onOpenCalibration,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("recalibrate_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Calibrer", fontSize = 12.sp)
                }
            }
        }

        // Pentagon Radar Chart Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Radar des 5 Facultés Fondamentales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Moyenne générale : ${((profile.logicAnalyticalScore + profile.creativeLateralScore + profile.memoryLearningScore + profile.sustainedAttentionScore + profile.mentalEnduranceScore) / 5)}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    CognitiveRadarChart(profile = profile)
                }
            }
        }

        // Chronotype & Attention span quick stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chronotype Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val (icon, title, desc) = when (profile.chronotype) {
                            "NIGHT_OWL" -> Triple(Icons.Default.NightsStay, "Hibou", "Pic 18h - 22h")
                            "INTERMEDIATE" -> Triple(Icons.Default.Bolt, "Équilibré", "Pic 10h & 16h")
                            else -> Triple(Icons.Default.WbSunny, "Alouette", "Pic 08h30 - 11h30")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = icon, contentDescription = null, tint = AmberGaze, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chronotype", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Attention Span Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Empan Focus", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${profile.attentionSpanMinutes} min", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Bloc de travail idéal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Section Title: Detail of each faculty
        item {
            Text(
                text = "Détail & Dynamique des Facultés",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 1. Logique & Raisonnement
        item {
            FacultyDetailCard(
                icon = Icons.Default.Psychology,
                title = "Logique & Raisonnement Analytique",
                score = profile.logicAnalyticalScore,
                accentColor = Color(0xFF3B82F6),
                description = "Capacité à résoudre des équations complexes, programmer, concevoir des architectures et raisonner rigoureusement.",
                bestTimeSlot = when (profile.chronotype) {
                    "NIGHT_OWL" -> "Fin de journée (17h30 - 21h00)"
                    else -> "Matinée fraîche (08h30 - 11h30)"
                }
            )
        }

        // 2. Créativité & Idéation
        item {
            FacultyDetailCard(
                icon = Icons.Default.Lightbulb,
                title = "Créativité & Pensée Divergente",
                score = profile.creativeLateralScore,
                accentColor = AmberGaze,
                description = "Génération d'idées neuves, résolution latérale de problèmes, conceptualisation visuelle et rédaction originale.",
                bestTimeSlot = "Transitions circadiennes & mode diffus (11h00 ou fin d'après-midi)"
            )
        }

        // 3. Mémoire & Apprentissage
        item {
            FacultyDetailCard(
                icon = Icons.Default.School,
                title = "Mémoire & Assimilation de Savoir",
                score = profile.memoryLearningScore,
                accentColor = EmeraldGlow,
                description = "Capacité d'encodage mnésique, rétention de lecture approfondie, consolidation de connaissances nouvelles.",
                bestTimeSlot = "Matinée et début de soirée avant le sommeil"
            )
        }

        // 4. Attention Soutenue
        item {
            FacultyDetailCard(
                icon = Icons.Default.Timer,
                title = "Attention & Focus Soutenu",
                score = profile.sustainedAttentionScore,
                accentColor = VioletCognitive,
                description = "Résistance aux distractions externes, immersion en état de Flow ininterrompu pendant ${profile.attentionSpanMinutes} minutes.",
                bestTimeSlot = "Cycles réguliers séparés de pauses sans écran"
            )
        }

        // 5. Endurance Mentale
        item {
            FacultyDetailCard(
                icon = Icons.Default.Bolt,
                title = "Endurance Cognitive & Récupération",
                score = profile.mentalEnduranceScore,
                accentColor = Color(0xFFEC4899),
                description = "Capacité à enchaîner plusieurs sessions intellectuelles sans saturation du cortex préfrontal.",
                bestTimeSlot = "Vigilance accrue si pauses de 10 min respectées"
            )
        }

        // Continuous learning status card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Apprentissage Continu Actif",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.totalSessionsCompleted} sessions focus enregistrées. Le modèle réajuste vos créneaux après chaque évaluation de concentration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FacultyDetailCard(
    icon: ImageVector,
    title: String,
    score: Int,
    accentColor: Color,
    description: String,
    bestTimeSlot: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { score / 100f },
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Créneau optimal : ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bestTimeSlot,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                }
            }
        }
    }
}
