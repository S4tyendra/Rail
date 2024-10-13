package `in`.devh.rail.ui.homepage

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R


@Composable
fun GetIconForItem(item: String) = when (item) {
    "Home" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_train,
            color = MaterialTheme.colorScheme.secondary
        )

    "PNR" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_list,
            color = MaterialTheme.colorScheme.secondary
        )

    "Live Status" ->
        Icon(
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            type = R.drawable.fi_br_train_side,
            color = MaterialTheme.colorScheme.secondary
        )

    "Schedule" -> Icon(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp),
        type = R.drawable.fi_br_calendar,
        color = MaterialTheme.colorScheme.secondary
    )

    else -> Icon(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp),
        type = R.drawable.fi_br_info,
        color = MaterialTheme.colorScheme.secondary
    )
}