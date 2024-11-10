package `in`.devh.rail.ui.homepage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.devh.rail.ui.components.homepage.StationSearchDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun StationCard(
    fromStation: Station,
    toStation: Station,
    onSwitchClick: () -> Unit,
    onFromSelect: () -> Unit,
    onToSelect: () -> Unit
) {
    var switchRotation by remember { mutableFloatStateOf(0f) }
    var fromOffset by remember { mutableFloatStateOf(0f) }
    var toOffset by remember { mutableFloatStateOf(0f) }


    val rotationAnimation by animateFloatAsState(
        targetValue = switchRotation,
        animationSpec = tween(300),
        label = "rotation"
    )

    val fromOffsetAnimation by animateFloatAsState(
        targetValue = fromOffset,
        animationSpec = tween(300),
        label = "fromOffset"
    )

    val toOffsetAnimation by animateFloatAsState(
        targetValue = toOffset,
        animationSpec = tween(300),
        label = "toOffset"
    )

    Card(modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer {
            shape = RoundedCornerShape(23.dp)
            clip = true
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                StationRow(
                    station = fromStation,
                    modifier = Modifier.offset(y = fromOffsetAnimation.dp),
                    onClickx = onFromSelect
                )
            }

            SwitchStationDivider(
                onSwitchClick = {
                    // Trigger animations
                    switchRotation += 180f
                    fromOffset = 48f  // Move down
                    toOffset = -48f   // Move up

                    // Reset positions after animation
                    kotlinx.coroutines.MainScope().launch {
                        delay(300)
                        fromOffset = 0f
                        toOffset = 0f
                        onSwitchClick()
                    }
                },
                rotation = rotationAnimation,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                StationRow(
                    station = toStation,
                    modifier = Modifier.offset(y = toOffsetAnimation.dp),
                    onClickx = onToSelect  // Fixed: directly pass the lambda
                )
            }

        }
    }

}



