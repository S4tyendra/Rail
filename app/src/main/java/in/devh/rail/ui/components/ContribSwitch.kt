package `in`.devh.rail.ui.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.devh.rail.pages.TrainApp
import `in`.devh.rail.ui.theme.RailTheme


@Composable
fun SwitchListTile(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    shape = RoundedCornerShape(23.dp)
                    clip = true
                }
                .clickable { onCheckedChange(!isChecked) },
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {
                Text(
                    text = title,
                    softWrap = true,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    softWrap = true,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                modifier = Modifier.padding(16.dp),
                checked = isChecked,
                onCheckedChange = { onCheckedChange(it) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RailAppPreview() {
    RailTheme(darkTheme = true, dynamicColor = true) {
        SwitchListTile(
            title = "Contribute cell id's to Rail",
            subtitle = "We need your help to improve Rail",
            isChecked = false,
            onCheckedChange = {  }
        )
    }
}
