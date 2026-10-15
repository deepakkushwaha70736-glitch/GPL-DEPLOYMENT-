package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.PoliceDatabase
import com.example.data.local.RecentSearchEntity
import com.example.data.model.DirectoryContact
import com.example.data.model.FilterCategory
import com.example.data.model.GplDeployment
import com.example.data.model.RadioCodeItem
import com.example.data.model.SearchMode
import com.example.data.repository.PoliceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val searchQuery: String = "",
    val searchMode: SearchMode = SearchMode.SET,
    val selectedCategory: FilterCategory = FilterCategory.ALL,
    val searchResults: List<GplDeployment> = emptyList(),
    val totalDeployments: Int = 0,
    val totalEquipmentCodes: Int = 0,
    val isLoading: Boolean = true,
    val activeTab: Int = 0, // 0 = Lookup, 1 = Directory, 2 = Bookmarks, 3 = Radio Guide
    val selectedDeploymentForDetail: GplDeployment? = null,
    val noteDialogItem: BookmarkEntity? = null,
    val snackbarMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PoliceDatabase.getDatabase(application)
    private val repository = PoliceRepository(application, database.policeDao())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<RecentSearchEntity>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val directoryContacts: List<DirectoryContact> by lazy {
        repository.getDirectoryContacts()
    }

    val radioGuideItems: List<RadioCodeItem> by lazy {
        repository.getRadioGuideItems()
    }

    init {
        viewModelScope.launch {
            repository.initialize()
            val total = repository.getTotalEntriesCount()
            val codes = repository.getTotalUniqueCodesCount()
            _uiState.value = _uiState.value.copy(
                totalDeployments = total,
                totalEquipmentCodes = codes,
                isLoading = false
            )
            executeSearch()
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
        executeSearch()
    }

    fun onModeChanged(newMode: SearchMode) {
        _uiState.value = _uiState.value.copy(searchMode = newMode)
        executeSearch()
    }

    fun onCategoryChanged(newCategory: FilterCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        executeSearch()
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        executeSearch()
    }

    fun onRecentSearchClicked(recent: RecentSearchEntity) {
        val mode = try {
            SearchMode.valueOf(recent.mode)
        } catch (e: Exception) {
            SearchMode.ALL
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = recent.query,
            searchMode = mode
        )
        executeSearch()
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    fun onSelectDeployment(deployment: GplDeployment?) {
        _uiState.value = _uiState.value.copy(selectedDeploymentForDetail = deployment)
    }

    fun toggleBookmark(deployment: GplDeployment) {
        viewModelScope.launch {
            repository.toggleBookmark(deployment)
            showSnackbar("Saved bookmark updated")
        }
    }

    fun openNoteDialog(bookmark: BookmarkEntity) {
        _uiState.value = _uiState.value.copy(noteDialogItem = bookmark)
    }

    fun closeNoteDialog() {
        _uiState.value = _uiState.value.copy(noteDialogItem = null)
    }

    fun saveNote(key: String, note: String) {
        viewModelScope.launch {
            repository.updateBookmarkNote(key, note)
            closeNoteDialog()
            showSnackbar("Note saved successfully")
        }
    }

    fun deleteBookmark(key: String) {
        viewModelScope.launch {
            repository.removeBookmark(key)
            showSnackbar("Bookmark removed")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    private fun executeSearch() {
        val query = _uiState.value.searchQuery
        val mode = _uiState.value.searchMode
        val category = _uiState.value.selectedCategory

        val results = repository.searchDeployments(query, mode, category)
        _uiState.value = _uiState.value.copy(searchResults = results)

        if (query.trim().length >= 2) {
            viewModelScope.launch {
                repository.saveRecentSearch(query, mode)
            }
        }
    }
}
