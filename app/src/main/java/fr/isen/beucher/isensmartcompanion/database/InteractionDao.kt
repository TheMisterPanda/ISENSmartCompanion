package fr.isen.beucher.isensmartcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {
    @Insert
    suspend fun insertInteraction(interaction: Interaction)

    @Query("SELECT * FROM interaction_table ORDER BY timestamp DESC")
    fun getAllInteractions(): Flow<List<Interaction>>
}
