package com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import com.daniebeler.pfpixelix.utils.Navigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPixelixComposable(
    navController: NavController,
    viewModel: AboutPixelixViewModel = injectViewModel(key = "about-pixelix-viewmodel-key") { aboutPixelixViewModel }
) {

    val context = LocalContext.current

    val scrollState = rememberScrollState()


    LaunchedEffect(Unit) {
        viewModel.getVersionName(context)
        viewModel.getAppIcon(context)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(Res.string.about_pixelix), fontWeight = FontWeight.Bold)
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos, contentDescription = ""
                )
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.appIcon == null) {
                    Image(
                        painter = painterResource(Res.drawable.pixelix_logo),
                        contentDescription = null,
                        Modifier
                            .width(84.dp)
                            .height(84.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        viewModel.appIcon!!,
                        contentDescription = null,
                        Modifier
                            .width(84.dp)
                            .height(84.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pixelix", fontSize = 36.sp, fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Version " + viewModel.versionName,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

            }

            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(icon = Icons.Outlined.StarRate,
                text = stringResource(Res.string.rate_us),
                onClick = { viewModel.rateApp(context) })


            HorizontalDivider(Modifier.padding(12.dp))


            Text(
                text = stringResource(Res.string.developed_by),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(12.dp, 0.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(text = "Emanuel Hiebeler", fontWeight = FontWeight.Bold)

                Row {
                    Image(painter = painterResource(Res.drawable.pixelfed_logo),
                        contentDescription = null,
                        Modifier
                            .width(32.dp)
                            .height(32.dp)
                            .clickable {
                                Navigate.navigate(
                                    "profile_screen/677938259497057424", navController
                                )
                            })

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(painter = painterResource(Res.drawable.mastodon_logo),
                        contentDescription = null,
                        Modifier
                            .width(32.dp)
                            .height(32.dp)
                            .clickable {
                                viewModel.openUrl(context, "https://techhub.social/@Hiebeler05")
                            })

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = "",
                        Modifier
                            .size(32.dp)
                            .clickable {
                                viewModel.openUrl(context, "https://emanuelhiebeler.me")
                            },
                        tint = Color(0xFF4793FF)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(text = "Daniel Hiebeler", fontWeight = FontWeight.Bold)

                Row {
                    Image(painter = painterResource(Res.drawable.pixelfed_logo),
                        contentDescription = null,
                        Modifier
                            .width(32.dp)
                            .height(32.dp)
                            .clickable {
                                Navigate.navigate(
                                    "profile_screen/497910174831013185", navController
                                )
                            })

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(painter = painterResource(Res.drawable.mastodon_logo),
                        contentDescription = null,
                        Modifier
                            .width(32.dp)
                            .height(32.dp)
                            .clickable {
                                viewModel.openUrl(context, "https://techhub.social/@daniebeler")
                            })

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = "",
                        Modifier
                            .size(32.dp)
                            .clickable {
                                viewModel.openUrl(context, "https://daniebeler.com")
                            },
                        tint = Color(0xFF4793FF)
                    )
                }
            }
        }
    }
}
