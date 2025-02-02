package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.CustomPost
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.Navigate

@Composable
fun TrendingAccountElement(
    account: Account,
    navController: NavController,
    viewModel: TrendingAccountElementViewModel = rememberViewModel(key = account.id) { trendingAccountElementViewModel }
) {
    val context = LocalKmpContext.current
    LaunchedEffect(account) {
        viewModel.loadItems(account.id)
    }

    Column(
        Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                Navigate.navigate("profile_screen/" + account.id, navController)
            }) {

        CustomAccount(account = account)

        if (account.note.isNotBlank()) {
            HashtagsMentionsTextView(
                text = account.note,
                mentions = null,
                navController = navController,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), openUrl = { url -> viewModel.openUrl(context, url) }
            )
        }

        NonlazyGrid(itemCount = minOf(9, viewModel.postsState.posts.size)) {
            Box(
                modifier = Modifier
            ) {
                CustomPost(post = viewModel.postsState.posts[it], navController = navController)
            }
        }
    }
}

@Composable
private fun NonlazyGrid(
    itemCount: Int,
    content: @Composable (Int) -> Unit
) {

    val columns = 3

    Column(modifier = Modifier.clip(RoundedCornerShape(12.dp)), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row (horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}
