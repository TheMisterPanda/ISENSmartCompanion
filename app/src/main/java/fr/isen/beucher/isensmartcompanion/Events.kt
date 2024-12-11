package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.isen.beucher.isensmartcompanion.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import java.io.Serializable

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
) : Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Events(): List<Event> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.apiService.getEvents().await()
                events = response
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load events: ${e.message}"
                isLoading = false
            }
        }
    }
    if (selectedEvent != null) {
        EventDetailScreen(
            context = context,
            event = selectedEvent!!,
            onBack = { selectedEvent = null }
        )
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Events") }) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            EventItem(event = event) {
                                selectedEvent = event
                            }
                        }
                    }
                }
            }
        }
    }
    return(events)
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = event.title, style = MaterialTheme.typography.titleLarge)
            Text(text = event.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.location, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(context: Context, event: Event, onBack: () -> Unit) {
    val sharedPreferences = context.getSharedPreferences("event_preferences", Context.MODE_PRIVATE)
    val isPinned = remember { mutableStateOf(sharedPreferences.getBoolean(event.id, false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title) },
                navigationIcon = {
                    Text(
                        text = "< Back",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onBack() }
                    )
                },
                actions = {
                    IconButton(onClick = {
                        val newPinnedState = !isPinned.value
                        isPinned.value = newPinnedState
                        sharedPreferences.edit().putBoolean(event.id, newPinnedState).apply()

                        if (newPinnedState) {
                            scheduleNotification(context, event)
                        }
                    }) {
                        Icon(
                            imageVector = if (isPinned.value) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Toggle Notification"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = event.title, style = MaterialTheme.typography.titleLarge)
            Text(text = event.description, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Date: ${event.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Location: ${event.location}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Category: ${event.category}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, event: Event) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "event_reminders",
            "Event Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("event_title", event.title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        event.id.hashCode(),
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = System.currentTimeMillis() + 10_000

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

class NotificationReceiver : android.content.BroadcastReceiver() {
    @SuppressLint("MissingPermission", "NotificationPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("event_title") ?: "Event Reminder"
        val notification = NotificationCompat.Builder(context, "event_reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Reminder")
            .setContentText("Don't forget: $title")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
    }
}