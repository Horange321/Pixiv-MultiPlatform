package top.kagg886.pmf.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.cache.downloadCacheKey
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch
import top.kagg886.pmf.LocalSnackBarHost
import top.kagg886.pmf.ui.component.icon.Save
import java.net.URI

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePreviewer(
    onDismiss: () -> Unit,
    url: List<String>,
    startIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    val ctx = LocalPlatformContext.current
    val request = remember(url.hashCode()) {
        url.map {
            ImageRequest.Builder(ctx, it).build()
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(startIndex) { url.size }
        Box {
            HorizontalPager(
                state = pagerState,
                modifier = modifier.fillMaxSize()
            ) {
                SketchZoomAsyncImage(
                    request[it],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(
                Modifier.align(Alignment.TopEnd).padding(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                var showMenu by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    onClick = {
                        showMenu = !showMenu
                    },
                ) {
                    Icon(Icons.Default.Menu, null, tint = Color.White)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = {
                        showMenu = false
                    },
                ) {
                    val snack = LocalSnackBarHost.current
                    val platform = LocalPlatformContext.current
                    DropdownMenuItem(
                        text = {
                            Text("保存")
                        },
                        leadingIcon = {
                            Icon(Save, null)
                        },
                        onClick = {
                            scope.launch {
                                val cache = SingletonSketch.get(platform).downloadCache
                                val cacheKey = request[pagerState.currentPage].downloadCacheKey
                                val file = cache.withLock(cacheKey) {
                                    openSnapshot(cacheKey)?.use { snapshot->
                                        snapshot.data.toFile()
                                    }
                                }
                                if (file == null) {
                                    snack.showSnackbar("文件仍在下载，请稍等片刻...")
                                    return@launch
                                }
                                FileKit.saveFile(
                                    bytes = file.readBytes(),
                                    extension = "png",
                                    baseName = URI.create(url[pagerState.currentPage]).path.replace("/","_")
                                )
                                showMenu = false
                            }
                        }
                    )
                }
            }


            if (url.size > 1) {
                Card(modifier = Modifier.padding(10.dp).align(Alignment.BottomCenter)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            enabled = pagerState.currentPage > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Spacer(Modifier.width(5.dp))
                        TextButton(onClick = {}) {
                            Text("${pagerState.currentPage + 1}/${url.size}")
                        }
                        Spacer(Modifier.width(5.dp))
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            enabled = pagerState.currentPage < url.size - 1
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        }
                    }
                }
            }

            IconButton(
                onClick = onDismiss
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        }
    }

}