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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Faculty
import com.example.ui.theme.AcademicCard
import com.example.ui.theme.AcademicCardBorder
import com.example.ui.theme.ArchitectureCategory
import com.example.ui.theme.EconomyCategory
import com.example.ui.theme.EngineeringCategory
import com.example.ui.theme.HealthCategory
import com.example.ui.theme.LawCategory
import com.example.ui.theme.LiteratureCategory
import com.example.ui.theme.ScienceCategory
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.ui.theme.TunisiaGold
import com.example.ui.theme.TunisiaRed
import com.example.ui.utils.IntentUtils

@Composable
fun FacultyCard(
    faculty: Faculty,
    onFacultyClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = when (faculty.category) {
        "Médecine & Santé" -> HealthCategory
        "Ingénierie & Informatique" -> EngineeringCategory
        "Sciences & Technologies" -> ScienceCategory
        "Droit & Politique" -> LawCategory
        "Économie & Gestion" -> EconomyCategory
        "Lettres & Humaines" -> LiteratureCategory
        "Art & Architecture" -> ArchitectureCategory
        else -> TunisiaGold
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, AcademicCardBorder, RoundedCornerShape(16.dp))
            .clickable { onFacultyClick() }
            .testTag("faculty_card_${faculty.id}"),
        colors = CardDefaults.cardColors(containerColor = AcademicCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category Badge + Short Code + Favorite Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Short Code Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(categoryColor.copy(alpha = 0.2f))
                            .border(1.dp, categoryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = faculty.shortCode,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Category Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = faculty.category,
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }

                // Favorite Icon Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("favorite_button_${faculty.id}")
                ) {
                    Icon(
                        imageVector = if (faculty.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favori",
                        tint = if (faculty.isFavorite) TunisiaRed else TextTertiaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Faculty Name (French)
            Text(
                text = faculty.name,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )

            // Arabic Name
            Text(
                text = faculty.arabicName,
                color = TextSecondaryDark,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // University & Governorate Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = TunisiaGold,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = faculty.university,
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Address & Governorate
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TunisiaRed,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${faculty.address} • ${faculty.governorate}",
                    color = TextTertiaryDark,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GPS Coordinates Chip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "GPS: ${String.format("%.4f", faculty.latitude)}° N, ${String.format("%.4f", faculty.longitude)}° E",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Gouvernorat: ${faculty.governorate}",
                        color = TunisiaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: Google Maps, Directions, Call
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open in Google Maps Button
                Button(
                    onClick = {
                        IntentUtils.openGoogleMaps(
                            context = context,
                            latitude = faculty.latitude,
                            longitude = faculty.longitude,
                            label = faculty.name
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_google_maps_${faculty.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TunisiaRed,
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Google Maps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Directions Button
                OutlinedButton(
                    onClick = {
                        IntentUtils.openDirections(
                            context = context,
                            latitude = faculty.latitude,
                            longitude = faculty.longitude
                        )
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_directions_${faculty.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Itinéraire",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Itinéraire",
                        fontSize = 12.sp
                    )
                }

                // Direct Call Button
                OutlinedButton(
                    onClick = {
                        IntentUtils.dialPhone(context, faculty.phone)
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_call_${faculty.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Appeler",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}
