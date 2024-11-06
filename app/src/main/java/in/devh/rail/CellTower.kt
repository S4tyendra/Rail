package `in`.devh.rail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import `in`.devh.rail.functions.Logs.LogCollector
import `in`.devh.rail.functions.Logs.logD
import `in`.devh.rail.functions.handleServiceState
import `in`.devh.rail.ui.theme.RailTheme
import `in`.devh.rail.wrappers.CellInfoWrapper
import `in`.devh.rail.wrappers.toJson
import org.json.JSONObject
import `in`.devh.rail.ui.components.SwitchListTile
import `in`.devh.rail.utils.PermissionHandler
import `in`.devh.rail.utils.PreferenceManager
import `in`.devh.rail.utils.PreferenceManager.isServiceEnabled
import com.slaviboy.iconscompose.Icon
import `in`.devh.rail.database.Database
import com.slaviboy.iconscompose.R as SR
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.tooling.preview.Preview

private const val TAG = "CellTower"

@RequiresApi(Build.VERSION_CODES.Q)
class CellTower : AppCompatActivity() {
    private lateinit var cellInfoWrapper: CellInfoWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogCollector.clearLogs()
        logD(TAG, "CellTower: onCreate")
//        supportActionBar?.hide()

        cellInfoWrapper = CellInfoWrapper(this)
        if (isServiceEnabled(this) &&
            PermissionHandler.hasRequiredPermissions(this)) {
            handleServiceState(true)
        }
        setContent {
            RailTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer

                ) {
                    MainContent()
                }
            }
        }
    }
}



@SuppressLint("InlinedApi", "AutoboxingStateCreation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent() {
    val phoneStatePermission = rememberPermissionState(Manifest.permission.READ_PHONE_STATE)
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val bgLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    // Track which permission we're currently requesting
    var currentPermissionIndex by remember { mutableIntStateOf(0) }

    // List of permissions and their requirements
    val permissions = remember {
        listOf(
            phoneStatePermission to "Phone State",
            notificationPermission to "Notifications",
            locationPermission to "Location",
            bgLocationPermission to "Background Location"
        )
    }

    // Function to request the next permission
    val requestNextPermission = {
        when (currentPermissionIndex) {
            0 -> phoneStatePermission.launchPermissionRequest()
            1 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermission.launchPermissionRequest()
                } else {
                    currentPermissionIndex++
                    locationPermission.launchPermissionRequest()
                }
            }
            2 -> locationPermission.launchPermissionRequest()
            3 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    bgLocationPermission.launchPermissionRequest()
                } else {
                    currentPermissionIndex++
                }
            }
        }
    }

    // Check if all required permissions are granted
    val allPermissionsGranted = remember(
        phoneStatePermission.status,
        notificationPermission.status,
        locationPermission.status,
        bgLocationPermission.status
    ) {
        phoneStatePermission.status.isGranted &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notificationPermission.status.isGranted) &&
                locationPermission.status.isGranted &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || bgLocationPermission.status.isGranted)
    }

    LaunchedEffect(
        phoneStatePermission.status,
        notificationPermission.status,
        locationPermission.status,
        bgLocationPermission.status
    ) {
        when (currentPermissionIndex) {
            0 -> if (phoneStatePermission.status.isGranted) currentPermissionIndex++
            1 -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                notificationPermission.status.isGranted) currentPermissionIndex++
            2 -> if (locationPermission.status.isGranted) currentPermissionIndex++
            3 -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                bgLocationPermission.status.isGranted) currentPermissionIndex++
        }
    }

    when {
        allPermissionsGranted -> {
            CellInfoDisplay()
        }
        else -> {
            PermissionScreen(
                currentPermission = permissions[currentPermissionIndex].second,
                onRequestPermission = requestNextPermission
            )
        }
    }
}

@Composable
fun PermissionScreen(
    currentPermission: String,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sad_dog),
            contentDescription = "Sad dog",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "We need $currentPermission permission to access cell information and show notifications",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(this)
                }
            }
        ) {
            Text("Open Settings")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellInfoDisplay() {
    logD(TAG, "CellInfoDisplay: Composable function called")
    val context = LocalContext.current
    var cellDataJson by remember { mutableStateOf("{}") }
    var isLoading by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var savedData by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    val sheetState = rememberModalBottomSheetState()


    val database = Database("cell_tower_logs")

    fun showSavedData() {
        savedData = database.getAll()
        showModal = true
    }



    fun updateCellInfo() {
        val cellInfoWrapper = CellInfoWrapper(context)
        cellDataJson = cellInfoWrapper.getCellInfo().toJson()
    }

    LaunchedEffect(Unit) {
        updateCellInfo()
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cell Information") },
                actions = {
                    IconButton(onClick = { updateCellInfo() }) {

                        Icon(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp),
                            type = SR.drawable.fi_br_rotate_right,
                            color = MaterialTheme.colorScheme.primary
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? AppCompatActivity)?.finish() }) {

                        Icon(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp),
                            type = SR.drawable.fi_br_angle_left,
                            color = MaterialTheme.colorScheme.primary

                        )

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
                JSONObject(cellDataJson)
            }
            SwitchListTile(
                title = "Contribute cell id's to Rail",
                subtitle = "We need your help to improve Rail",
                isChecked = isServiceEnabled(),
                onCheckedChange = { enabled ->
                    isLoading = true
                    PreferenceManager.setServiceEnabled(enabled = enabled)
                    context.handleServiceState(enabled)
                    isLoading = false
                }
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }


            Spacer(modifier = Modifier.height(16.dp))


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

            Button(
                onClick = {
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                        setPrimaryClip(ClipData.newPlainText("Cell Info JSON", cellDataJson))
                        Toast.makeText(context, "JSON copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy JSON")
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (showModal) {
            ModalBottomSheet(
                onDismissRequest = { showModal = false },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    savedData.forEach { (key, value) ->
                        Text("$key: $value")
                    }
                }
            }}



            Button(
                onClick = {
                    showSavedData()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Saved/Contributed Data")
            }
        }
    }

}

@Composable
fun CellInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
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

