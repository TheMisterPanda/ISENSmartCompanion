package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.beucher.isensmartcompanion.database.DatabaseManager
import fr.isen.beucher.isensmartcompanion.database.Interaction
import fr.isen.beucher.isensmartcompanion.ia.GeminiIA
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(databaseManager: DatabaseManager) {
    val iaViewModel: GeminiIA = viewModel()

    val iaResponse by iaViewModel.textGenerationResult.collectAsState()

    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var lastProcessedResponse by remember { mutableStateOf<String?>(null) } // Nouvelle variable pour éviter la boucle

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                GreetingImage()

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(messages) { (message, isFromApp) ->
                        Text(
                            text = message,
                            color = if (isFromApp) Color.Blue else Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))
                MessageAndInput(
                    messages = messages,
                    onMessageSend = { message ->
                        messages = messages + (message to false)

                        iaViewModel.generateResponse(message)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    SaveInteractionsToDatabase(messages, databaseManager)

    iaResponse?.let { response ->
        if (response != lastProcessedResponse) {
            messages = messages + (response to true)
            lastProcessedResponse = response
        }
    }
}

@Composable
fun SaveInteractionsToDatabase(messages: List<Pair<String, Boolean>>, databaseManager: DatabaseManager) {
    val interaction = Interaction(
        userInput = "",
        aiResponse = "",
        timestamp = System.currentTimeMillis()
    )
    LaunchedEffect(messages) {
        messages.forEach { (message, isFromApp) ->
            if (isFromApp) {
                interaction.aiResponse = message
            } else {
                interaction.userInput = message
            }
        }
        databaseManager.addInteraction(interaction)
    }
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
    var showSentMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            BasicTextFieldExample(
                onMessageSend = {
                    onMessageSend(it)

                    showSentMessage = true
                    coroutineScope.launch {
                        delay(2000)
                        showSentMessage = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

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

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onMessageSend(text)
                    text = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Envoyer")
        }
    }
}
