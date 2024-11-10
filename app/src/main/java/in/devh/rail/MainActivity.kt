package `in`.devh.rail

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import `in`.devh.rail.data.models.AppContextProvider
import `in`.devh.rail.data.models.LogsData
import `in`.devh.rail.pages.TrainApp
import `in`.devh.rail.ui.theme.RailTheme


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContextProvider.initialize(this)
        LogsData().initializeLogFile()
        val sharedPref: SharedPreferences = getSharedPreferences("rail", MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)
        setContent {
            RailTheme(dynamicColor = true) {
                TrainApp(isFirstLaunch = isFirstLaunch)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun RailAppPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {
        TrainApp(isFirstLaunch = false)
    }
}
