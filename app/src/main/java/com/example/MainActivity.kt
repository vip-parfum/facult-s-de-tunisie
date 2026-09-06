package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.TunisiaFacultiesViewModel
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.CognitiveCalibrationDialog
import com.example.ui.components.FocusSessionDialog
import com.example.ui.components.OptimizationResultDialog
import com.example.ui.components.TunisiaMapExplorer
import com.example.ui.screens.FacultiesMainScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TunisiaGold
import com.example.ui.theme.TunisiaRed

class MainActivity : ComponentActivity() {
    private val facultiesViewModel: TunisiaFacultiesViewModel by viewModels()
    private val scheduleViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(
                    facultiesViewModel = facultiesViewModel,
                    scheduleViewModel = scheduleViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    facultiesViewModel: TunisiaFacultiesViewModel,
    scheduleViewModel: MainViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Faculties State
    val allFaculties by facultiesViewModel.allFaculties.collectAsState()

    // Schedule State
    val profile by scheduleViewModel.profileState.collectAsState()
    val tasks by scheduleViewModel.getTasksForSelectedDay().collectAsState()
    val selectedDayOffset by scheduleViewModel.selectedDayOffset.collectAsState()
    val showAddTask by scheduleViewModel.showAddTaskDialog.collectAsState()
    val showCalibration by scheduleViewModel.showCalibrationDialog.collectAsState()
    val activeFocusTask by scheduleViewModel.activeFocusTask.collectAsState()
    val optimizationProposal by scheduleViewModel.optimizationProposal.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MidnightNavy,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Facultés (Directory & Search)
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        facultiesViewModel.toggleMapView(false)
                    },
                    icon = { Icon(Icons.Default.School, contentDescription = "Facultés de Tunisie") },
                    label = { Text("Facultés", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = TunisiaRed,
                        indicatorColor = TunisiaRed,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_faculties_tab")
                )

                // Tab 1: Google Maps / Cartographie
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Localisation Google Maps") },
                    label = { Text("Carte Maps", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF38BDF8),
                        indicatorColor = Color(0xFF0284C7),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_map_tab")
                )

                // Tab 2: Orientation & IA Gemini
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        facultiesViewModel.openOrientationDialog()
                    },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Orientation IA") },
                    label = { Text("Orientation IA", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = TunisiaGold,
                        indicatorColor = TunisiaGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_ai_tab")
                )

                // Tab 3: Emploi du temps (Schedule)
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Emploi du temps") },
                    label = { Text("Planning", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF090D16),
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_schedule_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    FacultiesMainScreen(viewModel = facultiesViewModel)
                }
                1 -> {
                    TunisiaMapExplorer(
                        faculties = allFaculties,
                        onSelectFaculty = { facultiesViewModel.selectFaculty(it) }
                    )
                }
                2 -> {
                    FacultiesMainScreen(viewModel = facultiesViewModel)
                }
                3 -> {
                    ScheduleScreen(
                        tasks = tasks,
                        profile = profile,
                        selectedDayOffset = selectedDayOffset,
                        onSelectDay = { scheduleViewModel.selectDay(it) },
                        onAddTask = { scheduleViewModel.openAddTaskDialog() },
                        onStartFocus = { scheduleViewModel.startFocusSession(it) },
                        onToggleTask = { scheduleViewModel.toggleTaskCompletion(it) },
                        onDeleteTask = { scheduleViewModel.deleteTask(it) },
                        onOptimizeSchedule = { scheduleViewModel.runScheduleOptimization(tasks, profile) }
                    )
                }
            }
        }
    }

    // Schedule Dialogs
    if (showAddTask) {
        AddTaskDialog(
            profile = profile,
            dayOffset = selectedDayOffset,
            onDismiss = { scheduleViewModel.closeAddTaskDialog() },
            onConfirm = { scheduleViewModel.addTask(it) }
        )
    }

    activeFocusTask?.let { task ->
        FocusSessionDialog(
            task = task,
            onDismiss = { scheduleViewModel.dismissFocusSession() },
            onFinishAndLearn = { rating, fatigue ->
                scheduleViewModel.finishFocusSessionAndLearn(rating, fatigue)
            }
        )
    }

    if (showCalibration) {
        CognitiveCalibrationDialog(
            currentProfile = profile,
            onDismiss = { scheduleViewModel.closeCalibrationDialog() },
            onSaveProfile = { scheduleViewModel.saveCalibratedProfile(it) }
        )
    }

    optimizationProposal?.let { proposal ->
        OptimizationResultDialog(
            result = proposal,
            onDismiss = { scheduleViewModel.dismissOptimizationProposal() },
            onApply = { scheduleViewModel.applyOptimizationProposal() }
        )
    }
}
