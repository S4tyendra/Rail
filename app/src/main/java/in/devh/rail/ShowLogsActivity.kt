package `in`.devh.rail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import `in`.devh.rail.ui.theme.RailTheme
import java.io.File
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R

class ShowLogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RailTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SavedDataScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDataScreen() {
    val context = LocalContext.current
    var fileContents by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        fileContents = readFileContents(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Data") },
                actions = {
                    IconButton(onClick = {
                        clearLogFile(context)
                        fileContents = readFileContents(context)
                    }) {

                        Icon(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp),
                            type = R.drawable.fi_rr_trash,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                    }
                    IconButton(onClick = {
                        shareLogFile(context)
                    }) {

                        Icon(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp),
                            type = R.drawable.fi_rr_share,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            LazyColumn {
                items(fileContents) { line ->
                    Text(
                        text = line,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

fun readFileContents(context: Context): List<String> {
    val logFileName = "app_log.txt"
    val file = File(context.filesDir, logFileName)
    return if (file.exists()) {
        file.readLines()
    } else {
        listOf("No data found")
    }
}

fun clearLogFile(context: Context) {
    val logFileName = "app_log.txt"
    val file = File(context.filesDir, logFileName)
    if (file.exists()) {
        file.writeText("")
    }
}

fun shareLogFile(context: Context) {
    val logFileName = "app_log.txt"
    val file = File(context.filesDir, logFileName)
    if (file.exists()) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, file.readText())
        }
        context.startActivity(Intent.createChooser(intent, "Share Log File"))
    }
}