package com.tzeentch.teacherhelper.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.tzeentch.teacherhelper.dto.DetailsDto
import com.tzeentch.teacherhelper.presenters.DetailsPresenter
import com.tzeentch.teacherhelper.utils.DetailsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DetailsScreen(
    navController: NavController,
    presenter: DetailsPresenter = koinInject(),
    requestId: String
) {
    LaunchedEffect(key1 = Unit) { presenter.getRequests(id = requestId) }


    when (val result = presenter.detailsState.collectAsState().value) {
        is DetailsUiState.ReceiveTask -> {
            DetailsScreen(
                detailsDto = result.detailsDto
            )
        }

        is DetailsUiState.Loading -> {
            RotatingProgressBar()
        }

        else -> {

        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreen(
    detailsDto: DetailsDto
) {

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val listOfDetailsTabs = listOf(
        DetailsTabItems.DetailsTab1 {
            SlidesTab(
                imagesUrls = detailsDto.images,
                downloadingUrl = detailsDto.pttxUrl
            )
        },
        DetailsTabItems.DetailsTab2 {
            SummaryTab(
                summaryText = detailsDto.shortText
            )
        },
        DetailsTabItems.DetailsTab3 {
            RecommendationsTab(
                recommendations = detailsDto.teachingRecommendations
            )
        },
        DetailsTabItems.DetailsTab4 {
            EstimationTab(
                estimations = detailsDto.lessonEstimates
            )
        },
        DetailsTabItems.DetailsTab5 {
            QuestionsTab(
                questions = detailsDto.possibleQuestions
            )
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxWidth().systemBarsPadding(),
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.details_screen_title),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFFC9D1C8)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF304040)
                    )
                )
                TabPicker(
                    tabs = listOfDetailsTabs,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            DetailsTabContent(tabs = listOfDetailsTabs, pagerState = pagerState)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabPicker(
    tabs: List<DetailsTabItems>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope

) {

    ScrollableTabRow(
        edgePadding = 0.dp,
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color(0xFF304040),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color(0xFFC9D1C8)
            )
        },
    ) {
        tabs.forEachIndexed { index, detailsTabItems ->
            Tab(
                text = {
                    Text(
                        text = detailsTabItems.title,
                        color = Color(0xFFC9D1C8),
                        fontWeight = FontWeight.W400,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DetailsTabContent(
    tabs: List<DetailsTabItems>, pagerState: PagerState
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen()
    }
}
