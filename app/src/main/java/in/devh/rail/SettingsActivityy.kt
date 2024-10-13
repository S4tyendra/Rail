package `in`.devh.rail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import `in`.devh.rail.pages.SettingsScreenWrapper
import `in`.devh.rail.ui.theme.RailTheme

class SettingsActivityy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {

            RailTheme(dynamicColor = true) {
                SettingsScreenWrapper()
            }
        }
    }
}