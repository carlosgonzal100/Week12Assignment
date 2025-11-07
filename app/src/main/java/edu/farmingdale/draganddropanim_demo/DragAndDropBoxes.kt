@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
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

    /**added so the rectnagle is centered in the middle of the screen
     * on any screen no matter what
     */
    var target by remember { mutableStateOf(IntOffset.Zero) } // will set to true center later

    // Max allowed top-left in PX so the rect stays fully inside the red area
    var maxXBound by remember { mutableIntStateOf(0) }
    var maxYBound by remember { mutableIntStateOf(0) }

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

                                        // Propose the next position in PX (move farther per drop)
                                        val moveStep = 250 // increase this value to move more
                                        val next = when (index) {
                                            0 -> IntOffset(target.x, target.y - moveStep) // Up
                                            1 -> IntOffset(target.x + moveStep, target.y) // Right
                                            2 -> IntOffset(target.x, target.y + moveStep) // Down
                                            else -> IntOffset(target.x - moveStep, target.y) // Left
                                        }

                                        // Clamp using the hoisted bounds (no maxPx scope issue)
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
                    // Direction arrow for this box
                    val arrow = when (index) {
                        0 -> Icons.Filled.ArrowUpward
                        1 -> Icons.AutoMirrored.Filled.ArrowForward
                        2 -> Icons.Filled.ArrowDownward
                        else -> Icons.AutoMirrored.Filled.ArrowBack
                    }

                    if (index == dragBoxIndex) {
                        // Show ONLY the draggable token when it "lives" in this box
                        Icon(
                            imageVector = Icons.Filled.Face,     // or your circle token if you switch later
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
                        // Otherwise show ONLY the arrow (no drag source here)
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

        /**made by chat gpt to help translate the rectangle
         * left right up and down. for to do 7, uses step to
         * determine where the icon is on to move the rectangle
         * up down left and right
         */


        val rtatView by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0.0f,
            // Configure the animation duration and easing.
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
            // Rectangle size (keep these in one place)
            val rectW = 60.dp
            val rectH = 20.dp

            // Compute the TRUE center of the red area in PX, then center the rect
            val density = LocalDensity.current
            val centerPx = with(density) {
                val cx = ((maxWidth - rectW) / 2).toPx().toInt()
                val cy = ((maxHeight - rectH) / 2).toPx().toInt()
                IntOffset(cx, cy)
            }

            // Compute bounds IN THIS SCOPE, then publish them to the hoisted state
            val computedMaxX = with(density) { (maxWidth - rectW).toPx().toInt() }
            val computedMaxY = with(density) { (maxHeight - rectH).toPx().toInt() }

            // Publish bounds so onDrop (above) can clamp correctly
            LaunchedEffect(computedMaxX, computedMaxY) {
                maxXBound = computedMaxX
                maxYBound = computedMaxY
            }

            // Set the starting target to the exact center ONCE
            LaunchedEffect(Unit) {
                if (target == IntOffset.Zero) {
                    target = centerPx
                }
            }

            // Animate from current to target
            val pOffset by animateIntOffsetAsState(
                targetValue = target,
                animationSpec = tween(3000, easing = LinearEasing)
            )

            Box(
                modifier = Modifier
                    .offset { pOffset }          // px overload (correct)
                    .graphicsLayer {
                        rotationZ = rtatView
                        transformOrigin = TransformOrigin.Center
                    }
                    .size(rectW, rectH)
                    .background(Color.Yellow)     // ← make it visible
                    .border(1.dp, Color.Black)    // ← optional: outline so it pops
            )

            // ===== ToDo 8: RESET BUTTON =====
            Button(
                onClick = { target = centerPx },                  // recenter
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

