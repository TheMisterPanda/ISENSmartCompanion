package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                        messages =
                            messages + (message to false) + (response to true) // Ajout utilisateur et application
                    },
                    modifier = Modifier.fillMaxSize()
                )
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
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
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
