package fr.isen.beucher.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.beucher.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

@Composable
fun BottomNavigationApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) { MainScreen() }
            composable(Screen.Events.route) { Events() }
            composable(Screen.Agenda.route) { Agenda() }
            composable(Screen.History.route) { History() }
        }
    }
}

// Enumérable pour gérer les écrans et leurs routes
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Main : Screen("main", "Accueil", Icons.Filled.Home)
    object Events : Screen("events", "Événements", Icons.Filled.Menu)
    object Agenda : Screen("agenda", "Agenda", Icons.Filled.DateRange)
    object History : Screen("history", "Historique", Icons.Filled.Search)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(Screen.Main, Screen.Events, Screen.Agenda, Screen.History)
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) }
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                BottomNavigationApp()
            }
        }
    }
}


