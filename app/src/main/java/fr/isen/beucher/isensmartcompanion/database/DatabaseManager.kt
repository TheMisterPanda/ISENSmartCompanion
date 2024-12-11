package fr.isen.beucher.isensmartcompanion.database

import kotlinx.coroutines.flow.Flow

class DatabaseManager(private val interactionDao: InteractionDao) {

    val interactions: Flow<List<Interaction>> = interactionDao.getAllInteractions()

    suspend fun addInteraction(interaction: Interaction) {
        interactionDao.insertInteraction(interaction)
    }
}