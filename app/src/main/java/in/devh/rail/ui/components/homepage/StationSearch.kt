package `in`.devh.rail.ui.components.homepage

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import `in`.devh.rail.ui.homepage.SearchStationRow
import `in`.devh.rail.ui.homepage.Station
import `in`.devh.rail.ui.homepage.StationRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSearchDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onStationSelected: (Station) -> Unit
) {
    if (!isVisible) return

    BackHandler {
        onDismiss()
    }

    val focusRequester = remember { FocusRequester() }
    var searchQuery by remember { mutableStateOf("") }
    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            stations = emptyList()
            return@LaunchedEffect
        }
        stations = withContext(Dispatchers.IO) {
            searchStations(context, searchQuery)
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
                    placeholder = { Text("Search stations...") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(stations) { station ->
                            SearchStationRow(
                                station = station,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    onStationSelected(station)
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

private fun searchStations(context: Context, query: String): List<Station> {
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
            SELECT code, name, offName FROM Stn 
            WHERE code LIKE ? OR name LIKE ? OR offName LIKE ?
            ORDER BY 
                CASE 
                    WHEN code = ? THEN 1           -- Exact code match
                    WHEN name = ? THEN 2           -- Exact name match
                    WHEN offName = ? THEN 3        -- Exact official name match
                    WHEN code LIKE ? THEN 4        -- Code starts with query
                    WHEN name LIKE ? THEN 5        -- Name starts with query
                    WHEN offName LIKE ? THEN 6     -- Official name starts with query
                    WHEN length(code) <= 3 THEN 7  -- Short codes (likely major stations)
                    ELSE 8
                END,
                length(code),                      -- Shorter codes first
                length(name)                       -- Shorter names next
            LIMIT 15
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(
            searchPattern, searchPattern, searchPattern,  // LIKE patterns for general matches
            exactPattern, exactPattern, exactPattern,     // Exact matches
            "$query%", "$query%", "$query%"              // Starts with patterns
        ))

        val results = mutableListOf<Station>()
        cursor.use {
            while (it.moveToNext()) {
                results.add(
                    Station(
                        code = it.getString(0),
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