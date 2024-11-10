package `in`.devh.rail.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import `in`.devh.rail.ui.homepage.GetIconForItem
import `in`.devh.rail.ui.theme.RailTheme

@Composable
fun BottomNavBar(
    navController: NavHostController,
    selectedTab: Int,
    setSelectedTab: (Int) -> Unit,
    hideBottomBar: Boolean = false
) {
    if (hideBottomBar) return
    NavigationBar(
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                clip = true

            },
    ) {
        listOf("Home", "PNR", "Live Status", "Schedule").forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { GetIconForItem(item) },
                label = { Text(item) },
                selected = selectedTab == index,
                onClick = {
                    setSelectedTab(index)
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



@Preview(showBackground = true)
@Composable
fun NavPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {
        BottomNavBar(
            navController = NavHostController(
                context = androidx.compose.ui.platform.LocalContext.current
            ),
            selectedTab = 1,
            setSelectedTab = { },
            hideBottomBar = false
        )
    }
}
