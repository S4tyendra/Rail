package `in`.devh.rail.ui.homepage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.slaviboy.iconscompose.Icon
import com.slaviboy.iconscompose.R
import `in`.devh.rail.ui.components.homepage.StationSearchDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainRow(
    train: Train,
    modifier: Modifier = Modifier,
    onClickx: () -> Unit,
) {


    Row(
        modifier = modifier
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.large

        ) {
            Text(
                text = train.number,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = train.name, modifier = Modifier.weight(1f))


            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    println("Search clicked3")
                    onClickx()
                }

            ) {
                Icon(
                    modifier = Modifier
                        .width(15.dp)
                        .height(15.dp),
                    type = R.drawable.fi_br_search,
                    color = MaterialTheme.colorScheme.primary
                )
            }



    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTrainRow(
    train: Train,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick),  // Fixed: properly implement onClick
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = train.number,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = train.name, modifier = Modifier.weight(1f))
    }
}

