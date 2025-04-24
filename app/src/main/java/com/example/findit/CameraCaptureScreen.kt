package com.example.findit

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager
import android.util.Log

@Composable
fun CameraCaptureScreen(
    navController: NavController,
    userId: Int,
    treasureId: Int
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var outputFile: File? = null
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && outputFile != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val requestFile = outputFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val photoPart = MultipartBody.Part.createFormData(
                        "photo",
                        outputFile!!.name,
                        requestFile
                    )
                    val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
                    val treasureIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), treasureId.toString())

                    val response = ApiService.api.uploadPhoto(photoPart, userIdBody, treasureIdBody)

                    if (response.isSuccessful) {
                        Log.d("UPLOAD", "✅ Serwer odpowiedział 200: zdjęcie zapisane")
                        println("✅ Zdjęcie wysłane!")

                        try {
                            val foundResponse = ApiService.api.markTreasureAsFound(
                                FoundRequest(userId = userId, treasureId = treasureId)
                            )
                            Log.d("UPLOAD", "📥 Zapisano znalezienie: ${foundResponse.code()}")
                            showSuccessDialog = true
                        } catch (e: Exception) {
                            Log.e("UPLOAD", "❌ Błąd przy zapisie znalezienia")
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("UPLOAD", "❌ Serwer odpowiedział: ${response.code()} - ${response.message()}")
                        println("❌ Błąd serwera: ${response.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            hasCameraPermission = true
        }
    }

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "treasure_${userId}_${treasureId}_$timestamp.jpg"
            )
            outputFile = file
            photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            photoUri?.let { takePictureLauncher.launch(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📸 Robienie zdjęcia...", style = MaterialTheme.typography.headlineMedium)
        if (!hasCameraPermission) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Potrzebujemy dostępu do kamery!", color = MaterialTheme.colorScheme.error)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack()
            },
            title = { Text("🎉 Sukces!") },
            text = { Text("Znalazłeś skarb! Gratulacje!") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
