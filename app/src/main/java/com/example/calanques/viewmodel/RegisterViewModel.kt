package com.example.calanques.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.model.RegisterRequest
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RegisterViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance

    // Champs de base
    var email by mutableStateOf("") 
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    
    // Informations personnelles
    var nom by mutableStateOf("")
    var prenom by mutableStateOf("")
    var birthDate by mutableStateOf("") 
    var gender by mutableStateOf(true) // true = H, false = F
    var familyCount by mutableStateOf("1")
    
    // Coordonnées
    var telephone by mutableStateOf("")
    var adresse by mutableStateOf("")
    var cp by mutableStateOf("")
    var ville by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || nom.isBlank() || prenom.isBlank() || telephone.isBlank() || birthDate.isBlank() || ville.isBlank() || cp.isBlank() || adresse.isBlank()) {
            errorMessage = "Veuillez remplir tous les champs obligatoires"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Les mots de passe ne correspondent pas"
            return
        }

        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            try {
                // Conversion Date FR -> API (ISO yyyy-MM-dd)
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateIso = try {
                    val date = inputFormat.parse(birthDate)
                    outputFormat.format(date!!)
                } catch (e: Exception) {
                    birthDate
                }

                // Création de la requête. 
                // IMPORTANT : On n'applique PLUS de lowercase() systématique sur le mot de passe
                // car cela peut causer des erreurs d'identification si l'utilisateur en a mis un.
                // On nettoie juste l'email qui sert d'identifiant.
                val request = RegisterRequest(
                    username = email.trim().lowercase(),
                    password = password,
                    email = email.trim().lowercase(),
                    nom = nom,
                    prenom = prenom,
                    adresse = adresse,
                    cp = cp,
                    ville = ville,
                    telephone = telephone,
                    roleId = 2,            // Rôle Client
                    gender = if (gender) 1 else 2,
                    birthDate = dateIso,
                    familyCount = familyCount.toIntOrNull() ?: 1,
                    fullName = "$prenom $nom"
                )

                val response = apiService.register(request)

                if (response.isSuccessful) {
                    isLoading = false
                    onSuccess()
                } else {
                    isLoading = false
                    val rawError = response.errorBody()?.string() ?: "Erreur inconnue"
                    errorMessage = "Erreur ${response.code()} : $rawError"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Erreur réseau : ${e.message}"
            }
        }
    }
}
