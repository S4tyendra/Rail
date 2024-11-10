package `in`.devh.rail.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R
import `in`.devh.rail.ui.components.BottomNavBar
import `in`.devh.rail.ui.components.DrawerContent
import `in`.devh.rail.ui.components.TopBar
import `in`.devh.rail.ui.homepage.HomeContent
import `in`.devh.rail.ui.urlfetcher.URLFetcherScreen
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import `in`.devh.rail.ui.theme.RailTheme

val LocalNavController = compositionLocalOf<NavHostController> { error("NavController not provided") }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainApp(isFirstLaunch: Boolean) {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        HomeScreen(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val openDialog = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val shouldShowBottomBar = remember(currentRoute) {
        when (currentRoute) {
            "home", "pnr", "live_status" -> true
            else -> false
        }
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Title") },
            text = { Text("This is an alert dialog.") },
            confirmButton = {
                TextButton(
                    onClick = { openDialog.value = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(navController) },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = shouldShowBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomNavBar(
                        navController = navController,
                        selectedTab = selectedTab,
                        setSelectedTab = { selectedTab = it }
                    )
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = currentRoute == "pnr",
                    enter = fadeIn(animationSpec = tween(400)) + expandIn(expandFrom = Alignment.Center),
                    exit = fadeOut(animationSpec = tween(400)) + shrinkOut(shrinkTowards = Alignment.Center)
                ) {
                    ExtendedFloatingActionButton(
                        text = { Text("Add PNR") },
                        onClick = {
                            openDialog.value = true
                        },
                        icon = {
                            Icon(
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp),
                                type = R.drawable.fi_br_plus,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeContent()
                }
                composable("pnr") {
                    PNRScreen()
                }
                composable("live_status") {
                    URLFetcherScreen()
                }
                composable("find_trains") {
                    FindTrainsPage()
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun RailAppPreviewz() {
    RailTheme(darkTheme = true, dynamicColor = true) {
        TrainApp(isFirstLaunch = false)
    }
}
