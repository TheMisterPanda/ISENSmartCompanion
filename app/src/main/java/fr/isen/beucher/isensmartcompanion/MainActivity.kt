package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import fr.isen.beucher.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Logo en haut
                        GreetingImage(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(innerPadding)
                                .padding(top = 16.dp)
                        )

                        // Champ de texte en bas
                        BasicTextFieldExample(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
                                .padding(16.dp) // Padding supplémentaire
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingImage(modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable.isen)
    Box(modifier) {
        Image(
            painter = image,
            contentDescription = null
        )
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun BasicTextFieldExample(modifier: Modifier = Modifier) {
    // Gestion de l'état pour le texte saisi et la réponse
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var response by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        // Champ de texte pour entrer la question
        TextField(
            value = text,
            onValueChange = { input ->
                text = input
                response = "Vous avez entré : ${input.text}" // Réponse immédiate
            },
            label = { Text("Posez une question") },
            placeholder = { Text("Ex: Quel est l'horaire ?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Affichage de la réponse
        Text(
            text = response,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}