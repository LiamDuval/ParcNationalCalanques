package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.ActivityType
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

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
                val response: Response<List<ActivityType>> = RetrofitClient.instance.getActivityTypes()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val listeTriee = body.sortedBy { it.libelle }
                        _uiState.value = ActivityTypesUiState.Success(listeTriee)
                    } else {
                        _uiState.value = ActivityTypesUiState.Error("Le serveur a renvoyé une liste vide")
                    }
                } else {
                    _uiState.value = ActivityTypesUiState.Error("Erreur serveur : ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ActivityTypesUiState.Error("Connexion impossible : ${e.message}")
            }
        }
    }

    fun retry() { loadActivityTypes() }
}