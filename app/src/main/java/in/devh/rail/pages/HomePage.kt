package `in`.devh.rail.pages

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.devh.rail.SettingsActivity
import `in`.devh.rail.ui.homepage.GetIconForItem
import `in`.devh.rail.ui.homepage.HomeContent
import `in`.devh.rail.ui.urlfetcher.URLFetcherScreen
import kotlinx.coroutines.launch
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainApp(isFirstLaunch: Boolean, navController: NavHostController = rememberNavController()) {
    HomeScreen(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
){
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
                    onClick = {
                        val context = navController.context.applicationContext
                        val intent = Intent(context, SettingsActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
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
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            shape = RoundedCornerShape( 25.dp)
                            clip = true

                        }

                    ,
                ) {
                    listOf("Home", "PNR", "Live Status", "Schedule").forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { GetIconForItem(item) },
                            label = { Text(item) },
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                when (item) {
                                    "Home" -> navController.navigate("home")
                                    "PNR" -> navController.navigate("pnr")
                                    "Live Status" -> navController.navigate("live_status")
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeContent(
                        innerPadding = innerPadding
                    )
                }
                composable("pnr") {
                    PNRScreen()
                }
                composable("live_status") {
                    URLFetcherScreen()
                }
            }
        }
    }
}