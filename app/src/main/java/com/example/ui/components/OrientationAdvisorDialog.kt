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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.OrientationAdvisorState
import com.example.ui.theme.AcademicCard
import com.example.ui.theme.AcademicCardBorder
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.ui.theme.TunisiaGold
import com.example.ui.theme.TunisiaRed

@Composable
fun OrientationAdvisorDialog(
    state: OrientationAdvisorState,
    onDismiss: () -> Unit,
    onUpdateForm: (branch: String, score: String, interests: String, gov: String) -> Unit,
    onGenerate: () -> Unit
) {
    val bacBranches = listOf(
        "Mathématiques",
        "Sciences Expérimentales",
        "Sciences Informatiques",
        "Économie & Gestion",
        "Technique",
        "Lettres"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, AcademicCardBorder, RoundedCornerShape(24.dp)),
            color = DeepSlateSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TunisiaGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = TunisiaGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Conseiller d'Orientation IA",
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "التوجيه الجامعي التونسي • Gemini AI",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextTertiaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Bac selector
                Text(
                    text = "Section du Baccalauréat",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bacBranches.take(3).forEach { branch ->
                        val isSelected = state.bacBranch == branch
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TunisiaRed else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) TunisiaRed else Color(0xFF334155), RoundedCornerShape(8.dp))
                                .clickable { onUpdateForm(branch, state.score, state.interests, state.preferredGovernorate) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = branch,
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bacBranches.drop(3).forEach { branch ->
                        val isSelected = state.bacBranch == branch
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TunisiaRed else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) TunisiaRed else Color(0xFF334155), RoundedCornerShape(8.dp))
                                .clickable { onUpdateForm(branch, state.score, state.interests, state.preferredGovernorate) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = branch,
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Score / Formule Globale
                Text(
                    text = "Score estimé / Formule Globale (FG)",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.score,
                    onValueChange = { onUpdateForm(state.bacBranch, it, state.interests, state.preferredGovernorate) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: 165.50 (ou moyenne bac: 15.80)", color = TextTertiaryDark, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TunisiaGold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ambitions & Domaines d'intérêt
                Text(
                    text = "Domaines d'intérêt & Ambitions professionnelles",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.interests,
                    onValueChange = { onUpdateForm(state.bacBranch, state.score, it, state.preferredGovernorate) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Médecine humaine, INSAT, Génie logiciel, IHEC, Droit...", color = TextTertiaryDark, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TunisiaGold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Preferred governorate
                Text(
                    text = "Gouvernorat de préférence",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.preferredGovernorate,
                    onValueChange = { onUpdateForm(state.bacBranch, state.score, state.interests, it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Tunis, Sousse, Monastir, Sfax...", color = TextTertiaryDark, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TunisiaGold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Generate Button
                Button(
                    onClick = onGenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRed),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyse de l'orientation en cours...", color = Color.White, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obtenir mes recommandations de Facultés", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // AI Result Display
                if (state.adviceText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, TunisiaGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = state.adviceText,
                            color = TextPrimaryDark,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
