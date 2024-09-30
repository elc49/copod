package com.lomolo.copod

import android.Manifest
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.lomolo.copod.permissions.LocationPermission
import com.lomolo.copod.ui.theme.CopodTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var locationServices: LocationPermission
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPriority: Int = Priority.PRIORITY_HIGH_ACCURACY
    private val mainViewModel: MainViewModel by viewModels { CopodViewModelProvider.Factory }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
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

            class SnackbarVisualWithError(override val message: String, val isError: Boolean) :
                SnackbarVisuals {
                override val actionLabel: String
                    get() = if (isError) "Error" else "Ok"

                override val withDismissAction: Boolean
                    get() = false

                override val duration: SnackbarDuration
                    get() = SnackbarDuration.Indefinite
            }

            CopodTheme {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val snackbarHostState = remember { SnackbarHostState() }
                        val copodSnackbarHost: @Composable (snackbarState: SnackbarHostState) -> Unit =
                            {
                                SnackbarHost(it) { data ->
                                    val isError =
                                        (data.visuals as? SnackbarVisualWithError)?.isError ?: false
                                    val buttonColor = if (isError) {
                                        ButtonDefaults.textButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        )
                                    } else {
                                        ButtonDefaults.textButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    }
                                    val containerColor = if (isError) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    }
                                    val contentColor = if (isError) {
                                        MaterialTheme.colorScheme.onErrorContainer
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    }

                                    Snackbar(
                                        dismissAction = {
                                            IconButton(onClick = { data.dismiss() }) {
                                                Icon(
                                                    Icons.TwoTone.Close,
                                                    modifier = Modifier.size(16.dp),
                                                    contentDescription = stringResource(R.string.close)
                                                )
                                            }
                                        },
                                        containerColor = containerColor,
                                        contentColor = contentColor,
                                        dismissActionContentColor = contentColor,
                                        action = {
                                            TextButton(
                                                onClick = { if (isError) data.dismiss() else data.performAction() },
                                                colors = buttonColor,
                                            ) {
                                                Text(data.visuals.actionLabel ?: "")
                                            }
                                        },
                                    ) {
                                        Text(
                                            data.visuals.message,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
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

                        CopodApplication(
                            navHostController,
                            snackbarHostState,
                            copodSnackbarHost,
                        )
                    }
                }
            }
        }
    }
}