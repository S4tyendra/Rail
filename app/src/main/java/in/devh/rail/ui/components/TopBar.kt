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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scope:  CoroutineScope,
    drawerState: DrawerState

) {
    TopAppBar(
        title = { Text("Rail Status") },
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