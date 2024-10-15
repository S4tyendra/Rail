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
import `in`.devh.rail.SettingsActivity

@Composable
fun DrawerContent(navController: NavHostController) {
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