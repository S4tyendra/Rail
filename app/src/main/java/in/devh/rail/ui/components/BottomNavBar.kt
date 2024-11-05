package `in`.devh.rail.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import `in`.devh.rail.ui.homepage.GetIconForItem

@Composable
fun BottomNavBar(
    navController: NavHostController,
    selectedTab: Int,
    setSelectedTab: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(23.dp)
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