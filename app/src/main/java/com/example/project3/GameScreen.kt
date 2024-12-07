package com.example.project3

import android.content.ClipData
import android.content.ClipDescription
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(navController: NavController, modifier: Modifier = Modifier) {
    val backgroundImage: Painter = painterResource(id = R.drawable.menu_bg)
    val icon = painterResource(id = R.drawable.red_robot)

    val gridSizeDp = 80.dp
    val cols = 8
    val rows = 4

    val density = LocalDensity.current.density // Get the device density
    val gridSizePx = gridSizeDp.value * density // Convert grid size from dp to px

    //Path to finish game row,column
    val path = remember {
        listOf(
            Pair(1, 0),
            Pair(1, 1),
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4),
            Pair(1, 5),
            Pair(1, 3),
            Pair(2, 5),
            Pair(2, 6),
            Pair(2, 7),
            Pair(2, 8),
            Pair(1, 8), // Destination
        )
    }

    //starting robot position for screen sizes and movement/destination
    val firstTile = path.first() // Get the first tile in the path
    val firstTileRow = firstTile.first // Row of the first white tile
    val firstTileCol = firstTile.second // Column of the first white tile
    var dropPosition by remember {mutableStateOf(Offset(firstTileCol * gridSizePx, firstTileRow * gridSizePx)) }
    val destination = path.last()
    val horizontalMovement = remember { Animatable(dropPosition.x) }
    val verticalMovement = remember { Animatable(dropPosition.y) }

    //Arrows used to sequence movement
    val arrows = listOf(
        R.drawable.baseline_arrow_upward_24,
        R.drawable.baseline_arrow_downward_24,
        R.drawable.baseline_arrow_forward_24,
        R.drawable.baseline_arrow_back_24
    )
    val arrowNames = listOf("Up", "Down", "Forward", "Back")
    val arrowSequence = remember { mutableStateListOf<Int?>(null, null, null, null) }
    var draggingArrowIndex by remember { mutableStateOf<Int?>(null) }

    //Trigger animation on button execute boolean
    var animationStarted by remember { mutableStateOf(false) }

    //Media playback
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current // Check if in preview mode
    var mediaPlayerBackground: MediaPlayer? by remember { mutableStateOf(null) }
    var mediaPlayerMove: MediaPlayer? by remember { mutableStateOf(null) }
    var isMuted by remember { mutableStateOf(false) }

    //Winning condition
    var isGameWon by remember { mutableStateOf(false) }
    fun checkWinCondition() {
        val currentPosition = Pair(
            (dropPosition.y / gridSizeDp.value).toInt(),
            (dropPosition.x / gridSizeDp.value).toInt()
        )

        if (currentPosition == destination) {
            isGameWon = true
        }
    }

    //for preview rendering issue avoidance
    if (!isPreview) {
        //Activate media on screen load
        DisposableEffect(Unit) {
            mediaPlayerBackground = MediaPlayer.create(context, R.raw.night_club)
            mediaPlayerBackground?.start()
            mediaPlayerBackground?.isLooping = true
            mediaPlayerMove = MediaPlayer.create(context, R.raw.rolling)
            //close resource on exit/back out
            onDispose {
                mediaPlayerBackground?.release()
                mediaPlayerMove?.release()
            }
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayerBackground?.pause()
            mediaPlayerMove?.pause()
        } else {
            mediaPlayerBackground?.start()
            mediaPlayerBackground?.isLooping = true
        }
    }


    // Header for drag and drop boxes and arrow control
    Column(modifier = Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.12f)
        ) {
            val boxCount = 4
            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(.19f)
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
                    .padding(1.dp)
                    .weight(.9f)
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
                                                clipData = ClipData.newPlainText(
                                                    "arrow",
                                                    arrowNames[index]
                                                )
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
        // Buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    arrowSequence.clear()
                    arrowSequence.addAll(listOf(null, null, null, null))
                    animationStarted = false
                },
                modifier = Modifier
                    .size(200.dp, 35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Background color of button
                    contentColor = Color.Black // Color of the text/icon
                )
            ) {
                Text("Reset sequence")
            }

            // Mute button in the header row
            Button(
                onClick = { toggleMute() },
                modifier = Modifier
                    .size(150.dp, 35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMuted) Color.Magenta else Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text(if (isMuted) "Unmute" else "Mute Sound")
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
                    .size(200.dp, 35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green, // Background color of button
                    contentColor = Color.Black // Color of the text/icon
                )
            ) {
                Text("Excecute Sequence")
            }
        }

        //Animation for robot
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
                                    // Move right by one tile
                                    val newX = newPosition.x + gridSizePx
                                    newPosition = Offset(
                                        newX,
                                        newPosition.y
                                    )  // Move exactly one tile to the right
                                }

                                R.drawable.baseline_arrow_back_24 -> {
                                    // Move left by one tile
                                    val newX = newPosition.x - gridSizePx
                                    newPosition = Offset(
                                        newX,
                                        newPosition.y
                                    )  // Move exactly one tile to the left
                                }

                                R.drawable.baseline_arrow_upward_24 -> {
                                    // Move up by one tile
                                    val newY = newPosition.y - gridSizePx
                                    newPosition =
                                        Offset(newPosition.x, newY)  // Move exactly one tile up
                                }

                                R.drawable.baseline_arrow_downward_24 -> {
                                    // Move down by one tile
                                    val newY = newPosition.y + gridSizePx
                                    newPosition =
                                        Offset(newPosition.x, newY)  // Move exactly one tile down
                                }
                            }

                            // Animate the movement based on the arrow sequence
                            horizontalMovement.animateTo(newPosition.x, animationSpec = tween(1000))
                            verticalMovement.animateTo(newPosition.y, animationSpec = tween(1000))
                        }
                    }
                }
                dropPosition = newPosition
                checkWinCondition()
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


            // Canvas for drawing grid and path
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw horizontal grid lines for an 8x8 grid
                for (row in 0 until rows + 1) {  // +1 to draw the last line at the bottom
                    drawLine(
                        color = Color.Black.copy(alpha = 0.8f),
                        start = Offset(0f, row * gridSizeDp.toPx()),
                        end = Offset(size.width, row * gridSizeDp.toPx()),
                        strokeWidth = 5f
                    )
                }

                // Draw vertical grid lines for an 8x8 grid
                for (col in 0 until cols + 1) {  // +1 to draw the last line at the right
                    drawLine(
                        color = Color.Black.copy(alpha = 0.8f),
                        start = Offset(col * gridSizeDp.toPx(), 0f),
                        end = Offset(col * gridSizeDp.toPx(), size.height),
                        strokeWidth = 5f
                    )
                }

                // Draw the path tiles in white
                path.forEach { (row, col) ->
                    drawRect(
                        color = Color.White,
                        size = Size(gridSizeDp.toPx(), gridSizeDp.toPx()),
                        topLeft = Offset(col * gridSizeDp.toPx(), row * gridSizeDp.toPx())
                    )
                }

                // Draw the destination point (green)
                drawRect(
                    color = Color.Green,
                    size = Size(gridSizeDp.toPx(), gridSizeDp.toPx()),
                    topLeft = Offset(
                        destination.second * gridSizeDp.toPx(),
                        destination.first * gridSizeDp.toPx()
                    )
                )
            }
            Image(
                painter = icon,
                contentDescription = "robot",
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp)
                    .offset {
                        IntOffset(horizontalMovement.value.toInt(), verticalMovement.value.toInt())
                    }
            )

            // Display win message if player reaches the destination
            if (isGameWon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "You Win!",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 32.sp, // Adjust the font size
                            fontWeight = FontWeight.Bold, // Optionally make it bold
                            letterSpacing = 2.sp // Adjust letter spacing
                        )
                    )
                }
            }
        }
    }
}

@Preview(name = "landscape", widthDp = 800, heightDp = 600)
@Composable
fun GSprievew() {
    GameScreen(rememberNavController())
}