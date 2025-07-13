package com.example.simplereader.ui

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect


@Composable
fun SetSystemBarsVisible(visible: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current

    LaunchedEffect(visible) {
        val activity = context as? Activity ?: return@LaunchedEffect
        val window = activity.window
        val controller = window.insetsController
        if (controller != null) {
            if (visible) {
                controller.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            }
        }
    }
}