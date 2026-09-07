package io.github.sophon.fightingnerd

import androidx.compose.ui.window.ComposeUIViewController

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    iosAppInit()
    App()
}
