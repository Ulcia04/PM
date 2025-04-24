package com.example.findit

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun FindTreasureScreen(navController: NavController, userId: Int) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var treasures by remember { mutableStateOf<List<Treasure>>(emptyList()) }
    var treasuresWithPhotos by remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedTreasure by remember { mutableStateOf<Treasure?>(null) }

    val heartIcon: Drawable? = context.getDrawable(R.drawable.love)

    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        onDispose { }
    }

    LaunchedEffect(Unit) {
        try {
            treasures = ApiService.api.getTreasures()
            treasuresWithPhotos = ApiService.api.getFoundTreasureIdsWithPhotos(userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(52.2297, 21.0122))
        }
    }

    LaunchedEffect(treasures, treasuresWithPhotos) {
        mapView.overlays.clear()

        treasures.forEach { treasure ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(treasure.lat, treasure.lng)
                title = treasure.name
                subDescription = treasure.description
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                if (treasure.id != null && treasuresWithPhotos.contains(treasure.id)) {
                    icon = heartIcon
                }

                setOnMarkerClickListener { _, _ ->
                    selectedTreasure = treasure
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.matchParentSize())

        Column(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Button(onClick = { navController.popBackStack() }) {
                Text("← Wróć")
            }
        }

        selectedTreasure?.let { treasure ->
            val alreadyWithPhoto = treasure.id != null && treasuresWithPhotos.contains(treasure.id)

            AlertDialog(
                onDismissRequest = { selectedTreasure = null },
                title = { Text("Skarb") },
                text = { Text("📍 ${treasure.name}\n${treasure.description}") },
                confirmButton = {
                    if (!alreadyWithPhoto) {
                        Button(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val response = ApiService.api.markTreasureAsFound(
                                        FoundRequest(userId = userId, treasureId = treasure.id!!)
                                    )
                                    println("✅ Skarb oznaczony jako znaleziony: ${response.code()}")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            selectedTreasure = null
                            navController.navigate("camera?userId=$userId&treasureId=${treasure.id}")
                        }) {
                            Text("📸 Zrób zdjęcie")
                        }
                    } else {
                        Button(onClick = { selectedTreasure = null }, enabled = false) {
                            Text("✅ Już znaleziony")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedTreasure = null }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}


