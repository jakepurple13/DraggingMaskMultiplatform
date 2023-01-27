package com.programmersbox.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

@Composable
internal fun ShowBehind(
    offset: Offset,
    offsetChange: (Offset) -> Unit,
    size: Float = 100f,
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    sourceDrawing: DrawScope.(color: Color, blendMode: BlendMode) -> Unit = { color, blendMode ->
        drawCircle(
            color = color,
            radius = size / 2f,
            center = offset + Offset(size / 2f, size / 2f),
            blendMode = blendMode
        )
    },
    sourceDraggingComposable: @Composable Density.() -> Unit = {
        Box(
            Modifier
                .size(size.toDp())
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    },
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
) {
    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // Destination
                drawRect(surfaceColor)

                // Source
                sourceDrawing(Color.Transparent, BlendMode.DstIn)

                restoreToCount(checkPoint)
            }
        }
        Box(
            modifier = Modifier.drag(
                offset = offset,
                offsetChange = offsetChange,
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            )
        ) { with(LocalDensity.current) { sourceDraggingComposable() } }
    }
}

@Composable
internal fun Modifier.drag(
    offset: Offset,
    offsetChange: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) = offset { offset.round() }
    .pointerInput(Unit) {
        detectDragGestures(
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDragCancel = onDragCancel
        ) { change, dragAmount ->
            change.consume()
            offsetChange(dragAmount)
        }
    }

internal object ShowBehindDefaults {
    fun defaultSourceDrawing(size: Float, offset: Offset): DrawScope.(color: Color, blendMode: BlendMode) -> Unit =
        { color, blendMode ->
            drawCircle(
                color = color,
                radius = size / 2f,
                center = offset + Offset(size / 2f, size / 2f),
                blendMode = blendMode
            )
        }

    const val defaultSize = 100f
}

@Composable
internal fun ShowBehind(
    offset: () -> Offset,
    offsetChange: (Offset) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    sourceDrawing: DrawScope.(color: Color, blendMode: BlendMode) -> Unit = ShowBehindDefaults
        .defaultSourceDrawing(ShowBehindDefaults.defaultSize, offset()),
) {
    Canvas(
        modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            ) { change, dragAmount ->
                change.consume()
                offsetChange(dragAmount)
            }
        }
    ) {
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(surfaceColor)

            // Source
            sourceDrawing(Color.Transparent, BlendMode.DstIn)

            restoreToCount(checkPoint)
        }
    }
}