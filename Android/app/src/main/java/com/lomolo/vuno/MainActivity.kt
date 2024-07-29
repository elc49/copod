package com.lomolo.vuno

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.lomolo.vuno.permissions.LocationPermission
import com.lomolo.vuno.ui.theme.GiggyTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var locationServices: LocationPermission
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPriority: Int = Priority.PRIORITY_HIGH_ACCURACY
    private val mainViewModel: MainViewModel by viewModels { GiggyViewModelProvider.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            locationServices = LocationPermission
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            var shouldShowPermissionRationale by remember { mutableStateOf(false) }
            var shouldRedirectToUserLocationSettings by remember { mutableStateOf(false) }
            var hasLocationPermission by remember {
                mutableStateOf(
                    locationServices.checkSelfLocationPermission(
                        this
                    )
                )
            }
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
            ) { permissions ->
                when {
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                        hasLocationPermission = true
                        locationPriority = Priority.PRIORITY_HIGH_ACCURACY
                    }

                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                        hasLocationPermission = true
                        locationPriority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }

                    else -> {
                        shouldShowPermissionRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                    }
                }
                if (hasLocationPermission) {
                    fusedLocationClient.getCurrentLocation(
                        locationPriority, CancellationTokenSource().token
                    ).addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            mainViewModel.setDeviceGps(
                                LatLng(
                                    location.latitude, location.longitude
                                )
                            )
                        }
                    }
                }

                shouldRedirectToUserLocationSettings =
                    !shouldShowPermissionRationale && !hasLocationPermission
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(hasLocationPermission, lifecycleOwner) {
                val locationRequest =
                    LocationRequest.Builder(locationPriority, TimeUnit.SECONDS.toMillis(3)).build()
                val locationCallback: LocationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        for (location in p0.locations) {
                            mainViewModel.setDeviceGps(
                                LatLng(
                                    location.latitude, location.longitude
                                )
                            )
                        }
                    }
                }
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START && !hasLocationPermission && !shouldShowPermissionRationale) {
                        locationPermissionLauncher.launch(locationServices.permissions)
                    } else if (event == Lifecycle.Event.ON_START && hasLocationPermission) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                    } else if (hasLocationPermission && event == Lifecycle.Event.ON_STOP) {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    } else if (hasLocationPermission && event == Lifecycle.Event.ON_PAUSE) {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            GiggyTheme {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (shouldShowPermissionRationale) {
                            AlertDialog(onDismissRequest = { return@AlertDialog }, confirmButton = {
                                Button(onClick = {
                                    shouldShowPermissionRationale = false
                                    locationPermissionLauncher.launch(locationServices.permissions)
                                }) {
                                    Text(
                                        text = getString(R.string.approve),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                            }, title = {
                                Text(getString(R.string.location_required))
                            }, text = {
                                Text(
                                    text = getString(R.string.why_gps_perm),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }, icon = {
                                Icon(
                                    Icons.TwoTone.Info,
                                    contentDescription = getString(R.string.content_info)
                                )
                            })
                        }

                        GiggyApplication(rememberNavController())
                    }
                }
            }
        }
    }
}