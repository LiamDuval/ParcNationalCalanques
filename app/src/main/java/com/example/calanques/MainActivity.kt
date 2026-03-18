package com.example.calanques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.calanques.model.ActivityType
import com.example.calanques.ui.theme.CalanquesTheme
import com.example.calanques.viewmodel.ActivitesViewModel
import com.example.calanques.viewmodel.ActivityTypesUiState
import com.example.calanques.viewmodel.ActivityTypesViewModel
import com.example.calanques.viewmodel.HomeViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calanques.ui.screens.ActivitesScreen

val Red = Color(0xFFE51A2E)
val RedLight = Color(0xFFFFF0F1)
val TextColor = Color(0xFF111111)
val TextMuted = Color(0xFF888888)
val SurfaceColor = Color(0xFFFFFFFF)
val BorderColor = Color(0xFFE8E8E8)
val BgColor = Color(0xFFF5F5F5)

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val activityTypesViewModel: ActivityTypesViewModel by viewModels()
    private val activitesViewModel: ActivitesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalanquesTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            activityTypesViewModel = activityTypesViewModel,
                            onActivityTypeClick = { type ->
                                activitesViewModel.selectActivityType(type)
                                navController.navigate("activites")
                            }
                        )
                    }

                    composable("activites") {
                        ActivitesScreen(
                            viewModel = activitesViewModel,
                            onBackClick = { navController.popBackStack() },
                            onActiviteClick = { activite ->
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    activityTypesViewModel: ActivityTypesViewModel,
    onActivityTypeClick: (ActivityType) -> Unit
) {
    val uiState by activityTypesViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgColor,
        bottomBar = { BottomNavBar() },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            AppHeader()

            // Types section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Types d'activités",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                    Text(
                        text = "Tout voir",
                        fontSize = 12.sp,
                        color = Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (uiState) {
                    is ActivityTypesUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Red)
                        }
                    }
                    is ActivityTypesUiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = (uiState as ActivityTypesUiState.Error).message,
                                color = Red, fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { activityTypesViewModel.retry() },
                                colors = ButtonDefaults.buttonColors(containerColor = Red),
                                shape = RoundedCornerShape(50)
                            ) { Text("Réessayer") }
                        }
                    }
                    is ActivityTypesUiState.Success -> {
                        val types = (uiState as ActivityTypesUiState.Success).activityTypes
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            types.forEach { type ->
                                ActivityTypeCard(
                                    activityType = type,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onActivityTypeClick(type) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calanques",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(RedLight),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 16.sp)
        }
    }
}

@Composable
fun ActivityTypeCard(activityType: ActivityType, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val emoji = when {
        activityType.libelle.contains("Randon", ignoreCase = true) || activityType.libelle.contains("Balade", ignoreCase = true) -> "🥾"
        activityType.libelle.contains("Plong", ignoreCase = true) -> "🤿"
        activityType.libelle.contains("Kayak", ignoreCase = true) -> "🛶"
        activityType.libelle.contains("Bateau", ignoreCase = true) -> "⛵"
        activityType.libelle.contains("Escalade", ignoreCase = true) -> "🧗"
        activityType.libelle.contains("VTT", ignoreCase = true) -> "🚵"
        activityType.libelle.contains("Yoga", ignoreCase = true) -> "🧘"
        activityType.libelle.contains("Culture", ignoreCase = true) -> "🏛️"
        activityType.libelle.contains("H\u00e9lico", ignoreCase = true) || activityType.libelle.contains("Helico", ignoreCase = true) -> "🚁"
        activityType.libelle.contains("Accrob", ignoreCase = true) -> "🌳"
        else -> "🏃"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activityType.libelle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text("→", fontSize = 14.sp, color = Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeroStat(number: String, label: String) {
    Column {
        Text(number, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label.uppercase(), fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.5.sp)
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = SurfaceColor,
        tonalElevation = 0.dp,
        modifier = Modifier
            .height(72.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Text("🏠", fontSize = 20.sp) },
            label = { Text("Accueil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = Red,
                indicatorColor = RedLight
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("🔍", fontSize = 20.sp) },
            label = { Text("Activités", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedTextColor = Red, indicatorColor = RedLight)
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Red),
                    contentAlignment = Alignment.Center
                ) { Text("🛒", fontSize = 20.sp) }
            },
            label = {},
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("📅", fontSize = 20.sp) },
            label = { Text("Réservations", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedTextColor = Red, indicatorColor = RedLight)
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("👤", fontSize = 20.sp) },
            label = { Text("Profil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedTextColor = Red, indicatorColor = RedLight)
        )
    }
}