package com.example.findit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    userId: Int,
    onShowMap: () -> Unit,
    onAddTreasure: () -> Unit,
    onMyTreasures: () -> Unit,
    onFindTreasure: () -> Unit,
    onShowRanking: () -> Unit,
//    onShowOSM: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "🎯 FindIt!",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Zalogowano jako użytkownik ID: $userId",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Znajduj i ukrywaj skarby w prawdziwym świecie",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainButton(text = "🗺️ Zobacz mapę", onClick = onShowMap)
            MainButton(text = "➕ Dodaj skarb", onClick = onAddTreasure)
            MainButton(text = "📦 Moje Skarby", onClick = onMyTreasures)
            MainButton(text = "📸 Znajdź skarb", onClick = onFindTreasure)
            MainButton(text = "🏆 Ranking", onClick = onShowRanking)
//            MainButton(text = "🗺️ Mapa (OSM)", onClick = onShowOSM)

        }

        Text(
            text = "© 2025 FindIt App",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun MainButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}
