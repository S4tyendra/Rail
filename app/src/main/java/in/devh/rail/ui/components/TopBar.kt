package `in`.devh.rail.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
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
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    val titles = mapOf(
        "home" to "Open Rail",
        "pnr" to "PNR",
        "live_status" to "Live Status",
        "find_trains" to ""
    )

    val navController = LocalNavController.current
    val currentRoute = navController.currentDestination?.route
    val currentTitle = titles[currentRoute] ?: "Open Rail"

    // Only show TopAppBar if there's a title
    if (currentTitle.isNotEmpty()) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Crossfade(
                        targetState = currentTitle,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        ),
                        label = "title_transition"
                    ) { title ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) + expandHorizontally(),
                            exit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) + shrinkHorizontally()
                        ) {
                            Text(title)
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        scope.launch { drawerState.open() }
                    }
                ) {
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
}