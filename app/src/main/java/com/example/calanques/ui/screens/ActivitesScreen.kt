package com.example.calanques.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.calanques.model.Activite
import com.example.calanques.model.ActivityType
import com.example.calanques.viewmodel.ActivitesUiState
import com.example.calanques.viewmodel.ActivitesViewModel

// ── Couleurs charte graphique Calanques ──────────────────────
private val Rouge     = Color(0xFFE51A2E)
private val Noir      = Color(0xFF000000)
private val GrisClair = Color(0xFFBBBBBB)
private val Gris      = Color(0xFF555555)
private val FondPage  = Color(0xFFF4F4F2)
private val BlancCard = Color(0xFFFFFFFF)

// ─────────────────────────────────────────────────────────────
// ÉCRAN 4.2 — LISTE DES ACTIVITÉS filtrées par type
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitesScreen(
    viewModel: ActivitesViewModel,
    typeInitial: ActivityType? = null,
    onActiviteClick: (Activite) -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(typeInitial) {
        viewModel.filtrerParType(typeInitial)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titre = when (val s = uiState) {
                        is ActivitesUiState.Success -> s.typeSelectionne?.libelle ?: "Toutes les activités"
                        else -> "Activités"
                    }

                    Text(
                        text       = titre,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint               = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Rouge)
            )
        },
        containerColor = FondPage
    ) { paddingValues ->

        when (val state = uiState) {

            // ── Chargement ───────────────────────────────────
            is ActivitesUiState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Rouge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Chargement des activités…", color = Gris, fontSize = 14.sp)
                    }
                }
            }

            // ── Erreur ───────────────────────────────────────
            is ActivitesUiState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier            = Modifier.padding(24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text       = "Impossible de charger les activités",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                            color      = Noir
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.message, color = Gris, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors  = ButtonDefaults.buttonColors(containerColor = Rouge),
                            shape   = RoundedCornerShape(50)
                        ) {
                            Text("Réessayer", color = Color.White)
                        }
                    }
                }
            }

            // ── Succès ───────────────────────────────────────
            is ActivitesUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    // Compteur
                    val nb = state.activites.size
                    Text(
                        text     = "$nb activité${if (nb > 1) "s" else ""}",
                        fontSize = 12.sp,
                        color    = Gris,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    if (state.activites.isEmpty()) {
                        // Aucun résultat
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text     = "Aucune activité dans cette catégorie",
                                    color    = Gris,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        // itemsIndexed évite toute ambiguïté avec items(Int)
                        LazyColumn(
                            contentPadding      = PaddingValues(
                                start  = 16.dp,
                                end    = 16.dp,
                                top    = 4.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = state.activites,
                                key   = { _, activite -> activite.id }
                            ) { _, activite ->
                                ActiviteCard(
                                    activite = activite,
                                    onClick  = { onActiviteClick(activite) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// CARTE D'UNE ACTIVITÉ
// Sans Coil : image remplacée par un bloc dégradé + emoji
// ─────────────────────────────────────────────
@Composable
private fun ActiviteCard(
    activite: Activite,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = BlancCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // ── Bloc image / placeholder ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                if (activite.image_url != null) {
                    AsyncImage(
                        model = "http://webngo.sio.bts:8004/${activite.image_url}",
                        contentDescription = activite.description,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                }
            }

            // ── Contenu texte ────────────────────────────
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text       = activite.nom,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Noir,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ligne durée / tarif
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Durée avec icône Material
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Utilisation d'une icône Material à la place de l'émoji
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Remplace par Icons.Default.Schedule si tu as l'import
                            contentDescription = null,
                            tint = GrisClair,
                            modifier = Modifier.height(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text     = "${activite.duree} h",
                            fontSize = 12.sp,
                            color    = Gris
                        )
                    }

                    // Tarif
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Rouge.copy(alpha = 0.10f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("€", fontSize = 13.sp, color = Rouge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text       = String.format("%.2f", activite.tarif),
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Rouge
                            )
                        }
                    }
                }
            }
        }
    }
}

