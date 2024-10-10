package top.kagg886.pmf.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch

@Composable
fun SupportRTLModalNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    rtlLayout:Boolean = false,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    val layout = remember(rtlLayout) {
        if (rtlLayout) LayoutDirection.Rtl else LayoutDirection.Ltr
    }
    CompositionLocalProvider(LocalLayoutDirection provides layout) {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        drawerContent()
                    }
                }
            },
            modifier,
            drawerState,
            gesturesEnabled,
            scrimColor,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }

    }

}