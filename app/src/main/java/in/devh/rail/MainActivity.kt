package `in`.devh.rail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import `in`.devh.rail.ui.theme.RailTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.ui.layout.ContentScale
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RailTheme(dynamicColor = true) {
                TrainApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainApp(navController: NavHostController = rememberNavController()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Rail App", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* Handle settings click */ }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = { /* Handle about click */ }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rail Status") },
                    navigationIcon = {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "PNR", "Live Status", "Schedule").forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { GetIconForItem(item) },
                            label = { Text(item) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        ) { innerPadding ->
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
    }
}

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


@Composable
fun SwitchStationDivider(
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = onSwitchClick,
            modifier = Modifier.border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                MaterialTheme.shapes.extraLarge
            )
        ) {

            Icon(
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp),
                type = R.drawable.fi_br_sort_alt,
                color = MaterialTheme.colorScheme.primary
            )

        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StationRow(station: Station) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = station.code,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = station.name, modifier = Modifier.weight(1f))
        IconButton(colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = { /* Handle remove */ }) {
            Icon(Icons.Default.Close, contentDescription = "Remove")
        }
    }
}

@Composable
fun SpotTrainCard(train: Train) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "SPOT TRAIN",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = train.number,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = train.name, modifier = Modifier.weight(1f))
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { /* Handle search */ },
                ) {
                    Icon(
                        modifier = Modifier
                            .width(15.dp)
                            .height(15.dp),
                        type = R.drawable.fi_br_search,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun LiveStationCard(station: Station) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LIVE STATION",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = station.code,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = station.name, modifier = Modifier.weight(1f))

                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { /* Handle search */ },
                ) {
                    Icon(
                        modifier = Modifier
                            .width(15.dp)
                            .height(15.dp),
                        type = R.drawable.fi_br_search,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun TrainList(trains: List<Train>) {
    LazyColumn {
        items(trains) { train ->
            TrainListItem(train)
            HorizontalDivider()
        }
    }
}

@Composable
fun TrainListItem(train: Train) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_search_alt,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = train.number,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = train.name, fontWeight = FontWeight.Bold)
            }
            Text(text = train.route, color = Color.Gray)
        }
    }
}

data class Station(val code: String, val name: String)
data class Train(val number: String, val name: String, val route: String = "")

@Composable
fun GetIconForItem(item: String) = when (item) {
    "Home" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_train,
            color = MaterialTheme.colorScheme.secondary
        )

    "PNR" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_list,
            color = MaterialTheme.colorScheme.secondary
        )

    "Live Status" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_train_side,
            color = MaterialTheme.colorScheme.secondary
        )

    "Schedule" -> Icon(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp),
        type = R.drawable.fi_br_calendar,
        color = MaterialTheme.colorScheme.secondary
    )

    else -> Icon(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp),
        type = R.drawable.fi_br_info,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Preview(showBackground = true)
@Composable
fun RailAppPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {

        TrainApp()
    }
}
