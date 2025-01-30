package com.daniebeler.pfpixelix.ui.composables.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*

@Composable
fun EndOfListComposable() {
    Column(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(Res.string.this_is_the_end))
    }
}