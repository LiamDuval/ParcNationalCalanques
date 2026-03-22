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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.calanques.model.ActivityType
import com.example.calanques.network.ApiConfig.BASE_URL
import com.example.calanques.ui.screens.ActivitesScreen
import com.example.calanques.ui.screens.LoginScreen
import com.example.calanques.ui.screens.RegisterScreen
import com.example.calanques.ui.screens.ProfileScreen
import com.example.calanques.ui.theme.CalanquesTheme
import com.example.calanques.viewmodel.*

val Red = Color(0xFFE51A2E)
val RedLight = Color(0xFFFFF0F1)
val TextColor = Color(0xFF111111)
val TextMuted = Color(0xFF888888)
val SurfaceColor = Color(0xFFFFFFFF)
val BgColor = Color(0xFFF5F5F5)

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val activityTypesViewModel: ActivityTypesViewModel by viewModels()
    private val activitesViewModel: ActivitesViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            CalanquesTheme {
                val navController = rememberNavController()
                
                // État de connexion global simple basé sur le token du loginViewModel
                val isLoggedIn = loginViewModel.token != null

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
                            },
                            onProfileClick = { 
                                val route = if (isLoggedIn) "profile" else "login"
                                navController.navigate(route) 
                            },
                            navController = navController,
                            isLoggedIn = isLoggedIn
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onRegisterSuccess = {
                                navController.navigate("login")
                            },
                            onBackToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("profile") {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val profileViewModel = remember { ProfileViewModel(context) }
                        ProfileScreen(
                            viewModel = profileViewModel,
                            token = loginViewModel.token
                        )
                    }

                    composable("activites") {
                        ActivitesScreen(
                            viewModel = activitesViewModel,
                            onBackClick = { navController.popBackStack() },
                            onActiviteClick = { }
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
    onActivityTypeClick: (ActivityType) -> Unit,
    onProfileClick: () -> Unit,
    navController: NavController,
    isLoggedIn: Boolean
) {
    val uiState by activityTypesViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val columns = if (configuration.screenWidthDp > 600) 2 else 1

    Scaffold(
        containerColor = BgColor,
        bottomBar = { BottomNavBar(navController, isLoggedIn) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AppHeader(onProfileClick = onProfileClick)

            val logoHeight = minOf(180.dp, (configuration.screenHeightDp * 0.20f).dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(logoHeight)
                    .padding(vertical = 12.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Types d'activités",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState) {
                    is ActivityTypesUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Red)
                        }
                    }
                    is ActivityTypesUiState.Error -> {
                        Text("Erreur de chargement", color = Red, modifier = Modifier.padding(16.dp))
                    }
                    is ActivityTypesUiState.Success -> {
                        val types = (uiState as ActivityTypesUiState.Success).activityTypes
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
//
@Composable
fun AppHeader(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Calanques", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Red)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RedLight)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Red, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ActivityTypeCard(activityType: ActivityType, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(220.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                AsyncImage(
                    model = "${BASE_URL}${activityType.image_url}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().weight(0.4f).padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activityType.libelle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Red
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, isLoggedIn: Boolean) {
    NavigationBar(
        containerColor = SurfaceColor,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            Triple("Activités", Icons.Default.Search, "home"),
            Triple("Panier", Icons.Default.ShoppingCart, "cart"),
            Triple("Compte", Icons.Default.Person, if (isLoggedIn) "profile" else "login"),
            Triple("Carte", Icons.Default.LocationOn, "map")
        )
        items.forEach { item ->
            val label = item.first
            val icon = item.second
            val route = item.third
            
            NavigationBarItem(
                selected = false,
                onClick = { 
                    if (route == "home" || route == "login" || route == "profile") {
                        navController.navigate(route) 
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(text = label, fontSize = 11.sp)
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
