package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.datatransport.Event
import fr.isen.beucher.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            composable(Screen.Events.route) { EventsScreen() }
            composable(Screen.Agenda.route) { AgendaScreen() }
            composable(Screen.History.route) { HistoryScreen() }
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

@Composable
fun MainScreen() {
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Logo en haut
                GreetingImage()

                // Liste des messages et zone d'entrée
                MessageAndInput(
                    messages = messages,
                    onMessageSend = { message ->
                        val response = generateRandomString(6) // Générer une réponse aléatoire
                        messages = messages + (message to false) + (response to true) // Ajout utilisateur et application
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun EventsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Événements à venir", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AgendaScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Voici votre agenda", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Historique des activités", style = MaterialTheme.typography.bodyLarge)
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

// Génère une chaîne de caractères aléatoire de longueur spécifiée
fun generateRandomString(length: Int): String {
    val chars = ('A'..'Z') + ('a'..'z') // Lettres majuscules et minuscules
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

@Composable
fun GreetingImage(modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable.isen)
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MessageAndInput(
    messages: List<Pair<String, Boolean>>,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Gestion de l'état pour afficher le message temporaire
    var showSentMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
    ) {
        // Liste des messages avec une hauteur maximale
        Box(
            modifier = Modifier
                .weight(1f) // Utilise l'espace disponible tout en respectant la zone inférieure
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(messages) { (message, isFromApp) ->
                    Text(
                        text = message,
                        color = if (isFromApp) Color.Blue else Color.Black, // Changement de couleur
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Zone d'entrée
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            BasicTextFieldExample(
                onMessageSend = {
                    onMessageSend(it)

                    // Affiche le message temporaire "Question envoyée"
                    showSentMessage = true
                    coroutineScope.launch {
                        delay(2000) // Garde le message visible pendant 2 secondes
                        showSentMessage = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Affichage du texte "Question envoyée" si nécessaire
            if (showSentMessage) {
                Text(
                    text = "Question envoyée",
                    color = Color.Green,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                )
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun BasicTextFieldExample(
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = { input -> text = input },
            label = { Text("Posez une question") },
            placeholder = { Text("Ex: Quel est l'horaire ?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Bouton pour envoyer le message
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onMessageSend(text) // Passer le message pour traitement
                    text = "" // Réinitialiser le champ de saisie
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Envoyer")
        }
    }
}
