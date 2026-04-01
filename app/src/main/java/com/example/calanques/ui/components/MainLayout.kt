package com.example.calanques.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.calanques.AppHeader
import com.example.calanques.BottomNavBar
import com.example.calanques.BgColor

@Composable
fun MainScreenLayout(
    navController: NavController,
    isLoggedIn: Boolean,
    onProfileClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = BgColor, // Ton gris F5F5F5
        topBar = {
            AppHeader(onProfileClick = onProfileClick)
        },
        bottomBar = {
            BottomNavBar(navController = navController, isLoggedIn = isLoggedIn)
        }
    ) { innerPadding ->
        // C'est ici que l'écran spécifique (Home, Activités, etc.) s'affichera
        content(innerPadding)
    }
}