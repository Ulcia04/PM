package com.example.findit

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun MyTreasuresScreen(navController: NavController, userId: Int) {

    val context = LocalContext.current
    var treasures by remember { mutableStateOf<List<Treasure>>(emptyList()) }
    var ranking by remember { mutableStateOf(-1) }
    var selectedTreasure by remember { mutableStateOf<Treasure?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                treasures = ApiService.api.getUserFoundTreasures(userId)
                ranking = ApiService.api.getUserRanking(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { navController.popBackStack() }) {
            Text("← Wróć")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("📦 Twoje Skarby", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("🔢 Liczba znalezionych: ${treasures.size}")
        Text("🏆 Miejsce w rankingu: ${if (ranking > 0) "$ranking." else "?"}")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(treasures) { treasure ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTreasure = treasure },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📍 ${treasure.name}", style = MaterialTheme.typography.titleMedium)
                        Text(text = treasure.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        selectedTreasure?.let { treasure ->
            Log.d("MyTreasuresScreen", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Ścieżka zdjęcia: ${treasure.photoPath}")

            AlertDialog(
                onDismissRequest = { selectedTreasure = null },
                title = { Text(text = "📍 ${treasure.name}") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = treasure.description)
                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            painter = rememberAsyncImagePainter("http://10.0.2.2:8080/${treasure.photoPath}"),
                            contentDescription = "Zdjęcie skarbu",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 250.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedTreasure = null }) {
                        Text("← Wróć")
                    }
                }
            )

        }
    }
}
