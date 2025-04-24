package com.example.findit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@Composable
fun RankingScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<UserWithFoundCount>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {

                users = ApiService.api.getUsersWithFoundCounts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Button(onClick = { navController.popBackStack() }) {
            Text("← Wróć")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("🏆 Ranking użytkowników", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("👤 ${user.username}")
                            Text("ID: ${user.id}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("📦 ${user.foundCount}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
