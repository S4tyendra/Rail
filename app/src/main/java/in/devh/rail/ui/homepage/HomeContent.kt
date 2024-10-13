package `in`.devh.rail.ui.homepage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R


@Composable
fun HomeContent(
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {


        Spacer(modifier = Modifier.height(16.dp))

        // From-To Section
        StationCard(
            fromStation = Station("MAS", "Chennai Central"),
            toStation = Station("SBC", "KSR Bengaluru City Junction")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Find Trains Button
        Button(
            onClick = { /* Handle find trains */ },
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
        SpotTrainCard(train = Train("12604", "Chennai Central SF Express"))

        Spacer(modifier = Modifier.height(16.dp))

        // Live Station Section
        LiveStationCard(station = Station("BNC", "Bengaluru Cantt."))

        Spacer(modifier = Modifier.height(16.dp))

        // Train List
        TrainList(
            trains = listOf(
                Train("12604", "Chennai Central SF Exp...", "HYB - MAS"),
                Train("18235", "Bilaspur Express cum P...", "BPL - BSP"),
                Train("17235", "Nagercoil Express", "SBC - NCJ"),
                Train("22412", "Arunachal AC SF Expre...", "ANVT - NHLN")
            )
        )
    }
}

