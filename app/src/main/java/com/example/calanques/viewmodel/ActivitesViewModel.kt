package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.Activite
import com.example.calanques.model.ActivityType
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// ÉTATS POSSIBLES DE L'ÉCRAN ACTIVITÉS
// ─────────────────────────────────────────────
sealed class ActivitesUiState {
    object Loading : ActivitesUiState()
    data class Success(
        val activites: List<Activite>,         // liste filtrée à afficher
        val activityTypes: List<ActivityType>, // types pour les chips de filtre
        val typeSelectionne: ActivityType?     // type actif (null = tous)
    ) : ActivitesUiState()
    data class Error(val message: String) : ActivitesUiState()
}

// ─────────────────────────────────────────────
// VIEWMODEL — remplace l'ancien ActivitesViewModel
// À placer dans : viewmodel/ActivitesViewModel.kt
// ─────────────────────────────────────────────
class ActivitesViewModel : ViewModel() {

    // Données brutes (jamais filtrées directement)
    private val _toutesActivites = MutableStateFlow<List<Activite>>(emptyList())
    private val _activityTypes   = MutableStateFlow<List<ActivityType>>(emptyList())
    private val _typeSelectionne = MutableStateFlow<ActivityType?>(null)
    private val _isLoading       = MutableStateFlow(true)
    private val _error           = MutableStateFlow<String?>(null)

    // État exposé à l'écran
    private val _uiState = MutableStateFlow<ActivitesUiState>(ActivitesUiState.Loading)
    val uiState: StateFlow<ActivitesUiState> = _uiState

    init {
        // Recalcule l'état affiché à chaque changement de flux
        viewModelScope.launch {
            combine(
                _toutesActivites,
                _activityTypes,
                _typeSelectionne,
                _isLoading,
                _error
            ) { activites, types, typeSelectionne, loading, error ->
                when {
                    loading       -> ActivitesUiState.Loading
                    error != null -> ActivitesUiState.Error(error)
                    else -> {
                        // Dans le bloc combine :
                        val filtrees = if (typeSelectionne == null) {
                            activites
                        } else {
                            // On compare l'ID du type de l'activité avec l'ID du type sélectionné
                            activites.filter { it.type == typeSelectionne.id }
                        }
                        ActivitesUiState.Success(
                            activites       = filtrees.sortedBy { it.nom },
                            activityTypes   = types,
                            typeSelectionne = typeSelectionne
                        )
                    }
                }
            }.collect { _uiState.value = it }
        }

        chargerDonnees()
    }

    // ─────────────────────────────────────────
    // Chargement depuis l'API
    // ─────────────────────────────────────────
    private fun chargerDonnees() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = null
            try {
                // Lancement des deux appels
                val repActivites = RetrofitClient.instance.getActivites()
                val repTypes = RetrofitClient.instance.getActivityTypes()

                // Vérification de la réponse des Activités
                if (repActivites.isSuccessful) {
                    _toutesActivites.value = repActivites.body() ?: emptyList()
                } else {
                    _error.value = "Erreur activités (${repActivites.code()})"
                    return@launch
                }

                // Vérification de la réponse des Types
                if (repTypes.isSuccessful) {
                    _activityTypes.value = repTypes.body()?.sortedBy { it.libelle } ?: emptyList()
                } else {
                    _error.value = "Erreur types (${repTypes.code()})"
                }

            } catch (e: Exception) {
                _error.value = "Impossible de joindre le serveur : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ─────────────────────────────────────────
    // Actions exposées à l'écran
    // ─────────────────────────────────────────

    /** Sélectionne ou désélectionne un filtre (cliquer le même = annuler) */
    fun filtrerParType(type: ActivityType?) {
        _typeSelectionne.value =
            if (_typeSelectionne.value?.id == type?.id) null else type
    }

    /** Recharge les données */
    fun retry() { chargerDonnees() }

    // Ajoute ceci dans la section "Actions exposées à l'écran"
    fun selectActivityType(type: ActivityType) {
        filtrerParType(type)
    }
}