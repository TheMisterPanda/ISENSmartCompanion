package fr.isen.beucher.isensmartcompanion.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entité représentant une interaction avec l'IA
@Entity(tableName = "interactions")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userInput: String,
    val aiResponse: String
)

// DAO pour interagir avec la base de données
@Dao
interface InteractionDao {
    @Insert
    suspend fun insertInteraction(interaction: Interaction)

    @Query("SELECT * FROM interactions ORDER BY id DESC")
    fun getAllInteractions(): Flow<List<Interaction>>
}

// Classe abstraite définissant la base de données Room
@Database(entities = [Interaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun interactionDao(): InteractionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Fonction pour obtenir une instance de la base de données
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "interaction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Gestionnaire pour effectuer des opérations sur la base de données
class DatabaseManager(private val interactionDao: InteractionDao) {

    // Récupère toutes les interactions
    val interactions: Flow<List<Interaction>> = interactionDao.getAllInteractions()

    // Ajoute une nouvelle interaction
    suspend fun addInteraction(userInput: String, aiResponse: String) {
        val interaction = Interaction(userInput = userInput, aiResponse = aiResponse)
        interactionDao.insertInteraction(interaction)
    }
}
