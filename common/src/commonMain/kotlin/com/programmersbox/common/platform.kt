package com.programmersbox.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

public expect fun getPlatformName(): String

@Composable
internal expect fun NetworkImage(url: String, modifier: Modifier)

internal sealed class ImageLoading {
    object Loading : ImageLoading()
    data class Loaded(val image: ImageBitmap) : ImageLoading()
}

@Composable
internal expect fun M3MaterialThemeSetup(isDarkMode: Boolean, content: @Composable () -> Unit)