package `in`.devh.rail.ui.homepage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R


@Composable
fun SwitchStationDivider(
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = onSwitchClick,
            modifier = Modifier.border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                MaterialTheme.shapes.extraLarge
            )
        ) {

            Icon(
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp),
                type = R.drawable.fi_br_sort_alt,
                color = MaterialTheme.colorScheme.primary
            )

        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}