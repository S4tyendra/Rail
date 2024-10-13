package `in`.devh.rail.pages

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import `in`.devh.rail.ui.theme.SettingsTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWrapper() {
    SettingsTheme(dynamicColor = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        ) { innerPadding ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                SettingsScreen(Modifier.padding(innerPadding))
            } else {
                SettingsScreenLegacy(Modifier.padding(innerPadding))
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE) }

    var notificationsEnabled by remember { mutableStateOf(isNotificationPermissionGranted(context) && sharedPrefs.getBoolean("notifications_enabled", false)) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            notificationsEnabled = true
            sharedPrefs.edit().putBoolean("notifications_enabled", true).apply()
        }
    }

    val options = remember { getOptions() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            NotificationSetting(
                enabled = notificationsEnabled,
                onToggle = { enabled ->
                    if (enabled && !isNotificationPermissionGranted(context)) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationsEnabled = enabled
                        sharedPrefs.edit().putBoolean("notifications_enabled", enabled).apply()
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        options.forEach { (key, values) ->
            item {
                OptionSetting(
                    title = key.replace("_", " ").capitalize(Locale.ROOT),
                    options = values,
                    sharedPrefs = sharedPrefs
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingsScreenLegacy(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE) }
    val options = remember { getOptions() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        options.forEach { (key, values) ->
            item {
                OptionSetting(
                    title = key.replace("_", " ").capitalize(Locale.ROOT),
                    options = values,
                    sharedPrefs = sharedPrefs
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun NotificationSetting(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Notifications")
        Switch(
            checked = enabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
fun OptionSetting(title: String, options: Map<String, String>, sharedPrefs: SharedPreferences) {
    var selectedOption by remember { mutableStateOf(sharedPrefs.getString(title, options.keys.first()) ?: options.keys.first()) }

    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        options.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == key,
                    onClick = {
                        selectedOption = key
                        sharedPrefs.edit().putString(title, key).apply()
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(value)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun isNotificationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

fun getOptions(): Map<String, Map<String, String>> {
    return mapOf(
        "live_status" to mapOf(
            "confirmtkt" to "ConfirmTkt",
            "redrail" to "RedRail",
            "wit" to "WhereIsMyTrain"
        ),
        "pnr_info" to mapOf(
            "confirmtkt" to "ConfirmTkt",
            "redrail" to "RedRail"
        ),
        "train_schedule" to mapOf(
            "confirmtkt" to "ConfirmTkt",
            "redrail" to "RedRail",
            "wit" to "WhereIsMyTrain"
        ),
        "coach_position" to mapOf(
            "redrail" to "RedRail"
        ),
        "station_status" to mapOf(
            "wit" to "WhereIsMyTrain"
        ),
        "reset_irctc_password" to mapOf(
            "confirmtkt" to "ConfirmTkt"
        ),
        "user_status" to mapOf(
            "confirmtkt" to "ConfirmTkt",
            "redrail" to "RedRail"
        )
    )
}