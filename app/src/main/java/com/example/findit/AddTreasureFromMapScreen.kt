package com.example.findit

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

@SuppressLint("MissingPermission")
@Composable
fun AddTreasureFromMapScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var name by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            hasLocationPermission = true
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(17.0)
            Configuration.getInstance().load(context, context.getSharedPreferences("osm", 0))
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val request = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    if (loc != null) {
                        val geoPoint = GeoPoint(loc.latitude, loc.longitude)
                        currentLocation = geoPoint
                        mapView.controller.setCenter(geoPoint)
                        val marker = Marker(mapView).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.clear()
                        mapView.overlays.add(marker)
                        mapView.invalidate()
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(request, callback, null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start)) {
            Text("← Wróć")
        }

        Text("📍 Dodaj Skarb z Mapy", style = MaterialTheme.typography.headlineMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(factory = { mapView })
        }

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

        Button(
            onClick = {
                currentLocation?.let { geoPoint ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val treasure = Treasure(
                                name = name,
                                description = hint,
                                lat = geoPoint.latitude,
                                lng = geoPoint.longitude
                            )
                            ApiService.api.addTreasure(treasure)
                            message = "✅ Skarb dodany!"
                            launch(Dispatchers.Main) {
                                navController.popBackStack("main", inclusive = false)
                            }
                        } catch (e: Exception) {
                            message = "❌ Błąd: ${e.message}"
                        }
                    }
                } ?: run {
                    message = "❗ Brak lokalizacji."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dodaj skarb")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = if (message.startsWith("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
