package `in`.devh.rail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import `in`.devh.rail.ui.theme.RailTheme
import org.json.JSONObject

import cz.mroczis.netmonster.core.factory.NetMonsterFactory
import cz.mroczis.netmonster.core.model.cell.*
import cz.mroczis.netmonster.core.model.connection.PrimaryConnection
import cz.mroczis.netmonster.core.model.signal.SignalGsm
import cz.mroczis.netmonster.core.model.signal.SignalLte
import cz.mroczis.netmonster.core.model.signal.SignalNr
import cz.mroczis.netmonster.core.model.signal.SignalTdscdma
import cz.mroczis.netmonster.core.model.signal.SignalWcdma
import cz.mroczis.netmonster.core.model.signal.SignalCdma


object LogCollector {
    var logs = StringBuilder()

    fun appendLog(tag: String, message: String) {
        logs.append("$tag: $message\n")
    }

    fun getLogs(): String {
        return logs.toString()
    }

    fun clearLogs() {
        logs.clear()
    }
}

fun LogD(tag: String, message: String) {
    Log.d(tag, message)
    LogCollector.appendLog(tag, message)
}
private const val TAG = "CellTower"

class CellTower : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogCollector.clearLogs()

        LogD(TAG, "CellTower: onCreate")
        supportActionBar?.hide()
        setContent {
            RailTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CellInfoDisplay()
                }
            }
        }
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
        LogD(TAG, "CellData.toJson: $it")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellInfoDisplay() {

    LogD(TAG, "CellInfoDisplay: Composable function called")
    val context = LocalContext.current
    var cellDataJson by remember { mutableStateOf("{}") }
    var isLoadingGPS by remember { mutableStateOf(false) }

    fun updateCellInfo() {
        LogD(TAG, "updateCellInfo: Started")
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "updateCellInfo: Permission ACCESS_FINE_LOCATION not granted")
            return
        }

        val cellInfoWrapper = CellInfoWrapper(context)
        val cellData = cellInfoWrapper.getCellInfo()

        isLoadingGPS = true
        LocationWrapper(context).getLocation { location ->
            LogD(TAG, "updateCellInfo: Location received - Lat: ${location.latitude}, Lon: ${location.longitude}")
            cellData.longitude = location.longitude.toString()
            cellData.latitude = location.latitude.toString()
            cellDataJson = cellData.toJson()
            isLoadingGPS = false
        }

        cellDataJson = cellData.toJson()
        LogD(TAG, "updateCellInfo: Completed")
    }

    fun copyJsonToClipboard() {
        LogD(TAG, "copyJsonToClipboard: Copying JSON to clipboard")
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Cell Info JSON", cellDataJson)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "JSON copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    fun copyLogs() {
        LogD(TAG, "copyLogs: Copying logs to clipboard")
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Logs", LogCollector.getLogs())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        LogD(TAG, "CellInfoDisplay: LaunchedEffect triggered")
        updateCellInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cell Information") },
                actions = {
                    IconButton(onClick = {
                        LogD(TAG, "CellInfoDisplay: Refresh button clicked")
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
                    LogD(TAG, "CellInfoDisplay: Parsed JSON - $it")
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
                    LogD(TAG, "CellInfoDisplay: Copy JSON button clicked")
                    copyJsonToClipboard()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy JSON")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    LogD(TAG, "Copy logs button clicked")
                    copyLogs()

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Logs")
            }
        }
    }
}

@Composable
fun CellInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    LogD(TAG, "CellInfoCard: Rendering card with title - $title")
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
    LogD(TAG, "InfoRow: $label - $value")
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
    fun getCellInfo(): CellData {
        LogD(TAG, "getCellInfo: Started")
        val cellData = CellData()
        val netMonster = NetMonsterFactory.get(context)

        val cells = netMonster.getCells()
        LogD(TAG, "getCellInfo: All cells - $cells")
        val servingCell = cells.firstOrNull { it.connectionStatus is PrimaryConnection }
        LogD(TAG, "getCellInfo: Serving cell - $servingCell")

        servingCell?.let { cell ->
            when (cell) {
                is CellGsm -> {
                    LogD(TAG, "getCellInfo: GSM cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.cid.toString()
                    cellData.gsmSignal = "${cell.signal.rssi} dBm"
                }
                is CellWcdma -> {
                    LogD(TAG, "getCellInfo: WCDMA cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.wcdmaSignal = "${cell.signal.rscp} dBm"
                }
                is CellLte -> {
                    LogD(TAG, "getCellInfo: LTE cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.eci.toString()
                    cellData.lteSignal = "${cell.signal.rsrp} dBm"
                }
                is CellNr -> {
                    LogD(TAG, "getCellInfo: NR cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.tac.toString()
                    cellData.cid = cell.nci.toString()
                    cellData.nrSignal = "${cell.signal.ssRsrp} dBm"
                }
                is CellTdscdma -> {
                    LogD(TAG, "getCellInfo: TD-SCDMA cell detected")
                    cellData.mcc = cell.network?.mcc ?: ""
                    cellData.mnc = cell.network?.mnc ?: ""
                    cellData.lac = cell.lac.toString()
                    cellData.cid = cell.ci.toString()
                    cellData.tdscdmaSignal = "${cell.signal.rscp} dBm"
                }
            }
        }

        LogD(TAG, "getCellInfo: Completed - $cellData")
        return cellData
    }
}

class LocationWrapper(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun getLocation(callback: (Location) -> Unit) {
        LogD(TAG, "getLocation: Started")
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                LogD(TAG, "getLocation: Location changed - Lat: ${location.latitude}, Lon: ${location.longitude}")
                callback(location)
                locationManager.removeUpdates(this)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                LogD(TAG, "getLocation: Status changed - Provider: $provider, Status: $status")
            }
            override fun onProviderEnabled(provider: String) {
                LogD(TAG, "getLocation: Provider enabled - $provider")
            }
            override fun onProviderDisabled(provider: String) {
                LogD(TAG, "getLocation: Provider disabled - $provider")
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        LogD(TAG, "getLocation: Location updates requested")
    }
}