package com.daniebeler.pfpixelix.ui.composables.hashtagMentionText

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.utils.Navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HashtagsMentionsTextView(
    text: String,
    modifier: Modifier = Modifier,
    mentions: List<Account>?,
    navController: NavController,
    openUrl: (url: String) -> Unit,
    textSize: TextUnit? = null,
    viewModel: TextWithClickableHashtagsAndMentionsViewModel = rememberViewModel(key = "hashtags-mentions-tv$text") { textWithClickableHashtagsAndMentionsViewModel }
) {

    val colorScheme = MaterialTheme.colorScheme
    val textStyle = SpanStyle(color = colorScheme.onBackground)
    val primaryStyle = SpanStyle(color = colorScheme.primary)

    val hashtags =
        Regex("(?=[^\\w!])[@#][\\u4e00-\\u9fa5\\w']+(?:@[\\w']+)?(?:\\.\\w+)?(?:\\/\\w+)*|https?:\\/\\/\\S+")

    val annotatedStringList = remember {

        var lastIndex = 0
        val annotatedStringList = mutableStateListOf<AnnotatedString.Range<String>>()

        // Add a text range for hashtags
        for (match in hashtags.findAll(text)) {

            val start = match.range.first
            val end = match.range.last + 1

            val string = text.substring(start, end)

            if (start > lastIndex) {
                annotatedStringList.add(
                    AnnotatedString.Range(
                        text.substring(lastIndex, start), lastIndex, start, "text"
                    )
                )
            }
            if (string.startsWith("#")) {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "tag")
                )
            } else if (string.startsWith("@")) {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "account")
                )
            } else {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "link")
                )
            }

            lastIndex = end
        }

        // Add remaining text
        if (lastIndex < text.length) {
            annotatedStringList.add(
                AnnotatedString.Range(
                    text.substring(lastIndex, text.length), lastIndex, text.length, "text"
                )
            )
        }
        annotatedStringList
    }

    // Build an annotated string
    val annotatedString = buildAnnotatedString {
        annotatedStringList.forEach {
            if (it.tag == "tag" || it.tag == "account" || it.tag == "link") {
                pushStringAnnotation(tag = it.tag, annotation = it.item)
                withStyle(style = primaryStyle) { append(it.item) }
                pop()
            } else {
                withStyle(style = textStyle) { append(it.item) }
            }
        }
    }

    ClickableText(text = annotatedString, style = if (textSize != null) {
        MaterialTheme.typography.bodyMedium.copy(fontSize = textSize)
    } else {
        MaterialTheme.typography.bodyMedium
    },
        modifier = modifier, onClick = { position ->
            CoroutineScope(Dispatchers.Default).launch {
                val annotatedStringRange =
                    annotatedStringList.firstOrNull { it.start <= position && position < it.end }
                if (annotatedStringRange != null) {
                    if (annotatedStringRange.tag == "tag" || annotatedStringRange.tag == "account") {
                        val newItem = annotatedStringRange.item.drop(1)
                        val route = if (annotatedStringRange.tag == "tag") {
                            "hashtag_timeline_screen/$newItem"
                        } else {
                            if (mentions == null) {
                                "profile_screen/byUsername/${annotatedStringRange.item.drop(1)}"
                            } else {
                                var account =
                                    mentions.find { account: Account -> account.acct == newItem }
                                if (account == null) {
                                    account =
                                        mentions.find { account: Account -> account.username == newItem }
                                }
                                if (account != null) {
                                    //get my account id and check if it is mine account
                                    val myAccountId = viewModel.getMyAccountId()
                                    if (account.id == myAccountId) {
                                        "own_profile_screen"
                                    } else {
                                        "profile_screen/${account.id}"
                                    }
                                } else {
                                    ""
                                }
                            }

                        }
                        withContext(Dispatchers.Main) {
                            if (route.isNotBlank() && route.isNotEmpty()) {
                                Navigate.navigate(route, navController)
                            }
                        }
                    } else if (annotatedStringRange.tag == "link") {
                        openUrl(annotatedStringRange.item)
                    }
                }
            }
        })
}