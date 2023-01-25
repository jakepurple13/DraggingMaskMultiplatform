package com.programmersbox.common

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

public actual fun getPlatformName(): String {
    return "Android"
}

@Composable
public fun UIShow() {
    App()
}

@Composable
internal actual fun NetworkImage(url: String, modifier: Modifier) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
internal actual fun M3MaterialThemeSetup(isDarkMode: Boolean, content: @Composable () -> Unit) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> {
            if (isDarkMode) darkColorScheme() else lightColorScheme()
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}