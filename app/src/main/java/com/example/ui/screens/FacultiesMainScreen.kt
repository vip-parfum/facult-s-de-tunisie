package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
import com.example.data.model.TunisiaFacultiesData
import com.example.ui.TunisiaFacultiesViewModel
import com.example.ui.components.FacultyCard
import com.example.ui.components.FacultyDetailSheet
import com.example.ui.components.OrientationAdvisorDialog
import com.example.ui.components.TunisiaMapExplorer
import com.example.ui.theme.AcademicCard
import com.example.ui.theme.AcademicCardBorder
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.ui.theme.TunisiaGold
import com.example.ui.theme.TunisiaRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultiesMainScreen(
    viewModel: TunisiaFacultiesViewModel,
    modifier: Modifier = Modifier
) {
    val faculties by viewModel.filteredFaculties.collectAsState()
    val totalFaculties by viewModel.allFaculties.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedUniv by viewModel.selectedUniversity.collectAsState()
    val selectedGov by viewModel.selectedGovernorate.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()
    val selectedFaculty by viewModel.selectedFaculty.collectAsState()
    val isMapView by viewModel.isMapView.collectAsState()
    val orientationState by viewModel.orientationState.collectAsState()

    var showUnivMenu by remember { mutableStateOf(false) }
    var showGovMenu by remember { mutableStateOf(false) }
    var showCatMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepSlateSurface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(TunisiaRed)
                            )
                            Text(
                                text = "Facultés de Tunisie",
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "كليات وتونس • Enseignement Supérieur",
                            color = TunisiaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightNavy),
                actions = {
                    // Orientation AI Advisor Button
                    IconButton(
                        onClick = { viewModel.openOrientationDialog() },
                        modifier = Modifier.testTag("btn_open_orientation_ai")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Conseiller IA",
                            tint = TunisiaGold
                        )
                    }

                    // Map View Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleMapView(!isMapView) },
                        modifier = Modifier.testTag("btn_toggle_map_view")
                    ) {
                        Icon(
                            imageVector = if (isMapView) Icons.Default.ViewList else Icons.Default.Map,
                            contentDescription = if (isMapView) "Vue Liste" else "Vue Carte",
                            tint = if (isMapView) TunisiaRed else Color(0xFF38BDF8)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("faculties_search_input"),
                    placeholder = {
                        Text(
                            text = "Rechercher une faculté, ville, diplôme (ex: FMT, INSAT, Médecine)...",
                            color = TextTertiaryDark,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TunisiaRed,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = TextTertiaryDark)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TunisiaRed,
                        unfocusedBorderColor = AcademicCardBorder,
                        focusedContainerColor = AcademicCard,
                        unfocusedContainerColor = AcademicCard,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Horizontal Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorites Only Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (onlyFavorites) TunisiaRed else AcademicCard)
                        .border(1.dp, if (onlyFavorites) TunisiaRed else AcademicCardBorder, RoundedCornerShape(20.dp))
                        .clickable { viewModel.toggleFavoritesOnly() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (onlyFavorites) Color.White else TunisiaRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Favoris",
                            color = if (onlyFavorites) Color.White else TextPrimaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Governorates Filter Chip
                Box {
                    val isGovFiltered = selectedGov != "Tous les Gouvernorats"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isGovFiltered) Color(0xFF0284C7) else AcademicCard)
                            .border(1.dp, if (isGovFiltered) Color(0xFF0284C7) else AcademicCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showGovMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isGovFiltered) "📍 $selectedGov" else "Gouvernorats",
                            color = if (isGovFiltered) Color.White else TextPrimaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = showGovMenu,
                        onDismissRequest = { showGovMenu = false }
                    ) {
                        TunisiaFacultiesData.governorates.forEach { gov ->
                            DropdownMenuItem(
                                text = { Text(gov) },
                                onClick = {
                                    viewModel.selectGovernorate(gov)
                                    showGovMenu = false
                                }
                            )
                        }
                    }
                }

                // Categories Filter Chip
                Box {
                    val isCatFiltered = selectedCat != "Toutes les Catégories"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isCatFiltered) TunisiaGold else AcademicCard)
                            .border(1.dp, if (isCatFiltered) TunisiaGold else AcademicCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showCatMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isCatFiltered) "🎓 $selectedCat" else "Filières",
                            color = if (isCatFiltered) Color.Black else TextPrimaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = showCatMenu,
                        onDismissRequest = { showCatMenu = false }
                    ) {
                        TunisiaFacultiesData.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.selectCategory(cat)
                                    showCatMenu = false
                                }
                            )
                        }
                    }
                }

                // Universities Filter Chip
                Box {
                    val isUnivFiltered = selectedUniv != "Toutes les Universités"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isUnivFiltered) Color(0xFF8B5CF6) else AcademicCard)
                            .border(1.dp, if (isUnivFiltered) Color(0xFF8B5CF6) else AcademicCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showUnivMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isUnivFiltered) "🏛️ $selectedUniv" else "Universités",
                            color = if (isUnivFiltered) Color.White else TextPrimaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = showUnivMenu,
                        onDismissRequest = { showUnivMenu = false }
                    ) {
                        TunisiaFacultiesData.universities.forEach { univ ->
                            DropdownMenuItem(
                                text = { Text(univ) },
                                onClick = {
                                    viewModel.selectUniversity(univ)
                                    showUnivMenu = false
                                }
                            )
                        }
                    }
                }

                // Reset Filters Button if any active
                if (selectedUniv != "Toutes les Universités" || selectedGov != "Tous les Gouvernorats" || selectedCat != "Toutes les Catégories" || onlyFavorites || searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF334155))
                            .clickable { viewModel.resetFilters() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Réinitialiser", color = Color(0xFFF1F5F9), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Body: List View or Map View
            if (isMapView) {
                TunisiaMapExplorer(
                    faculties = faculties,
                    onSelectFaculty = { viewModel.selectFaculty(it) }
                )
            } else {
                if (faculties.isEmpty()) {
                    // Empty Results State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aucune faculté trouvée",
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Essayez de modifier vos filtres ou termes de recherche.",
                                color = TextSecondaryDark,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.resetFilters() },
                                colors = ButtonDefaults.buttonColors(containerColor = TunisiaRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Afficher toutes les facultés", color = Color.White)
                            }
                        }
                    }
                } else {
                    // Faculties Cards List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${faculties.size} établissement(s) répertorié(s)",
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "🇹🇳 Géolocalisation active",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        items(faculties, key = { it.id }) { faculty ->
                            FacultyCard(
                                faculty = faculty,
                                onFacultyClick = { viewModel.selectFaculty(faculty) },
                                onToggleFavorite = { viewModel.toggleFavorite(faculty) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }

    // Overlays: Detail Dialog and Orientation AI Dialog
    selectedFaculty?.let { faculty ->
        FacultyDetailSheet(
            faculty = faculty,
            onDismiss = { viewModel.selectFaculty(null) },
            onToggleFavorite = { viewModel.toggleFavorite(faculty) },
            onSaveNotes = { notes -> viewModel.saveNotes(faculty.id, notes) }
        )
    }

    if (orientationState.showDialog) {
        OrientationAdvisorDialog(
            state = orientationState,
            onDismiss = { viewModel.closeOrientationDialog() },
            onUpdateForm = { branch, score, interests, gov ->
                viewModel.updateOrientationForm(branch, score, interests, gov)
            },
            onGenerate = { viewModel.generateOrientationAdvice() }
        )
    }
}
