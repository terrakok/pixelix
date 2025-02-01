package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline.GlobalTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline.HomeTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline.LocalTimelineComposable
import com.daniebeler.pfpixelix.utils.Navigate
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(navController: NavController) {

    val pagerState = rememberPagerState { 3 }

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(Res.string.app_name), fontWeight = FontWeight.Bold)
        },navigationIcon = {
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Outlined.QuestionMark, contentDescription = "Help")
            }
        }, actions = {
            Row {

                IconButton(onClick = { Navigate.navigate("conversations", navController) }) {
                    Icon(imageVector = Icons.Outlined.Mail, contentDescription = "Conversations")
                }
                IconButton(onClick = { Navigate.navigate("preferences_screen", navController) }) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        })

    }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(text = { Text(stringResource(Res.string.home)) },
                    selected = pagerState.currentPage == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    })

                Tab(text = { Text(stringResource(Res.string.local)) },
                    selected = pagerState.currentPage == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })

                Tab(text = { Text(stringResource(Res.string.global)) },
                    selected = pagerState.currentPage == 2,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    })
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> Box(modifier = Modifier.fillMaxSize()) {
                        HomeTimelineComposable(navController)
                    }

                    1 -> Box(modifier = Modifier.fillMaxSize()) {
                        LocalTimelineComposable(navController)
                    }

                    2 -> Box(modifier = Modifier.fillMaxSize()) {
                        GlobalTimelineComposable(navController)
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))

                        SheetItem(
                            header = stringResource(Res.string.home),
                            description = stringResource(Res.string.home_timeline_explained)
                        )

                        SheetItem(
                            header = stringResource(Res.string.local),
                            description = stringResource(Res.string.local_timeline_explained)
                        )

                        SheetItem(
                            header = stringResource(Res.string.global),
                            description = stringResource(Res.string.global_timeline_explained)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SheetItem(header: String, description: String) {
    Column {
        Text(text = header, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description)
        Spacer(modifier = Modifier.height(16.dp))
    }
}