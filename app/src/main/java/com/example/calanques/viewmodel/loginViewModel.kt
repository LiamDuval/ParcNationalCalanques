package com.example.calanques.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    //
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    
    // Ajout du token pour suivre l'état de connexion
    var token by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val apiService = RetrofitClient.instance

    fun onLoginClick(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Veuillez remplir tous les champs"
            return
        }

        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            try {
                val cleanUsername = username.trim().lowercase()

                val response = try {
                    apiService.login(cleanUsername, password)
                } catch (e: retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    isLoading = false
                    errorMessage = parseErrorMessage(errorBody)
                    return@launch
                }

                isLoading = false

                if (response.token != null) {
                    this@LoginViewModel.token = response.token // Sauvegarde du token
                    onSuccess()
                } else {
                    errorMessage = response.message ?: "Identifiants incorrects ou problème de compte"
                }

            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Erreur de connexion : ${e.message}"
            }
        }
    }

    private fun parseErrorMessage(errorJson: String?): String {
        if (errorJson == null) return "Erreur inconnue"
        return try {
            val json = JSONObject(errorJson)
            json.optString("message", json.optString("detail", "Données invalides (422)"))
        } catch (e: Exception) {
            "Erreur 422 : $errorJson"
        }
    }
    
    fun logout() {
        token = null
        username = ""
        password = ""
    }
}
