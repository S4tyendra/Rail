
package `in`.devh.rail.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.chip.Chip
import `in`.devh.rail.ui.components.findtrainspage.TopCard
import `in`.devh.rail.ui.theme.RailTheme

@Composable
fun FindTrainsPage() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopCard(
            title = "All Days",
            onclick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FindTrainsPagePreview() {
    RailTheme { FindTrainsPage() }
}
