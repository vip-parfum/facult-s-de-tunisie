package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.CognitiveSchedulerEngine
import com.example.data.ai.GeminiApiClient
import com.example.data.ai.ScheduleOptimizationResult
import com.example.data.local.AppDatabase
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import com.example.data.repository.CognitiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val selectedDayOffset: Int = 0,
    val tasks: List<TaskItem> = emptyList(),
    val profile: UserCognitiveProfile = UserCognitiveProfile(),
    val averageAlignment: Int = 0,
    val optimizationProposal: ScheduleOptimizationResult? = null,
    val isOptimizing: Boolean = false,
    val aiAdviceText: String = "",
    val isLoadingAiAdvice: Boolean = false,
    val activeFocusTask: TaskItem? = null,
    val showCalibrationDialog: Boolean = false,
    val showAddTaskDialog: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CognitiveRepository

    private val _selectedDayOffset = MutableStateFlow(0)
    val selectedDayOffset: StateFlow<Int> = _selectedDayOffset.asStateFlow()

    private val _optimizationProposal = MutableStateFlow<ScheduleOptimizationResult?>(null)
    val optimizationProposal: StateFlow<ScheduleOptimizationResult?> = _optimizationProposal.asStateFlow()

    private val _aiAdviceText = MutableStateFlow("")
    val aiAdviceText: StateFlow<String> = _aiAdviceText.asStateFlow()

    private val _isLoadingAiAdvice = MutableStateFlow(false)
    val isLoadingAiAdvice: StateFlow<Boolean> = _isLoadingAiAdvice.asStateFlow()

    private val _activeFocusTask = MutableStateFlow<TaskItem?>(null)
    val activeFocusTask: StateFlow<TaskItem?> = _activeFocusTask.asStateFlow()

    private val _showCalibrationDialog = MutableStateFlow(false)
    val showCalibrationDialog: StateFlow<Boolean> = _showCalibrationDialog.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    val profileState: StateFlow<UserCognitiveProfile>
    val currentDayTasksState: StateFlow<List<TaskItem>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CognitiveRepository(db)

        profileState = repository.profileFlow
            .combine(MutableStateFlow(Unit)) { prof, _ -> prof ?: UserCognitiveProfile() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserCognitiveProfile()
            )

        currentDayTasksState = _selectedDayOffset
            .combine(repository.profileFlow) { offset, _ -> offset }
            .combine(repository.getTasksForDay(0)) { _, tasks -> tasks }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        viewModelScope.launch {
            repository.getProfileSync() // Ensures profile and seed tasks exist
            fetchAiAdvice()
        }
    }

    fun selectDay(offset: Int) {
        _selectedDayOffset.value = offset
    }

    fun getTasksForSelectedDay(): StateFlow<List<TaskItem>> {
        return repository.getTasksForDay(_selectedDayOffset.value)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun openAddTaskDialog() {
        _showAddTaskDialog.value = true
    }

    fun closeAddTaskDialog() {
        _showAddTaskDialog.value = false
    }

    fun addTask(task: TaskItem) {
        viewModelScope.launch {
            repository.addTask(task.copy(dayOffset = _selectedDayOffset.value))
            _showAddTaskDialog.value = false
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun startFocusSession(task: TaskItem) {
        _activeFocusTask.value = task
    }

    fun dismissFocusSession() {
        _activeFocusTask.value = null
    }

    fun finishFocusSessionAndLearn(rating: Int, fatigue: Int) {
        val task = _activeFocusTask.value ?: return
        viewModelScope.launch {
            repository.recordFocusFeedback(task.id, rating, fatigue)
            _activeFocusTask.value = null
        }
    }

    fun openCalibrationDialog() {
        _showCalibrationDialog.value = true
    }

    fun closeCalibrationDialog() {
        _showCalibrationDialog.value = false
    }

    fun saveCalibratedProfile(profile: UserCognitiveProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            _showCalibrationDialog.value = false
        }
    }

    fun runScheduleOptimization(currentTasks: List<TaskItem>, profile: UserCognitiveProfile) {
        val result = CognitiveSchedulerEngine.optimizeSchedule(currentTasks, profile)
        _optimizationProposal.value = result
    }

    fun dismissOptimizationProposal() {
        _optimizationProposal.value = null
    }

    fun applyOptimizationProposal() {
        val proposal = _optimizationProposal.value ?: return
        viewModelScope.launch {
            repository.replaceTasksForDay(_selectedDayOffset.value, proposal.optimizedTasks)
            _optimizationProposal.value = null
        }
    }

    fun fetchAiAdvice() {
        viewModelScope.launch {
            _isLoadingAiAdvice.value = true
            val profile = profileState.value
            val tasks = repository.getTasksForDay(_selectedDayOffset.value).firstOrNull() ?: emptyList()
            val advice = GeminiApiClient.getCognitiveCoachingAdvice(profile, tasks)
            _aiAdviceText.value = advice
            _isLoadingAiAdvice.value = false
        }
    }
}
