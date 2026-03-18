package com.squeakgames.wyldore.location

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SanctuaryZoneManager(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val _zoneState = MutableStateFlow<ZoneState>(ZoneState.Unknown)
    val zoneState: StateFlow<ZoneState> = _zoneState.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val event = GeofencingEvent.fromIntent(intent) ?: return
            val transition = event.geofenceTransition
            _zoneState.value = when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> ZoneState.Inside
                Geofence.GEOFENCE_TRANSITION_EXIT -> ZoneState.Outside
                Geofence.GEOFENCE_TRANSITION_DWELL -> ZoneState.Dwelling
                else -> ZoneState.Unknown
            }
        }
    }

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    fun registerSanctuary(latitude: Double, longitude: Double, radius: Float) {
        if (!hasLocationPermission()) return
        context.registerReceiver(receiver, IntentFilter(GEOFENCE_ACTION), Context.RECEIVER_NOT_EXPORTED)
        val geofence = Geofence.Builder()
            .setRequestId(SANCTUARY_ID)
            .setCircularRegion(latitude, longitude, radius)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                Geofence.GEOFENCE_TRANSITION_EXIT or
                Geofence.GEOFENCE_TRANSITION_DWELL
            )
            .setLoiteringDelay(DWELL_MS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(GEOFENCE_ACTION).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        geofencingClient.addGeofences(request, pendingIntent)
    }

    fun unregisterAll() {
        try { context.unregisterReceiver(receiver) } catch (_: IllegalArgumentException) { }
        geofencingClient.removeGeofences(listOf(SANCTUARY_ID))
        _zoneState.value = ZoneState.Unknown
    }

    sealed interface ZoneState {
        data object Unknown : ZoneState
        data object Inside : ZoneState
        data object Outside : ZoneState
        data object Dwelling : ZoneState
    }

    companion object {
        private const val GEOFENCE_ACTION = "com.squeakgames.wyldore.GEOFENCE_EVENT"
        private const val SANCTUARY_ID = "sanctuary_zone"
        private const val DWELL_MS = 300_000
    }
}
