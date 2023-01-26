package com.programmersbox.common

import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() {
    M3MaterialThemeSetup(
        isDarkMode = isSystemInDarkTheme()
    ) {
        var offset by remember { mutableStateOf(Offset.Zero) }
        val offsetAnimation = remember(offset) { Animatable(offset, Offset.VectorConverter) }
        var size by remember { mutableStateOf(100f) }
        var showCustomUrl by remember { mutableStateOf(false) }
        var url by remember { mutableStateOf(initialUrl) }
        var customUrl by remember { mutableStateOf(url) }
        var animateColors by remember { mutableStateOf(false) }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var center by remember { mutableStateOf(IntOffset.Zero) }
        Surface {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet {
                        TopAppBar(
                            title = { Text("Settings") },
                            actions = {
                                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }
                        )
                        ListItem(
                            headlineText = { Text("Url") },
                            supportingText = {
                                OutlinedTextField(
                                    value = customUrl,
                                    onValueChange = { customUrl = it },
                                    label = { Text("Url") },
                                    trailingIcon = {
                                        IconButton(onClick = { url = customUrl }) { Icon(Icons.Default.Send, null) }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            trailingContent = {
                                Column {
                                    Switch(
                                        showCustomUrl,
                                        onCheckedChange = { showCustomUrl = it }
                                    )

                                    Switch(
                                        animateColors,
                                        onCheckedChange = { animateColors = it }
                                    )
                                }
                            }
                        )
                    }
                },
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Masking") },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, null)
                                }
                            },
                            actions = {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            offsetAnimation.animateTo(center.toOffset()) {
                                                offset = value.copy(
                                                    x = value.x - size / 2,
                                                    y = value.y - size / 2
                                                )
                                            }
                                        }
                                    }
                                ) { Text("Reset Position") }
                            }
                        )
                    },
                    bottomBar = {
                        Column {
                            ListItem(
                                headlineText = { Text(size.toString()) },
                                supportingText = {
                                    Slider(
                                        value = size,
                                        onValueChange = { size = it },
                                        valueRange = 50f..900f,
                                    )
                                }
                            )
                        }
                    },
                ) { p ->
                    Crossfade(showCustomUrl) { target ->
                        when (target) {
                            true -> {
                                NetworkImage(
                                    url = url,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(p)
                                )
                            }

                            false -> {
                                val colors = listOf(
                                    Color.Blue,
                                    Color.Red,
                                    Color.Green,
                                    Color.Cyan,
                                    Color.Magenta,
                                    Color.Yellow
                                )

                                val ani = remember {
                                    List(colors.size) { index -> ColorAnimation(colors, index) }
                                }

                                ani.forEach { it.start(animateColors) }

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(p)
                                        .let { m ->
                                            if (animateColors) {
                                                m.background(Brush.sweepGradient(ani.map { it.color.value }))
                                            } else {
                                                m.background(Brush.sweepGradient(colors))
                                            }
                                        }
                                )
                            }
                        }
                    }
                    //This one guy is the reason why the masking works!
                    ShowBehind(
                        offset = offset,
                        offsetChange = { offset += it },
                        size = size,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(p)
                            .onGloballyPositioned { center = it.size.center },
                        sourceDrawing = { color, blendMode ->
                            drawRect(
                                color = color,
                                size = Size(size, size),
                                blendMode = blendMode,
                                topLeft = offset
                            )
                        },
                        sourceDraggingComposable = {
                            Box(
                                Modifier
                                    .size(size.toDp())
                                    .border(2.dp, MaterialTheme.colorScheme.primary, RectangleShape)
                            )
                        }
                    )
                }
            }
        }
    }
}

internal class ColorAnimation(
    private val colorList: List<Color>,
    initialIndex: Int
) {
    private var currentIndex: Int by mutableStateOf(initialIndex)
    val color = Animatable(colorList[currentIndex])

    @Composable
    fun start(animateColors: Boolean) {
        LaunchedEffect(currentIndex, animateColors) {
            if (animateColors) {
                val result = color.animateTo(
                    colorList[currentIndex],
                    animationSpec = tween(1000, easing = LinearEasing)
                )
                if (result.endState.isFinished) {
                    currentIndex = if (currentIndex + 1 >= colorList.size) {
                        0
                    } else {
                        currentIndex + 1
                    }
                }
            }
        }
    }
}
