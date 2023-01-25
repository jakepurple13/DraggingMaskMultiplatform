package com.programmersbox.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.skia.Image
import platform.UIKit.UIViewController

public actual fun getPlatformName(): String {
    return "iOS"
}

@Composable
private fun UIShow() {
    App()
}

public fun MainViewController(): UIViewController = Application("DraggingMask") {
    Column {
        Spacer(Modifier.height(30.dp))
        UIShow()
    }
}

@Composable
internal actual fun M3MaterialThemeSetup(isDarkMode: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if(isDarkMode) darkColorScheme() else lightColorScheme(), content = content)
}

@Composable
internal fun loadImage(url: String): State<ImageLoading> = produceState<ImageLoading>(ImageLoading.Loading, url) {
    try {
        value = ImageLoading.Loaded(
            Image.makeFromEncoded(HttpClient().get(url).readBytes()).toComposeImageBitmap()
        )
    } catch (e: Exception) {
        println(url)
        e.printStackTrace()
    }
}

@Composable
internal actual fun NetworkImage(url: String, modifier: Modifier) {
    val loading = loadImage(url).value

    if (loading is ImageLoading.Loaded) {
        Image(
            bitmap = loading.image,
            contentDescription = null,
            modifier = modifier
        )
    }
}