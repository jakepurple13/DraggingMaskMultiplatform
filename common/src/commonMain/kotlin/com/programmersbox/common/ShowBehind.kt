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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

@Composable
internal fun ShowBehind(
    offset: Offset,
    offsetChange: (Offset) -> Unit,
    size: Float = 100f,
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 2.dp,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) {
    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // Destination
                drawRect(surfaceColor)

                // Source
                drawCircle(
                    color = Color.Transparent,
                    radius = size / 2f,
                    center = offset + Offset(size / 2f, size / 2f),
                    blendMode = BlendMode.DstIn
                )
                restoreToCount(checkPoint)
            }
        }
        Box(
            modifier = Modifier
                .drag(
                    offset = offset,
                    offsetChange = offsetChange,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
                .border(borderWidth, borderColor, CircleShape)
                .size(with(LocalDensity.current) { size.toDp() })
        )
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