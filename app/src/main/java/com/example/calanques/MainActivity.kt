package com.example.calanques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.calanques.model.ActivityType
import com.example.calanques.network.ApiConfig.BASE_URL
import com.example.calanques.ui.screens.ActivitesScreen
import com.example.calanques.ui.theme.CalanquesTheme
import com.example.calanques.viewmodel.ActivitesViewModel
import com.example.calanques.viewmodel.ActivityTypesUiState
import com.example.calanques.viewmodel.ActivityTypesViewModel
import com.example.calanques.viewmodel.HomeViewModel

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    activityTypesViewModel: ActivityTypesViewModel,
    onActivityTypeClick: (ActivityType) -> Unit
) {
    val uiState by activityTypesViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Déterminer le nombre de colonnes en fonction de la largeur (ex: > 600dp = tablette/paysage)
    val columns = if (configuration.screenWidthDp > 600) 2 else 1

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

            // Logo adaptatif (max 200dp ou 30% de la hauteur de l'écran)
            val logoHeight = minOf(200.dp, (configuration.screenHeightDp * 0.25f).dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Calanques",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(logoHeight)
                    .padding(vertical = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Types section
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Types d'activités",
                        fontSize = if (screenWidth > 600.dp) 24.sp else 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                    Text(
                        text = "Tout voir",
                        fontSize = 14.sp,
                        color = Red,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { /* Action */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        
                        // Utilisation de FlowRow pour un rendu en grille adaptatif sans imbriquer de scroll
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            maxItemsInEachRow = columns
                        ) {
                            val itemWidth = if (columns > 1) (screenWidth - 44.dp) / 2 else screenWidth - 32.dp
                            types.forEach { type ->
                                ActivityTypeCard(
                                    activityType = type,
                                    modifier = Modifier.width(itemWidth),
                                    onClick = { onActivityTypeClick(type) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
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
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calanques",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Red
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RedLight)
                .clickable { /* Profile action */ },
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 18.sp)
        }
    }
}

@Composable
fun ActivityTypeCard(activityType: ActivityType, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(200.dp) // Hauteur fixe pour permettre la répartition proportionnelle
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Zone Image : 60%
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                if (!activityType.image_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = "${BASE_URL}${activityType.image_url}",
                        contentDescription = activityType.libelle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image non disponible", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            // Zone Texte : 40%
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = activityType.libelle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp).padding(start = 4.dp),
                        tint = Red
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = SurfaceColor,
        tonalElevation = 8.dp,
        // On retire la hauteur fixe qui coupait les têtes et on ajoute le padding système
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf("Accueil" to "🏠", "Activités" to "🔍", "Panier" to "🛒", "Résas" to "📅", "Profil" to "👤")
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == 0,
                onClick = {},
                icon = {
                    if (item.first == "Panier") {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Red),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.second, fontSize = 20.sp)
                        }
                    } else {
                        // Un petit padding vertical sur l'emoji aide à éviter qu'il soit rogné
                        Text(item.second, fontSize = 24.sp, modifier = Modifier.padding(bottom = 2.dp))
                    }
                },
                label = {
                    if (item.first != "Panier") {
                        Text(item.first, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Red,
                    selectedTextColor = Red,
                    unselectedTextColor = TextMuted,
                    indicatorColor = RedLight.copy(alpha = 0.3f)
                )
            )
        }
    }
}