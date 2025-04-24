package com.example.findit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.findit.ui.theme.FindItTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = getOrRegisterUserId()
        Log.d("FindIt", "\uD83C\uDD11 Zalogowano jako userId = $userId")

        setContent {
            FindItTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(userId = userId)
                }
            }
        }
    }

    private fun getOrRegisterUserId(): Int {
        val ipAddress = getDeviceIpAddress() ?: UUID.randomUUID().toString()
        var userId = -1

        runBlocking(Dispatchers.IO) {
            try {
                val response = ApiService.api.getUserByIp(ipAddress)
                userId = response.id
                Log.d("FindIt", "\uD83D\uDD04 Istniejący użytkownik znaleziony: id=$userId")
            } catch (e: Exception) {
                Log.d("FindIt", "\uD83D\uDCEB Nie znaleziono użytkownika, tworzenie nowego...")
                try {
                    val newUsername = "Użytkownik $ipAddress"
                    val createResponse = ApiService.api.createUser(newUsername, ipAddress)

                    if (createResponse.isSuccessful) {
                        userId = createResponse.body()?.get("userId") ?: -1
                        Log.d("FindIt", "\u2705 Utworzono nowego użytkownika: id=$userId")
                    } else {
                        Log.e("FindIt", "\u274C Błąd rejestracji: ${createResponse.code()} ${createResponse.message()}")
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        return userId
    }

    private fun getDeviceIpAddress(): String? {
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
}
