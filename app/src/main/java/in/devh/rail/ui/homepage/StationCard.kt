package `in`.devh.rail.ui.homepage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.devh.rail.*


@Composable
fun StationCard(fromStation: Station, toStation: Station) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            StationRow(station = fromStation)
            SwitchStationDivider(
                onSwitchClick = { /* Handle station switch */ },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            StationRow(station = toStation)
        }
    }
}


