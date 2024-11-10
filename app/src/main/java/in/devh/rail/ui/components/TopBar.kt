package `in`.devh.rail.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R
import `in`.devh.rail.pages.LocalNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scope:  CoroutineScope,
    drawerState: DrawerState

) {
    val titles = mapOf<String, String>(
        "home" to "Open Rail",
        "pnr" to "PNR",
        "live_status" to "Live Status",
        "find_trains" to "Available Trains",
    )
    val navController = LocalNavController.current
    TopAppBar(
        title = { Text(titles[navController.currentDestination?.route] ?: "Open rail") },
        navigationIcon = {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = {
                    scope.launch { drawerState.open() }
                }) {

                Icon(
                    modifier = Modifier
                        .width(15.dp)
                        .height(15.dp),
                    type = R.drawable.fi_br_menu_burger,
                    color = MaterialTheme.colorScheme.primary
                )

            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}