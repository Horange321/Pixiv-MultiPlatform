package top.kagg886.pmf.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
actual val useWideScreenMode: Boolean
    @Composable
    get() = with(LocalWindowInfo.current.containerSize) {
        width.toFloat() / height.toFloat() > 1
    }