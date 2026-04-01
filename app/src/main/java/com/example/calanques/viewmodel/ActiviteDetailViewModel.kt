package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.Activite
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ActiviteDetailUiState {
    object Loading : ActiviteDetailUiState()
    data class Success(val activite: Activite) : ActiviteDetailUiState()
    data class Error(val message: String) : ActiviteDetailUiState()
}

class ActiviteDetailViewModel(private val activiteId: Int) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiviteDetailUiState>(ActiviteDetailUiState.Loading)
    val uiState: StateFlow<ActiviteDetailUiState> = _uiState

    init { charger() }

    private fun charger() {
        viewModelScope.launch {
            _uiState.value = ActiviteDetailUiState.Loading
            try {
                val response = RetrofitClient.instance.getActivite(activiteId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ActiviteDetailUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ActiviteDetailUiState.Error("Activité introuvable")
                }
            } catch (e: Exception) {
                _uiState.value = ActiviteDetailUiState.Error("Erreur : ${e.message}")
            }
        }
    }
}