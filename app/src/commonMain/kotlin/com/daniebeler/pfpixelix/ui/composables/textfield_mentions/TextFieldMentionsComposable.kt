package com.daniebeler.pfpixelix.ui.composables.textfield_mentions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextFieldMentionsComposable(
    submit: (text: String) -> Unit,
    text: TextFieldValue,
    changeText: (newText: TextFieldValue) -> Unit,
    labelStringId: StringResource,
    submitButton: (@Composable () -> Unit)?,
    modifier: Modifier?,
    imeAction: ImeAction,
    suggestionsBoxColor: Color,
    viewModel: TextFieldMentionsViewModel = rememberViewModel("TextFieldMentionsViewModel") { textFieldMentionsViewModel }
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {

            TextField(
                value = text,
                singleLine = false,
                onValueChange = {
                    viewModel.changeText(it)
                    changeText(it)
                },
                placeholder = { Text(stringResource(labelStringId)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    submit(text.text)
                    viewModel.mentionsDropdownOpen = false
                })
            )


            if (submitButton != null) {
                Spacer(modifier = Modifier.width(12.dp))
                submitButton()
            }
        }
        if (viewModel.mentionsDropdownOpen) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(suggestionsBoxColor)
            ) {
                if (viewModel.mentionSuggestions.mentions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        viewModel.mentionSuggestions.mentions.map {
                            TextButton(onClick = {
                                viewModel.mentionsDropdownOpen = false
                                val textBeforeSelection = text.getTextBeforeSelection(9999).toString()
                                val index = textBeforeSelection.lastIndexOf("@") + 1
                                val newText = textBeforeSelection.substring(0, index) + it.acct
                                changeText(
                                    text.copy(
                                        text = newText + text.getTextAfterSelection(9999).toString(), selection = TextRange(newText.length)
                                    )
                                )
                            }) {
                                Text(
                                    text = "@${it.acct}",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}