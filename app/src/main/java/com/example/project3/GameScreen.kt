package com.example.project3

import android.content.ClipData
import android.content.ClipDescription
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(navController: NavController, modifier: Modifier = Modifier) {

    val backgroundImage: Painter = painterResource(id = R.drawable.menu_bg)
    val icon = painterResource(id = R.drawable.red_robot)

    val arrows = listOf(
        R.drawable.baseline_arrow_upward_24,
        R.drawable.baseline_arrow_downward_24,
        R.drawable.baseline_arrow_forward_24,
        R.drawable.baseline_arrow_back_24
    )

    val arrowNames = listOf("Up", "Down", "Forward", "Back")
    var arrowSequence = remember { mutableStateListOf<Int?>(null, null, null, null) }
    var draggingArrowIndex by remember { mutableStateOf<Int?>(null) }

    var animationStarted by remember { mutableStateOf(false) }
    var dropPosition by remember { mutableStateOf(Offset(20f, 155f)) }
    val horizontalMovement = remember { Animatable(dropPosition.x) }
    val verticalMovement = remember { Animatable(dropPosition.y) }

    var context = LocalContext.current
    val isPreview = LocalInspectionMode.current // Check if in preview mode

    var mediaPlayerBackground: MediaPlayer? by remember { mutableStateOf(null) }
    var mediaPlayerMove: MediaPlayer? by remember { mutableStateOf(null) }

    if (!isPreview) {
        DisposableEffect(Unit) {
            mediaPlayerBackground = MediaPlayer.create(context, R.raw.night_club)
            mediaPlayerBackground?.start()
            mediaPlayerBackground?.isLooping = true
            mediaPlayerMove = MediaPlayer.create(context, R.raw.rolling)

            onDispose {
                mediaPlayerBackground?.release()
                mediaPlayerMove?.release()
            }
        }
    }

    Column(modifier = Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.15f)
        ) {
            val boxCount = 4
            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(.2f)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .border(1.dp, Color.Blue)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        draggingArrowIndex?.let {
                                            // Add the corresponding arrow resource ID to the sequence
                                            arrowSequence[index] = arrows[it]
                                            draggingArrowIndex = null
                                        }
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Display the arrow image in the box
                    arrowSequence[index]?.let { arrowId ->
                        painterResource(id = arrowId).let { arrowPainter ->
                            Icon(
                                painter = arrowPainter,
                                contentDescription = "Arrow in box",
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .weight(.8f)
            ) {
                arrows.forEachIndexed { index, arrowId ->
                    Box(
                        modifier = Modifier
                            .weight(.8f)
                            .padding(2.dp)
                            .dragAndDropSource {
                                detectTapGestures(
                                    onLongPress = { offset ->
                                        startTransfer(
                                            transferData = DragAndDropTransferData(
                                                clipData = ClipData.newPlainText("arrow", arrowNames[index])
                                            )
                                        )
                                        draggingArrowIndex = index
                                    }
                                )
                            }
                    ) {
                        painterResource(id = arrowId).let { arrowPainter ->
                            Icon(
                                painter = arrowPainter,
                                contentDescription = "Arrow $index",
                                modifier = Modifier
                                    .size(35.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        draggingArrowIndex?.let { draggingIndex ->
                            if (draggingIndex == index) {
                                painterResource(id = arrowId).let { arrowPainter ->
                                    Icon(
                                        painter = arrowPainter, // Use the same painter for the dragged item
                                        contentDescription = "Dragged Arrow",
                                        modifier = Modifier
                                            .size(35.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        Row (modifier = Modifier.fillMaxWidth()
            .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceAround){
            Button(
                onClick = {
                    arrowSequence.clear()
                    arrowSequence.addAll(listOf(null, null, null, null))
                    animationStarted = false
                },
                modifier = Modifier
                    .padding(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Background color of button
                    contentColor = Color.Black // Color of the text/icon
                )
            ) {
                Text("Reset sequence")
            }
            // Trigger the animation on button press
            Button(
                onClick = {
                    if (arrowSequence.all { it != null }) {
                        Log.d("ArrowSequence", "Arrow Sequence: $arrowSequence")
                        animationStarted = true
                    }
                },
                modifier = Modifier
                    .padding(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green, // Background color of button
                    contentColor = Color.Black // Color of the text/icon
                )
            ) {
                Text("Start Animation")
            }
        }

        LaunchedEffect(animationStarted) {
            if (animationStarted) {
                var newPosition = dropPosition

                // Process each arrow in the sequence
                for (arrow in arrowSequence) {
                    if (arrow != null) {
                        val index = arrows.indexOf(arrow)
                        if (index >= 0) {
                            mediaPlayerMove?.start()
                            when (arrow) {
                                R.drawable.baseline_arrow_forward_24 -> {
                                    newPosition = newPosition.copy(x = newPosition.x + 50f)
                                }
                                R.drawable.baseline_arrow_back_24 -> {
                                    newPosition = newPosition.copy(x = newPosition.x - 50f)
                                }
                                R.drawable.baseline_arrow_upward_24 -> {
                                    newPosition = newPosition.copy(y = newPosition.y - 50f)
                                }
                                R.drawable.baseline_arrow_downward_24 -> {
                                    newPosition = newPosition.copy(y = newPosition.y + 50f)
                                }
                            }

                            // Animate the movement based on the arrow sequence
                            horizontalMovement.animateTo(newPosition.x, animationSpec = tween(1000))
                            verticalMovement.animateTo(newPosition.y, animationSpec = tween(1000))
                        }
                    }
                }
                dropPosition = newPosition
            }
            animationStarted = false
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        ) {
            Image(
                painter = backgroundImage,
                contentDescription = "Background_img",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = icon,
                contentDescription = "robot",
                modifier = Modifier
                    .size(45.dp)
                    .padding(10.dp)
                    .offset(horizontalMovement.value.dp, verticalMovement.value.dp)
            )
        }
    }
}

@Preview(name = "landscape", widthDp = 800, heightDp = 600)
@Composable
fun GSprievew() {
    GameScreen(rememberNavController())
}