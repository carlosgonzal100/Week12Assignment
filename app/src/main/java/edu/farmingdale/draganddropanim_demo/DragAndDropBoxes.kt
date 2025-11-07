@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable

/**Notes:
 *
 * used this to help lock screen orientation:
 * https://developer.android.com/guide/topics/manifest/activity-element
 *
 * found out how to rotate items here:
 * https://developer.android.com/develop/ui/compose/graphics/draw/modifiers
 */

//private val rotation = FloatPropKey()

@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }

    // Current top-left (in PX) of the yellow rectangle in the red area.
    // Initialized to (0,0); we set to true center later.
    var target by remember { mutableStateOf(IntOffset.Zero) }

    // Bounds (in PX) so the rectangle stays fully inside the red area.
    var maxXBound by remember { mutableIntStateOf(0) }
    var maxYBound by remember { mutableIntStateOf(0) }

    // Which direction was last triggered: 0=Up,1=Right,2=Down,3=Left
    var lastDir by remember { mutableIntStateOf(-1) }
    // "Bump" value that causes LaunchedEffect(dirTrigger) to rerun on each drop
    var dirTrigger by remember { mutableIntStateOf(0) }

    // Extra rotation (quick spin for Left/Right) layered onto your base rotation
    val extraRotation = remember { Animatable(0f) }
    // Scale pulse (brief pop) for Up/Down
    val scalePulse   = remember { Animatable(1f) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4

            var dragBoxIndex by remember {
                mutableIntStateOf(0)
            }

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        isPlaying = !isPlaying
                                        dragBoxIndex = index

                                        // Record direction + trigger
                                        lastDir = index
                                        dirTrigger += 1

                                        // Compute next position by a fixed step (in PX)
                                        val moveStep = 250 // increase this value to move more
                                        val next = when (index) {
                                            0 -> IntOffset(target.x, target.y - moveStep) // Up
                                            1 -> IntOffset(target.x + moveStep, target.y) // Right
                                            2 -> IntOffset(target.x, target.y + moveStep) // Down
                                            else -> IntOffset(target.x - moveStep, target.y) // Left
                                        }

                                        // Clamp so the rectangle stays fully visible
                                        target = IntOffset(
                                            x = next.x.coerceIn(0, maxXBound),
                                            y = next.y.coerceIn(0, maxYBound)
                                        )
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Static arrow label for this command box
                    val arrow = when (index) {
                        0 -> Icons.Filled.ArrowUpward
                        1 -> Icons.AutoMirrored.Filled.ArrowForward
                        2 -> Icons.Filled.ArrowDownward
                        else -> Icons.AutoMirrored.Filled.ArrowBack
                    }

                    if (index == dragBoxIndex) {

                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = "Drag token",
                            modifier = Modifier
                                .size(36.dp)
                                .dragAndDropSource(
                                    transferData = {
                                        DragAndDropTransferData(
                                            clipData = ClipData.newPlainText("token", "face")
                                        )
                                    }
                                ),
                            tint = Color.Black
                        )
                    } else {

                        //if the icon is not in the box, display the arrow
                        Icon(
                            imageVector = arrow,
                            contentDescription = when (index) {
                                0 -> "Move Up"
                                1 -> "Move Right"
                                2 -> "Move Down"
                                else -> "Move Left"
                            },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(36.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }


        val rtatView by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0.0f,
            animationSpec = repeatable(
                iterations = if (isPlaying) 10 else 1,
                tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(Color.Red)
        ) {
            // Rectangle size
            val rectW = 60.dp
            val rectH = 20.dp

            // Compute true visual center (top-left px so the rect is centered)
            val density = LocalDensity.current
            val centerPx = with(density) {
                val cx = ((maxWidth - rectW) / 2).toPx().toInt()
                val cy = ((maxHeight - rectH) / 2).toPx().toInt()
                IntOffset(cx, cy)
            }

            // Publish bounds (px) so top row can clamp movement
            val computedMaxX = with(density) { (maxWidth - rectW).toPx().toInt() }
            val computedMaxY = with(density) { (maxHeight - rectH).toPx().toInt() }
            LaunchedEffect(computedMaxX, computedMaxY) {
                maxXBound = computedMaxX
                maxYBound = computedMaxY
            }

            LaunchedEffect(dirTrigger) {
                when (lastDir) {

                               //when the rectangle moves up or down, it does a quick pulse
                    0, 2 -> {
                        scalePulse.snapTo(1f)
                        scalePulse.animateTo(1.15f, animationSpec = tween(140, easing = LinearEasing))
                        scalePulse.animateTo(1f,    animationSpec = tween(140, easing = LinearEasing))
                    }
                    1 -> { //when it moves to the right, it does a full spin to the right and keeps moving
                        extraRotation.snapTo(0f)
                        extraRotation.animateTo(360f, animationSpec = tween(300, easing = LinearEasing))
                        extraRotation.snapTo(0f)
                    }
                    3 -> { //when it moves to the left, it does a full spin to the left and keeps moving
                        extraRotation.snapTo(0f)
                        extraRotation.animateTo(-360f, animationSpec = tween(300, easing = LinearEasing))
                        extraRotation.snapTo(0f)
                    }
                }
            }

            // Set initial position to the true center of the screen
            LaunchedEffect(Unit) {
                if (target == IntOffset.Zero) {
                    target = centerPx
                }
            }


            val pOffset by animateIntOffsetAsState(
                targetValue = target,
                animationSpec = tween(3000, easing = LinearEasing)
            )

            // The yellow rectangle on the screen
            Box(
                modifier = Modifier
                    .offset { pOffset }
                    .graphicsLayer {

                        //causes the base rotation plus the extra rotation
                        //when moving the rectangle left or right
                        rotationZ = rtatView + extraRotation.value
                        transformOrigin = TransformOrigin.Center

                        //causes the scale pulse when moving up or down
                        scaleX = scalePulse.value
                        scaleY = scalePulse.value
                    }
                    .size(rectW, rectH)
                    .background(Color.Yellow)
                    .border(1.dp, Color.Black)
            )

            //the reset button, instantly resets the rectangle to the center
            Button(
                onClick = { target = centerPx },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

