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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        onCardClicked = {},
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
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val timer = remember {
            mutableFloatStateOf(5000f)
        }
        when (val result = presenter.mainState.collectAsState().value) {
            is MainUiState.ReceiveListOfTask -> {
                LaunchedEffect(key1 = result.requestList) {
                    var needTimerTask = false
                    result.requestList.forEach {
                        if (it.status == "process")
                            needTimerTask = true
                    }
                    if (needTimerTask) {
                        while (timer.floatValue > 0) {
                            delay(1000)
                            timer.floatValue -= 1000
                        }
                        presenter.updateAllJobs()
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(result.requestList.size) {
                        Card(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (result.requestList[it].status == "ready") onCardClicked(
                                    result.requestList[it].id
                                )
                            }) {
                            Text(text = result.requestList[it].status)
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
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(50.dp),
                onClick = { onScanButtonClicked() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
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
