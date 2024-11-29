package fr.isen.beucher.isensmartcompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_table")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var userInput: String,
    var aiResponse: String,
    val timestamp: Long
)