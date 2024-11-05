package `in`.devh.rail.ui.components


import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import `in`.devh.rail.CellTower
import `in`.devh.rail.SettingsActivity
import `in`.devh.rail.ShowLogsActivity
import `in`.devh.rail.data.models.AppContextProvider
import `in`.devh.rail.functions.Logs.logD

@Composable
fun DrawerContent(navController: NavHostController) {
    val TAG = "DrawerContent"
    ModalDrawerSheet {
        Text("Rail App", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = {
                val context = AppContextProvider.getAppContext()
                val intent = Intent(context, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = {
                val context = AppContextProvider.getAppContext()
                val intent = Intent(context, CellTower::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Show Logs") },
            selected = false,
            onClick = {
                logD(TAG, "Opening ShowLogsActivity")
                val context = AppContextProvider.getAppContext()
                val intent = Intent(context, ShowLogsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
}

