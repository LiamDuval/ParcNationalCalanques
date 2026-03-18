package com.example.calanques.ui.screens

// --- IMPORTS ---
// Compose de base
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Composants Material3 (TopAppBar, Card, Button, etc.)
import androidx.compose.material3.*
// Pour observer le StateFlow du ViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// Mise en page et style
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Nos propres classes
import com.example.calanques.model.ActivityType
import com.example.calanques.viewmodel.ActivityTypesUiState
import com.example.calanques.viewmodel.ActivityTypesViewModel

// ─────────────────────────────────────────────
// ÉCRAN PRINCIPAL
// Reçoit le ViewModel (données) et un callback de navigation
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ActivityTypesViewModel,        // source des données
    onActivityTypeClick: (ActivityType) -> Unit // navigation vers écran 4.2
) {
    // On écoute le StateFlow — Compose redessine automatiquement si ça change
    val uiState by viewModel.uiState.collectAsState()

    // Scaffold = squelette de l'écran (gère TopAppBar + fond + marges)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Parc National des Calanques",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE51A2E) // rouge
                )
            )
        },
        containerColor = Color(0xFFF5F5F5) // fond gris clair
    ) { paddingValues ->

        // On switch selon l'état du ViewModel
        when (val state = uiState) {

            // ── ÉTAT 1 : chargement en cours ──
            is ActivityTypesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Cercle de chargement rouge
                        CircularProgressIndicator(color = Color(0xFFE51A2E))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chargement…",
                            color = Color(0xFF666666),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── ÉTAT 2 : erreur API ──
            is ActivityTypesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(text = "😕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Impossible de charger les activités",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Affiche le message d'erreur technique
                        Text(
                            text = state.message,
                            color = Color(0xFF666666),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        // Bouton qui relance l'appel API
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE51A2E)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Réessayer", color = Color.White)
                        }
                    }
                }
            }

            // ── ÉTAT 3 : données reçues → on affiche la liste ──
            is ActivityTypesUiState.Success -> {
                LazyColumn(
                    // LazyColumn = liste optimisée, ne crée que les éléments visibles
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = 16.dp, bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp), // espace entre cartes
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // respecte l'espace de la TopAppBar
                ) {
                    // Pour chaque type d'activité, on crée une carte
                    items(state.activityTypes, key = { it.id }) { activityType ->
                        ActivityTypeCard(
                            activityType = activityType,
                            onClick = { onActivityTypeClick(activityType) }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// CARTE D'UN TYPE D'ACTIVITÉ
// Composant réutilisable, appelé pour chaque élément de la liste
// ─────────────────────────────────────────────
@Composable
private fun ActivityTypeCard(
    activityType: ActivityType,  // les données à afficher
    onClick: () -> Unit          // ce qui se passe quand on clique
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()      // prend toute la largeur
            .clickable { onClick() }, // rend la carte cliquable
        shape = RoundedCornerShape(12.dp), // coins arrondis
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // ombre légère
    ) {
        Row(
            // Row = disposition horizontale (emoji | texte | chevron)
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp)) // espace entre emoji et texte

            // Nom du type — weight(1f) = prend tout l'espace restant
            Text(
                text = activityType.libelle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )

            // Chevron à droite pour indiquer que c'est cliquable
            Text(
                text = "›",
                fontSize = 24.sp,
                color = Color(0xFFE51A2E),
                fontWeight = FontWeight.Bold
            )
        }
    }
}