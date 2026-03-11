package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.ActivityType
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ActivityTypesUiState {
    object Loading : ActivityTypesUiState()
    data class Success(val activityTypes: List<ActivityType>) : ActivityTypesUiState()
    data class Error(val message: String) : ActivityTypesUiState()
}

class ActivityTypesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityTypesUiState>(ActivityTypesUiState.Loading)
    val uiState: StateFlow<ActivityTypesUiState> = _uiState

    init { loadActivityTypes() }

    private fun loadActivityTypes() {
        viewModelScope.launch {
            _uiState.value = ActivityTypesUiState.Loading
            try {
                val types = RetrofitClient.instance.getActivityTypes()
                _uiState.value = ActivityTypesUiState.Success(types.sortedBy { it.libelle })
            } catch (e: Exception) {
                _uiState.value = ActivityTypesUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun retry() { loadActivityTypes() }
}