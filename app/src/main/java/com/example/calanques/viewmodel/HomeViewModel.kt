package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calanques.model.WelcomeMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val _message = MutableStateFlow(
        WelcomeMessage(
            titre = "Parc National des Calanques",
            sousTitre = "Découvrez nos activités",
            emoji = "🏔️"
        )
    )
    val message: StateFlow<WelcomeMessage> = _message
}