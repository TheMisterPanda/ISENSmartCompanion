package fr.isen.beucher.isensmartcompanion

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun Agenda() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val events = remember { mutableStateMapOf<LocalDate, MutableList<String>>() }
    var newEventText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        selectedDate?.let {
            Text(
                text = "Date sélectionnée : ${it.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = newEventText,
                onValueChange = { newEventText = it },
                label = { Text("Nouvel événement") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (newEventText.isNotBlank()) {
                        val eventList = events.getOrPut(selectedDate!!) { mutableListOf() }
                        eventList.add(newEventText)
                        newEventText = ""
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Ajouter événement")
            }

            if (events[selectedDate].isNullOrEmpty()) {
                Text("Aucun événement pour cette date.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    events[selectedDate]?.forEach { event ->
                        Text(
                            text = "- $event",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        CalendarView { date ->
            selectedDate = date
        }
    }
}

@Composable
fun CalendarView(onDaySelected: (LocalDate?) -> Unit) {
    val months = generateYear2024()

    Column(modifier = Modifier.fillMaxSize()) {
        months.forEach { (month, dates) ->
            Text(
                text = month,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(dates) { date ->
                    DayItem(date) { selectedDate ->
                        onDaySelected(selectedDate)
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun DayItem(date: LocalDate?, onDayClick: (LocalDate?) -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .background(if (date != null) Color.LightGray else Color.Transparent)
            .border(1.dp, if (date != null) Color.Gray else Color.Transparent)
            .clickable { onDayClick(date) },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@SuppressLint("NewApi")
fun generateYear2024(): List<Pair<String, List<LocalDate?>>> {
    val months = listOf(
        Month.JANUARY to 31,
        Month.FEBRUARY to 29,
        Month.MARCH to 31,
        Month.APRIL to 30,
        Month.MAY to 31,
        Month.JUNE to 30,
        Month.JULY to 31,
        Month.AUGUST to 31,
        Month.SEPTEMBER to 30,
        Month.OCTOBER to 31,
        Month.NOVEMBER to 30,
        Month.DECEMBER to 31
    )

    return months.map { (month, daysInMonth) ->
        val firstDayOfMonth = LocalDate.of(2024, month, 1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val totalCells = firstDayOfWeek + daysInMonth

        val dates = mutableListOf<LocalDate?>()

        repeat(firstDayOfWeek) { dates.add(null) }

        dates.addAll((1..daysInMonth).map { day ->
            LocalDate.of(2024, month, day)
        })

        while (dates.size % 7 != 0) {
            dates.add(null)
        }

        month.name.lowercase().replaceFirstChar { it.uppercase() } to dates
    }
}
