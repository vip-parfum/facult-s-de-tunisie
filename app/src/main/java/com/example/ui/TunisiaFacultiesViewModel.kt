package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiApiClient
import com.example.data.local.AppDatabase
import com.example.data.model.Faculty
import com.example.data.repository.FacultyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrientationAdvisorState(
    val bacBranch: String = "Mathématiques",
    val score: String = "165.50",
    val interests: String = "Informatique, Intelligence Artificielle & Génie Logiciel",
    val preferredGovernorate: String = "Tunis",
    val adviceText: String = "",
    val isLoading: Boolean = false,
    val showDialog: Boolean = false
)

class TunisiaFacultiesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FacultyRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FacultyRepository(db.facultyDao())
    }

    val allFaculties: StateFlow<List<Faculty>> = repository.allFaculties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedUniversity = MutableStateFlow("Toutes les Universités")
    val selectedUniversity: StateFlow<String> = _selectedUniversity.asStateFlow()

    private val _selectedGovernorate = MutableStateFlow("Tous les Gouvernorats")
    val selectedGovernorate: StateFlow<String> = _selectedGovernorate.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Toutes les Catégories")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _selectedFaculty = MutableStateFlow<Faculty?>(null)
    val selectedFaculty: StateFlow<Faculty?> = _selectedFaculty.asStateFlow()

    private val _isMapView = MutableStateFlow(false)
    val isMapView: StateFlow<Boolean> = _isMapView.asStateFlow()

    private val _orientationState = MutableStateFlow(OrientationAdvisorState())
    val orientationState: StateFlow<OrientationAdvisorState> = _orientationState.asStateFlow()

    private data class FilterParams(
        val query: String,
        val university: String,
        val governorate: String,
        val category: String,
        val favoritesOnly: Boolean
    )

    // Filtered list
    val filteredFaculties: StateFlow<List<Faculty>> = combine(
        allFaculties,
        combine(
            _searchQuery,
            _selectedUniversity,
            _selectedGovernorate,
            _selectedCategory,
            _onlyFavorites
        ) { query, university, governorate, category, favoritesOnly ->
            FilterParams(query, university, governorate, category, favoritesOnly)
        }
    ) { faculties: List<Faculty>, filters: FilterParams ->
        faculties.filter { faculty ->
            val matchesQuery = filters.query.isBlank() ||
                    faculty.name.contains(filters.query, ignoreCase = true) ||
                    faculty.arabicName.contains(filters.query, ignoreCase = true) ||
                    faculty.shortCode.contains(filters.query, ignoreCase = true) ||
                    faculty.university.contains(filters.query, ignoreCase = true) ||
                    faculty.governorate.contains(filters.query, ignoreCase = true) ||
                    faculty.category.contains(filters.query, ignoreCase = true) ||
                    faculty.degrees.contains(filters.query, ignoreCase = true) ||
                    faculty.address.contains(filters.query, ignoreCase = true)

            val matchesUniv = filters.university == "Toutes les Universités" || faculty.university == filters.university
            val matchesGov = filters.governorate == "Tous les Gouvernorats" || faculty.governorate.equals(filters.governorate, ignoreCase = true)
            val matchesCat = filters.category == "Toutes les Catégories" || faculty.category == filters.category
            val matchesFav = !filters.favoritesOnly || faculty.isFavorite

            matchesQuery && matchesUniv && matchesGov && matchesCat && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectUniversity(university: String) {
        _selectedUniversity.value = university
    }

    fun selectGovernorate(gov: String) {
        _selectedGovernorate.value = gov
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun toggleFavoritesOnly() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun toggleFavorite(faculty: Faculty) {
        viewModelScope.launch {
            repository.toggleFavorite(faculty.id, faculty.isFavorite)
            if (_selectedFaculty.value?.id == faculty.id) {
                _selectedFaculty.value = _selectedFaculty.value?.copy(isFavorite = !faculty.isFavorite)
            }
        }
    }

    fun saveNotes(facultyId: String, notes: String) {
        viewModelScope.launch {
            repository.saveNotes(facultyId, notes)
            if (_selectedFaculty.value?.id == facultyId) {
                _selectedFaculty.value = _selectedFaculty.value?.copy(notes = notes)
            }
        }
    }

    fun selectFaculty(faculty: Faculty?) {
        _selectedFaculty.value = faculty
    }

    fun toggleMapView(showMap: Boolean) {
        _isMapView.value = showMap
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedUniversity.value = "Toutes les Universités"
        _selectedGovernorate.value = "Tous les Gouvernorats"
        _selectedCategory.value = "Toutes les Catégories"
        _onlyFavorites.value = false
    }

    // Orientation AI
    fun openOrientationDialog() {
        _orientationState.value = _orientationState.value.copy(showDialog = true)
    }

    fun closeOrientationDialog() {
        _orientationState.value = _orientationState.value.copy(showDialog = false)
    }

    fun updateOrientationForm(
        branch: String = _orientationState.value.bacBranch,
        score: String = _orientationState.value.score,
        interests: String = _orientationState.value.interests,
        gov: String = _orientationState.value.preferredGovernorate
    ) {
        _orientationState.value = _orientationState.value.copy(
            bacBranch = branch,
            score = score,
            interests = interests,
            preferredGovernorate = gov
        )
    }

    fun generateOrientationAdvice() {
        val current = _orientationState.value
        _orientationState.value = current.copy(isLoading = true, adviceText = "")

        viewModelScope.launch {
            val result = GeminiApiClient.getTunisiaOrientationAdvice(
                bacBranch = current.bacBranch,
                score = current.score,
                targetInterests = current.interests,
                preferredGovernorate = current.preferredGovernorate
            )
            _orientationState.value = _orientationState.value.copy(
                isLoading = false,
                adviceText = result
            )
        }
    }
}
