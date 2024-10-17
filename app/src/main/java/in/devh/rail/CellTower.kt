package `in`.devh.rail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import `in`.devh.rail.ui.theme.RailTheme
import org.json.JSONObject
import cz.mroczis.netmonster.core.factory.NetMonsterFactory
import cz.mroczis.netmonster.core.model.cell.*
import cz.mroczis.netmonster.core.model.connection.PrimaryConnection
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import `in`.devh.rail.data.models.LogsData
import `in`.devh.rail.functions.Logs.LogCollector
import `in`.devh.rail.functions.Logs.logD
import `in`.devh.rail.workers.CellLocationWorker
import java.util.concurrent.TimeUnit


private const val TAG = "CellTower"
private const val REQUEST_PERMISSIONS = 1

@RequiresApi(Build.VERSION_CODES.Q)
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION
)

@RequiresApi(Build.VERSION_CODES.Q)
class CellTower : AppCompatActivity() {
    private lateinit var cellInfoWrapper: CellInfoWrapper
    private lateinit var locationWrapper: LocationWrapper



    override fun onCreate(savedInstanceState: Bundle?) {
        scheduleCellLocationWorker()
        super.onCreate(savedInstanceState)
        LogCollector.clearLogs()
        logD(TAG, "CellTower: onCreate")
        supportActionBar?.hide()

        cellInfoWrapper = CellInfoWrapper(this)
        locationWrapper = LocationWrapper(this)

        setContent {
            RailTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionHandler(REQUIRED_PERMISSIONS) {
                        CellInfoDisplay()
                    }
                }
            }
        }

        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                logD(TAG, "All permissions granted")
                updateCellInfo()
            } else {
                logD(TAG, "Some permissions were denied")
                Toast.makeText(this, "Permissions are required for full functionality", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun scheduleCellLocationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<CellLocationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CellLocationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun updateCellInfo(

    ): CellData {
        logD(TAG, "updateCellInfo: Started")
        if (!hasPermissions()) {
            logD(TAG, "updateCellInfo: Permissions not granted")
            requestPermissions()
            return CellData()
        }

        val cellData = cellInfoWrapper.getCellInfo()

        locationWrapper.getLocation { location ->
            logD(TAG, "updateCellInfo: Location received - Lat: ${location.latitude}, Lon: ${location.longitude}")
            cellData.longitude = location.longitude.toString()
            cellData.latitude = location.latitude.toString()
        }

        return cellData
    }
}
data class CellData(
    var mcc: String = "",
    var mnc: String = "",
    var lac: String = "",
    var cid: String = "",
    var longitude: String = "",
    var latitude: String = "",
    var gsmSignal: String = "",
    var wcdmaSignal: String = "",
    var lteSignal: String = "",
    var nrSignal: String = "",
    var tdscdmaSignal: String = ""
)

fun CellData.toJson(): String {
    return JSONObject().apply {
        put("mcc", mcc)
        put("mnc", mnc)
        put("lac", lac)
        put("cid", cid)
        put("longitude", longitude)
        put("latitude", latitude)
        put("strengthGSM", gsmSignal)
        put("strengthWCDMA", wcdmaSignal)
        put("strengthLTE", lteSignal)
        put("strengthNR", nrSignal)
        put("strengthTDSCDMA", tdscdmaSignal)
    }.toString().also {
        logD(TAG, "CellData.toJson: $it")
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: Array<String>,
    onPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }

    val permissionState = rememberMultiplePermissionsState(listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ))

    when {
        permissionState.allPermissionsGranted -> {
            onPermissionsGranted()
        }
        permissionState.shouldShowRationale || !permissionState.allPermissionsGranted -> {
            PermissionDeniedContent(
                onRequestPermission = { permissionState.launchMultiplePermissionRequest() },
                onOpenSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            )
        }
        else -> {
            SideEffect {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    }

    if (showRationale) {
        RationaleDialog(
            onDismiss = { showRationale = false },
            onConfirm = {
                showRationale = false
                permissionState.launchMultiplePermissionRequest()
            }
        )
    }
}

@Composable
fun PermissionDeniedContent(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sad_dog), // Make sure to add this image to your drawable resources
            contentDescription = "Sad dog",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Oh no! We're feeling a bit lost without those permissions.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Please help us find our way by granting the permissions we need.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permissions")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onOpenSettings) {
            Text("Open Settings")
        }
    }
}
@Composable
fun RationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permissions Required") },
        text = { Text("This app requires location and phone state permissions to function properly. Please grant the permissions to continue.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellInfoDisplay() {
    logD(TAG, "CellInfoDisplay: Composable function called")
    val context = LocalContext.current
    var cellDataJson by remember { mutableStateOf("{}") }
    var isLoadingGPS by remember { mutableStateOf(false) }

    fun updateCellInfo() {
        logD(TAG, "updateCellInfo: Started")
        val cellInfoWrapper = CellInfoWrapper(context)
        val cellData = cellInfoWrapper.getCellInfo()

        isLoadingGPS = true
        LocationWrapper(context).getLocation { location ->
            logD(TAG, "updateCellInfo: Location received - Lat: ${location.latitude}, Lon: ${location.longitude}")
            cellData.longitude = location.longitude.toString()
            cellData.latitude = location.latitude.toString()
            cellDataJson = cellData.toJson()
            isLoadingGPS = false
        }

        cellDataJson = cellData.toJson()
        logD(TAG, "updateCellInfo: Completed")
    }

    fun copyJsonToClipboard() {
        logD(TAG, "copyJsonToClipboard: Copying JSON to clipboard")
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Cell Info JSON", cellDataJson)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "JSON copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun copyLogs() {
        logD(TAG, "copyLogs: Copying logs to clipboard")
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Logs", LogCollector.getLogs())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    fun showSavedData() {

    }

    fun saveData() {
        logD(TAG, "saveData: Saving data")

        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        logD(TAG, "CellInfoDisplay: LaunchedEffect triggered")
        updateCellInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cell Information") },
                actions = {
                    IconButton(onClick = {
                        logD(TAG, "CellInfoDisplay: Refresh button clicked")
                        updateCellInfo()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val cellData = remember(cellDataJson) {
                JSONObject(cellDataJson).also {
                    logD(TAG, "CellInfoDisplay: Parsed JSON - $it")
                }
            }

            CellInfoCard(
                title = "Network Information",
                content = {
                    InfoRow("MCC", cellData.optString("mcc", "N/A"))
                    InfoRow("MNC", cellData.optString("mnc", "N/A"))
                    InfoRow("LAC", cellData.optString("lac", "N/A"))
                    InfoRow("CID", cellData.optString("cid", "N/A"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CellInfoCard(
                title = "Signal Strength",
                content = {
                    InfoRow("GSM", cellData.optString("strengthGSM", "N/A"))
                    InfoRow("WCDMA", cellData.optString("strengthWCDMA", "N/A"))
                    InfoRow("LTE", cellData.optString("strengthLTE", "N/A"))
                    InfoRow("NR", cellData.optString("strengthNR", "N/A"))
                    InfoRow("TD-SCDMA", cellData.optString("strengthTDSCDMA", "N/A"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CellInfoCard(
                title = "Location",
                content = {
                    if (isLoadingGPS) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), strokeCap = StrokeCap.Round)
                    } else {
                        InfoRow("Longitude", cellData.optString("longitude", "N/A"))
                        InfoRow("Latitude", cellData.optString("latitude", "N/A"))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    logD(TAG, "CellInfoDisplay: Copy JSON button clicked")
                    copyJsonToClipboard()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy JSON")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    logD(TAG, "Copy logs button clicked")
                    copyLogs()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Logs")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    logD(TAG, "Show Saved Data button clicked")
                    showSavedData()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show saved data")
            }

        }
    }
}

@Composable
fun CellInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    logD(TAG, "CellInfoCard: Rendering card with title - $title")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    logD(TAG, "InfoRow: $label - $value")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

class CellInfoWrapper(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun getCellInfo(): CellData {
        logD(TAG, "getCellInfo: Started")
        val cellData = CellData()
        val netMonster = NetMonsterFactory.get(context)

        val cells = netMonster.getCells()
        logD(TAG, "getCellInfo: All cells - $cells")
        val servingCell = cells.firstOrNull { it.connectionStatus is PrimaryConnection }
        logD(TAG, "getCellInfo: Serving cell - $servingCell")

        servingCell?.let { cell ->
            when (cell) {
                is CellGsm -> {
                    logD(TAG, "getCellInfo: GSM cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.cid.toString()
                    cellData.gsmSignal = "${cell.signal.rssi} dBm"
                }
                is CellWcdma -> {
                    logD(TAG, "getCellInfo: WCDMA cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.wcdmaSignal = "${cell.signal.rscp} dBm"
                }
                is CellLte -> {
                    logD(TAG, "getCellInfo: LTE cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.eci.toString()
                    cellData.lteSignal = "${cell.signal.rsrp} dBm"
                }
                is CellNr -> {
                    logD(TAG, "getCellInfo: NR cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.nci.toString()
                    cellData.nrSignal = "${cell.signal.ssRsrp} dBm"
                }
                is CellTdscdma -> {
                    logD(TAG, "getCellInfo: TD-SCDMA cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.tdscdmaSignal = "${cell.signal.rscp} dBm"
                }
            }
        }

        logD(TAG, "getCellInfo: Completed - $cellData")
        return cellData
    }
}

class LocationWrapper(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun getLocation(callback: (Location) -> Unit) {
        logD(TAG, "getLocation: Started")
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                logD(TAG, "getLocation: Location changed - Lat: ${location.latitude}, Lon: ${location.longitude}")
                callback(location)
                locationManager.removeUpdates(this)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                logD(TAG, "getLocation: Status changed - Provider: $provider, Status: $status")
            }
            override fun onProviderEnabled(provider: String) {
                logD(TAG, "getLocation: Provider enabled - $provider")
            }
            override fun onProviderDisabled(provider: String) {
                logD(TAG, "getLocation: Provider disabled - $provider")
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        logD(TAG, "getLocation: Location updates requested")
    }
}