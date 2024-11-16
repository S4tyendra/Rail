package `in`.devh.rail.ui.homepage

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R
import `in`.devh.rail.pages.LocalNavController
import `in`.devh.rail.ui.components.homepage.StationSearchDialog
import `in`.devh.rail.ui.components.homepage.TrainSearchDialog
import `in`.devh.rail.ui.theme.RailTheme


@SuppressLint("CommitPrefEdits")
@Composable
fun HomeContent() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("search_trains", MODE_PRIVATE)

    val from = remember { mutableStateOf(sharedPreferences.getString("from", "MAS|Chennai Central") ?: "MAS|Chennai Central") }
    val to = remember { mutableStateOf(sharedPreferences.getString("to", "SBC|KSR Bengaluru City Junction") ?: "SBC|KSR Bengaluru City Junction") }
    val train = remember { mutableStateOf(sharedPreferences.getString("train", "12604|Chennai Central SF Express") ?: "12604|Chennai Central SF Express") }
    var showSearchDialog = remember { mutableStateOf(false) }
    var isSearchingFrom = remember { mutableStateOf(false) }
    var isTrainSearching = remember { mutableStateOf(false) }

    val navController = LocalNavController.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // From-To Section
        StationCard(
            fromStation = Station(from.value.split("|")[1], from.value.split("|")[0]),
            toStation = Station(to.value.split("|")[1], to.value.split("|")[0]),
            onSwitchClick = {
                sharedPreferences.edit().apply {
                    putString("from", "${to.value.split("|")[0]}|${to.value.split("|")[1]}")
                    putString("to", "${from.value.split("|")[0]}|${from.value.split("|")[1]}")
                    apply()
                }
                from.value = sharedPreferences.getString("from", "MAS|Chennai Central") ?: "MAS|Chennai Central"
                to.value = sharedPreferences.getString("to", "SBC|KSR Bengaluru City Junction") ?: "SBC|KSR Bengaluru City Junction"
            },
            onToSelect = {
                isSearchingFrom.value = false
                showSearchDialog.value = true
            },
            onFromSelect = {
                isSearchingFrom.value = true
                showSearchDialog.value = true
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Find Trains Button
        Button(
            onClick = {
                navController.navigate("find_trains")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors()
        ) {
            Icon(
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp),
                type = R.drawable.fi_br_search,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Trains")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spot Train Section
        SpotTrainCard(
            train = Train(train.value.split("|")[0], train.value.split("|")[1]),
            onClick = {
                isTrainSearching.value = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Station Section
        LiveStationCard(station = Station("BNC", "Bengaluru Cantt."))

        Spacer(modifier = Modifier.height(16.dp))

        // Train List
//        TrainList(
//            trains = listOf(
//                Train("12604", "Chennai Central SF Exp...", "HYB - MAS"),
//                Train("18235", "Bilaspur Express cum P...", "BPL - BSP"),
//                Train("17235", "Nagercoil Express", "SBC - NCJ"),
//                Train("22412", "Arunachal AC SF Expre...", "ANVT - NHLN")
//            )
//        )
    }
    if (showSearchDialog.value) {
        StationSearchDialog(
            isVisible = showSearchDialog.value,
            onDismiss = {
                showSearchDialog.value = false
            },
            onStationSelected = { station ->
                if (isSearchingFrom.value) {
                    // Update "from" station
                    val newValue = "${station.code}|${station.name}"
                    sharedPreferences.edit().putString("from", newValue).apply()
                    from.value = newValue
                } else {
                    // Update "to" station
                    val newValue = "${station.code}|${station.name}"
                    sharedPreferences.edit().putString("to", newValue).apply()
                    to.value = newValue
                }
                showSearchDialog.value = false
            }
        )
    }
    if (isTrainSearching.value) {
        TrainSearchDialog (
            isVisible = isTrainSearching.value,
            onDismiss = {
                isTrainSearching.value = false
            },
            onTrainSelected = { selectedTrain ->
                if (isTrainSearching.value) {
                    val newValue = "${selectedTrain.number}|${selectedTrain.name}"
                    sharedPreferences.edit().putString("train", newValue).apply()
                    train.value = newValue
                }
                showSearchDialog.value = false
            }
        )
    }

}
