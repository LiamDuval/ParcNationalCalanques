package com.example.calanques.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.calanques.AppHeader
import com.example.calanques.BottomNavBar
import com.example.calanques.ui.theme.CalanquesBg

@Composable
    fun CalanquesScaffold(
        navController: NavController,
        isLoggedIn: Boolean,
        showBottomBar: Boolean = true,
        content: @Composable (PaddingValues) -> Unit
    ) {
        Scaffold(
            containerColor = CalanquesBg,
            topBar = {
                // Ton header avec le petit logo "Calanques" en haut à gauche
                AppHeader(onProfileClick = { /* Action globale */ })
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(navController = navController, isLoggedIn = isLoggedIn)
                }
            },
            content = content
        )
    }