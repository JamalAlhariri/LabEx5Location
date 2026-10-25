
package ca.gbc.comp3074.alhariri.jamal.labex5location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import ca.gbc.comp3074.alhariri.jamal.labex5location.ui.theme.LabEx5LocationTheme

class MainActivity : ComponentActivity() {

    private var locationText by mutableStateOf("Press Get Location")

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            getLocation()
        } else {
            locationText = "Location permission denied"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LabEx5LocationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("My GPS Location")

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(locationText)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(onClick = { getLocation() }) {
                        Text("Get Location")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fine && !coarse) {
            permissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        locationText = "Getting location..."

        val client = LocationServices.getFusedLocationProviderClient(this)

        val priority = if (fine) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        client.getCurrentLocation(
            priority,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                locationText = "Latitude: ${location.latitude}\n" +
                        "Longitude: ${location.longitude}"
            } else {
                locationText = "Location unavailable"
            }
        }.addOnFailureListener {
            locationText = "Could not get location"
        }
    }
}
