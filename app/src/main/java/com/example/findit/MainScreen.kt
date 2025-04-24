package com.example.findit

import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

@Composable
fun MainScreen(
    userId: Int,
    onShowMap: () -> Unit,
    onAddTreasure: () -> Unit,
    onMyTreasures: () -> Unit,
    onFindTreasure: () -> Unit,
    onShowRanking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("Użytkownik $userId") }
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }
    val ipAddress = remember { getDeviceIpAddress() ?: "Nieznane IP" }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = ApiService.api.getUserByIp(ipAddress)
                userName = response.username
                editedName = response.username
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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

            if (isEditing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nowa nazwa użytkownika") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val response = ApiService.api.updateUsername(userId, editedName)
                                if (response.isSuccessful) {
                                    userName = editedName
                                    println("✅ Zaktualizowano nazwę użytkownika w bazie: $editedName")
                                } else {
                                    println("❌ Błąd aktualizacji: ${'$'}{response.code()} - ${'$'}{response.message()}")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        isEditing = false
                    }) {
                        Text("Zapisz")
                    }
                    TextButton(onClick = { isEditing = false }) {
                        Text("Anuluj")
                    }
                }
            } else {
                Text("👤 name: $userName", color = Color.Gray)
                Text("🆔 ID: $userId", color = Color.Gray)
                Text("🌐 IP: $ipAddress", color = Color.Gray)
                TextButton(onClick = { isEditing = true }) {
                    Text("Zmień nazwę")
                }
            }

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

fun getDeviceIpAddress(): String? {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in Collections.list(interfaces)) {
            val addrs = intf.inetAddresses
            for (addr in Collections.list(addrs)) {
                if (!addr.isLoopbackAddress && addr is InetAddress) {
                    val ip = addr.hostAddress
                    if (!ip.contains("::")) return ip
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
}