package com.example.findit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddTreasureManualScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var hint by remember { mutableStateOf(TextFieldValue("")) }
    var lat by remember { mutableStateOf(TextFieldValue("")) }
    var lng by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start)) {
            Text("\u2190 Wróć")
        }

        Text("\u270D\uFE0F Dodaj Skarb Ręcznie", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa skarbu") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = hint,
            onValueChange = { hint = it },
            label = { Text("Wskazówka / opis") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Szerokość (latitude)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lng,
            onValueChange = { lng = it },
            label = { Text("Długość (longitude)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val treasure = Treasure(
                            name = name.text,
                            description = hint.text,
                            lat = lat.text.toDouble(),
                            lng = lng.text.toDouble()
                        )
                        ApiService.api.addTreasure(treasure)
                        message = "\u2705 Skarb dodany!"
                    } catch (e: Exception) {
                        message = "\u274C Błąd: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dodaj skarb")
        }

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}
