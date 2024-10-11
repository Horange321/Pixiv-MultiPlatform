package top.kagg886.pmf.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import top.kagg886.pixko.User
import top.kagg886.pmf.ui.component.ProgressedAsyncImage
import top.kagg886.pmf.ui.route.main.detail.author.AuthorScreen

@Composable
fun AuthorCard(modifier: Modifier = Modifier, user: User) {
    val nav = LocalNavigator.currentOrThrow
    OutlinedCard(modifier = modifier.clickable {
        nav.push(AuthorScreen(user.id))
    }) {
        ListItem(
            headlineContent = {
                Text(user.name)
            },
            supportingContent = {
                Text(user.comment?.lines()?.first()?.takeIf { it.isNotEmpty() } ?: "没有简介")
            },
            leadingContent = {
                ProgressedAsyncImage(
                    url = user.profileImageUrls.content,
                    modifier = Modifier.size(35.dp)
                )
            }
        )
    }
}