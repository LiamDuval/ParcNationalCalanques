package com.example.calanques.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.ReservationResponse
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Modèle pour les activités affichées
data class ActivityItem(val title: String, val date: String)

class ProfileViewModel(context: Context) : ViewModel() {
    // État de l'utilisateur
    var userName by mutableStateOf("")
    var userEmail by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Listes d'activités
    val upcomingActivities = mutableStateListOf<ActivityItem>()
    val pastActivities = mutableStateListOf<ActivityItem>()

    private val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val apiService = RetrofitClient.instance

    init {
        // Charger l'image sauvegardée localement
        val savedUri = sharedPrefs.getString("profile_image", null)
        if (savedUri != null) {
            try {
                profileImageUri = Uri.parse(savedUri)
            } catch (e: Exception) {
                profileImageUri = null
            }
        }
    }

    fun loadUserData(token: String) {
        val authHeader = "Bearer $token"
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // 1. Récupérer les infos du profil
                val profileResponse = apiService.getProfile(authHeader)
                if (profileResponse.isSuccessful) {
                    val user = profileResponse.body()
                    userName = "${user?.prenom ?: ""} ${user?.nom ?: ""}".trim().ifEmpty { user?.username ?: "Utilisateur" }
                    userEmail = user?.email ?: ""
                }

                // 2. Récupérer les réservations
                val reservationsResponse = apiService.getMesReservations(authHeader)
                if (reservationsResponse.isSuccessful) {
                    val reservations = reservationsResponse.body() ?: emptyList()
                    processReservations(reservations)
                } else if (reservationsResponse.code() == 404) {
                    // Si 404, on considère simplement qu'il n'y a pas de réservations
                    upcomingActivities.clear()
                    pastActivities.clear()
                } else {
                    // On ne bloque pas l'affichage du profil pour une erreur de liste de réservations
                    errorMessage = "Impossible de charger vos activités pour le moment"
                }
            } catch (e: Exception) {
                // Erreur silencieuse pour les réservations si le profil a pu être chargé
                if (userName.isEmpty()) {
                    errorMessage = "Erreur de connexion au serveur"
                    userName = "Profil indisponible"
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun processReservations(reservations: List<ReservationResponse>) {
        upcomingActivities.clear()
        pastActivities.clear()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply { 
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        reservations.forEach { res ->
            val date = try { sdf.parse(res.dateReservation) } catch (e: Exception) { null }
            val activityName = res.activite?.nom ?: "Activité #${res.activiteId}"
            val formattedDate = date?.let { displayFormat.format(it) } ?: res.dateReservation
            
            val item = ActivityItem(activityName, formattedDate)

            if (date != null && date.after(today)) {
                upcomingActivities.add(item)
            } else {
                pastActivities.add(item)
            }
        }
        
        // On trie les activités passées par date la plus récente en premier
        pastActivities.sortByDescending { it.date }
    }

    fun updateProfileImage(uri: Uri?) {
        profileImageUri = uri
        sharedPrefs.edit().putString("profile_image", uri.toString()).apply()
    }
}
