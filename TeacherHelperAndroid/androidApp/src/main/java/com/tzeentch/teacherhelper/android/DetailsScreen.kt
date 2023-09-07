package com.tzeentch.teacherhelper.android

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    presenter.getRequests(id = "")
    when(val result = presenter.detailsState.collectAsState().value) {
        is DetailsUiState.ReceiveTask -> {
            DetailsScreen(
                requestId = requestId,
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
    requestId: String,
    detailsDto: DetailsDto
) {

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val listOfDetailsTabs = listOf(
        DetailsTabItems.DetailsTab1 {
            DetailsTab1()
        },
        DetailsTabItems.DetailsTab2 {
            DetailsTab2()
        },
        DetailsTabItems.DetailsTab3 {
            DetailsTab3()
        },
        DetailsTabItems.DetailsTab4 {
            DetailsTab4()
        },
        DetailsTabItems.DetailsTab5 {
            DetailsTab5()
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(id = R.string.login_title),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W600
                            )
                        }
                    },
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

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.Black
            )
        }
    ) {
        tabs.forEachIndexed { index, detailsTabItems ->
            Tab(
                text = { Text(text = detailsTabItems.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    Log.e("TAG", "TabPicker: CLICKED!")
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
