package com.example.calanques.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.Activite
import com.example.calanques.model.ActivityType
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class ActivitesUiState {
    object Loading : ActivitesUiState()
    data class Success(
        val activites: List<Activite>,
        val activityTypes: List<ActivityType>,
        val typeSelectionne: ActivityType?
    ) : ActivitesUiState()
    data class Error(val message: String) : ActivitesUiState()
}

class ActivitesViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _toutesActivites = MutableStateFlow<List<Activite>>(emptyList())
    private val _activityTypes   = MutableStateFlow<List<ActivityType>>(emptyList())
    private val _typeSelectionne = MutableStateFlow<ActivityType?>(null)
    private val _isLoading       = MutableStateFlow(true)
    private val _error           = MutableStateFlow<String?>(null)

    // 🎯 Utilise bien le "d" minuscule partout
    private var pendingTypeId: Int? = savedStateHandle.get<Int>("type")

    private val _uiState = MutableStateFlow<ActivitesUiState>(ActivitesUiState.Loading)
    val uiState: StateFlow<ActivitesUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(_toutesActivites, _activityTypes, _typeSelectionne, _isLoading, _error)
            { activites, types, selection, loading, err ->
                when {
                    loading -> ActivitesUiState.Loading
                    err != null -> ActivitesUiState.Error(err)
                    else -> {
                        // Filtrage dynamique : si on a une catégorie, on filtre, sinon liste vide
                        val listeAafficher = if (selection != null) {
                            activites.filter { it.type == selection.id }
                        } else {
                            emptyList()
                        }

                        ActivitesUiState.Success(
                            activites = listeAafficher,
                            activityTypes = types,
                            typeSelectionne = selection
                        )
                    }
                }
            }.collect { _uiState.value = it }
        }
        chargerDonnees()
    }

    private fun chargerDonnees() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val repActivites = RetrofitClient.instance.getActivites()
                val repTypes = RetrofitClient.instance.getActivityTypes()

                if (repActivites.isSuccessful && repTypes.isSuccessful) {
                    val activitesRecues = repActivites.body() ?: emptyList()
                    val typesRecus = repTypes.body() ?: emptyList()

                    _activityTypes.value = typesRecus
                    _toutesActivites.value = activitesRecues

                    pendingTypeId?.let { id ->
                        val typeTrouve = typesRecus.find { it.id == id }
                        if (typeTrouve != null) {
                            _typeSelectionne.value = typeTrouve
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filtrerParIdDirect(id: Int) {
        pendingTypeId = id
        val typeTrouve = _activityTypes.value.find { it.id == id }
        if (typeTrouve != null) {
            _typeSelectionne.value = typeTrouve
        }
    }

    fun retry() { chargerDonnees() }

    fun selectActivityType(type: ActivityType) {
        filtrerParIdDirect(type.id)
    }

    fun selectActivityTypeById(id: Int) {
        val type = _activityTypes.value.find { it.id == id }
        if (type != null) {
            _typeSelectionne.value = type
        }
    }
}