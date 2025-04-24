package com.example.findit

import android.annotation.SuppressLint
import android.content.Context
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

@SuppressLint("ClickableViewAccessibility")@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Konfiguracja OSMDroid
    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        onDispose { }
    }

    var treasures by remember { mutableStateOf<List<Treasure>>(emptyList()) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(52.2297, 21.0122))
        }
    }

    // Pobieranie danych
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = ApiService.api.getTreasures()
                treasures = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Odświeżanie markerów
    LaunchedEffect(treasures) {
        mapView.overlays.clear()
        treasures.forEach { treasure ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(treasure.lat, treasure.lng)
                title = treasure.name
                subDescription = treasure.description
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // UI z Boxem
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.matchParentSize()
        )

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Wróć")
        }
    }
}
