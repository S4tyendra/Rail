package `in`.devh.rail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import `in`.devh.rail.ui.homepage.*
import `in`.devh.rail.ui.theme.RailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RailTheme(dynamicColor = true) {
                TrainApp()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RailAppPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {

        TrainApp()
    }
}
