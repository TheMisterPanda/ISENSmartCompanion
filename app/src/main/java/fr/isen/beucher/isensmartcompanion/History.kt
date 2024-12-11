package fr.isen.beucher.isensmartcompanion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.beucher.isensmartcompanion.database.DatabaseManager
import fr.isen.beucher.isensmartcompanion.database.Interaction

@Composable
fun HistoryScreen(databaseManager: DatabaseManager) {
    val interactions by databaseManager.interactions.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Historique des interactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(color = androidx.compose.ui.graphics.Color.Gray)

        LazyColumn {
            items(interactions) { interaction ->
                InteractionItem(interaction)
            }
        }
    }
}

@Composable
fun InteractionItem(interaction: Interaction) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (interaction.userInput != "" && interaction.aiResponse != ""){
            Text(
                text = "Utilisateur : ${interaction.userInput}",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "IA : ${interaction.aiResponse}",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Blue,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = androidx.compose.ui.graphics.Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}