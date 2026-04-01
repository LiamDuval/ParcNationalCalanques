package com.example.calanques.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.calanques.BgColor
import com.example.calanques.Red
import com.example.calanques.SurfaceColor
import com.example.calanques.TextColor
import com.example.calanques.TextMuted
import com.example.calanques.network.ApiConfig.BASE_URL
import com.example.calanques.viewmodel.ActiviteDetailUiState
import com.example.calanques.viewmodel.ActiviteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiviteDetailScreen(
    viewModel: ActiviteDetailViewModel,
    padding: PaddingValues,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail activité") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceColor,
                    titleContentColor = TextColor,
                    navigationIconContentColor = Red
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgColor)
        ) {
            when (uiState) {
                is ActiviteDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Red
                    )
                }
                is ActiviteDetailUiState.Error -> {
                    Text(
                        text = (uiState as ActiviteDetailUiState.Error).message,
                        color = Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is ActiviteDetailUiState.Success -> {
                    val activite = (uiState as ActiviteDetailUiState.Success).activite
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = "${BASE_URL}${activite.image_url}",
                            contentDescription = activite.nom,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = activite.nom,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                activite.tarif?.let {
                                    InfoChip(label = "%.2f €".format(it))
                                }
                                activite.duree?.let {
                                    InfoChip(label = it)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            activite.description?.let {
                                Text(
                                    text = it,
                                    fontSize = 15.sp,
                                    color = TextMuted,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { /* TODO: réservation */ },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Red)
                            ) {
                                Text("Réserver", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(com.example.calanques.RedLight)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = Red, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}