package com.example.calanques.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.calanques.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, token: String?) {
    val scrollState = rememberScrollState()

    // Charger les données quand l'écran s'affiche
    LaunchedEffect(token) {
        if (token != null) {
            viewModel.loadUserData(token)
        }
    }

    // Sélecteur d'image dans la galerie
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.updateProfileImage(uri) }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFE51A2E))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- SECTION PHOTO DE PROFIL ---
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = viewModel.profileImageUri ?: "https://via.placeholder.com/150",
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                FloatingActionButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.size(35.dp),
                    shape = CircleShape,
                    containerColor = Color.LightGray
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editer", modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Affichage du Prénom et Nom
            Text(text = viewModel.userName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = viewModel.userEmail, fontSize = 14.sp, color = Color.Gray)

            // On ne montre plus le bloc d'erreur ici à la demande de l'utilisateur

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            // --- SECTION ACTIVITÉ PRÉVUE ---
            SectionTitle("Activités prévues")
            if (viewModel.upcomingActivities.isEmpty()) {
                Text("Aucune activité prévue", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                viewModel.upcomingActivities.forEach { activity ->
                    ActivityCard(activity)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION DERNIÈRES ACTIVITÉS ---
            SectionTitle("Dernières activités faites")
            if (viewModel.pastActivities.isEmpty()) {
                Text("Aucun historique d'activité", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                viewModel.pastActivities.take(5).forEach { activity ->
                    ActivityCard(activity)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.DarkGray
    )
}

@Composable
fun ActivityCard(activity: com.example.calanques.viewmodel.ActivityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = activity.title, fontWeight = FontWeight.Bold)
                Text(text = activity.date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}