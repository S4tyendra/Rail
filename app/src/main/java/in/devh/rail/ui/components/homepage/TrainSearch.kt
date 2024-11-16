package `in`.devh.rail.ui.components.homepage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import `in`.devh.rail.ui.homepage.SearchStationRow
import `in`.devh.rail.ui.homepage.SearchTrainRow
import `in`.devh.rail.ui.homepage.Station
import `in`.devh.rail.ui.homepage.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainSearchDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onTrainSelected: (Train) -> Unit
) {
    if (!isVisible) return

    BackHandler {
        onDismiss()
    }

    val focusRequester = remember { FocusRequester() }
    var searchQuery by remember { mutableStateOf("") }
    var trains by remember { mutableStateOf<List<Train>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            trains = emptyList()
            return@LaunchedEffect
        }
        trains = withContext(Dispatchers.IO) {
            searchTrains(context, searchQuery)
        }
    }
    LaunchedEffect(isVisible) {
        if (isVisible) {
            focusRequester.requestFocus()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Scaffold(
            topBar = {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { },
                    active = true,
                    onActiveChange = { if (!it) onDismiss() },
                    leadingIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    placeholder = { Text("Search trains...") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(trains) { train ->
                            SearchTrainRow(
                                train = train,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    onTrainSelected(train)
                                    onDismiss()
                                },
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

private fun searchTrains(context: Context, query: String): List<Train> {
    val dbFile = File(context.cacheDir, "ir.db")
    if (!dbFile.exists()) {
        context.assets.open("ir.db").use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    val db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)

    return try {
        val searchPattern = "%$query%"
        val exactPattern = query

        // Modified SQL query with improved ranking logic
        val sql = """
            SELECT number, name, offName FROM Trn 
            WHERE number LIKE ? OR name LIKE ? OR offName LIKE ?
            ORDER BY 
                CASE 
                    WHEN number = ? THEN 1           -- Exact code match
                    WHEN name = ? THEN 2           -- Exact name match
                    WHEN offName = ? THEN 3        -- Exact official name match
                    WHEN number LIKE ? THEN 4        -- Code starts with query
                    WHEN name LIKE ? THEN 5        -- Name starts with query
                    WHEN offName LIKE ? THEN 6     -- Official name starts with query
                    WHEN length(number) <= 3 THEN 7  -- Short codes (likely major stations)
                    ELSE 8
                END,
                length(number),                      -- Shorter codes first
                length(name)                       -- Shorter names next
            LIMIT 15
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(
            searchPattern, searchPattern, searchPattern,  // LIKE patterns for general matches
            exactPattern, exactPattern, exactPattern,     // Exact matches
            "$query%", "$query%", "$query%"              // Starts with patterns
        ))

        val results = mutableListOf<Train>()
        cursor.use {
            while (it.moveToNext()) {
                results.add(
                    Train(
                        number = it.getString(0),
                        name = it.getString(1)
                    )
                )
            }
        }
        results
    } finally {
        db.close()
    }
}