package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.Activite
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivitesViewModel : ViewModel() {

    private val _activites = MutableStateFlow<List<Activite>>(emptyList())
    val activites: StateFlow<List<Activite>> = _activites

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun chargerActivites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getActivites()
                if (response.isSuccessful) {
                    _activites.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Erreur ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Impossible de joindre le serveur"
            } finally {
                _isLoading.value = false
            }
        }
    }
}