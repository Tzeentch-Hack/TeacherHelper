package com.tzeentch.teacherhelper.android

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tzeentch.teacherhelper.presenters.MainPresenter
import com.tzeentch.teacherhelper.utils.MainUiState
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@Composable
fun OptionScreen(
    navController: NavController
) {
    val cameraPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navController.navigate(MainSections.CameraSection.destination)
            }
        }
    OptionScreen(
        onScanButtonClicked = { cameraPermission.launch(Manifest.permission.CAMERA) },
        onCardClicked = { id ->
            navController.navigate(
                MainSections.DetailsSection.destination
                    .replace("{$REQUEST_ID_KEY}", id)
            )
        },
        onErrorAction = { navController.navigate(AuthenticationSections.LoginSection.destination) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionScreen(
    onScanButtonClicked: () -> Unit,
    onCardClicked: (id: String) -> Unit,
    onErrorAction: () -> Unit,
    presenter: MainPresenter = koinInject()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.option_screen_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFFC9D1C8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF304040)
                )
            )
        },
        containerColor = Color(0xFF5B7065)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val result = presenter.mainState.collectAsState().value) {
                is MainUiState.ReceiveListOfTask -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(result.requestList.size) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp)
                                    .clickable {
                                        onCardClicked(
                                            result.requestList[it].id
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF304040)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (result.requestList[it].status != "Succeeded") {
                                        TypewriterText(texts = listOf(result.requestList[it].status))
                                    } else {
                                        Text(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            text = result.requestList[it].status,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFC9D1C8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is MainUiState.Error -> {
                    //onErrorAction()
                }

                is MainUiState.Loading -> {
                    RotatingProgressBar()
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(70.dp),
                    onClick = { onScanButtonClicked() },
                    backgroundColor = Color(0xFF304040),
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_event_note_24),
                        contentDescription = stringResource(
                            id = R.string.image_description
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TypewriterText(
    texts: List<String>,
) {
    var textIndex by remember {
        mutableIntStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }

    LaunchedEffect(
        key1 = texts,
    ) {
        while (textIndex < texts.size) {
            texts[textIndex].forEachIndexed { charIndex, _ ->
                textToDisplay = texts[textIndex]
                    .substring(
                        startIndex = 0,
                        endIndex = charIndex + 1,
                    )
                delay(160)
            }
            textIndex = (textIndex + 1) % texts.size
            delay(1000)
        }
    }

    Text(
        modifier = Modifier.padding(vertical = 8.dp),
        text = textToDisplay,
        fontSize = 24.sp,
        fontWeight = FontWeight.W400,
        color = Color(0xFFC9D1C8)
    )
}

@Preview
@Composable
fun OptionScreenPreview() {
    OptionScreen(
        onScanButtonClicked = {},
        onCardClicked = {},
        onErrorAction = {}
    )
}
