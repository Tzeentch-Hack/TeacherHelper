package com.tzeentch.teacherhelper.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun OptionScreen(
    navController: NavController
) {

    OptionScreen(
        onScanButtonClicked = { navController.navigate(MainSections.CameraSection.destination) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionScreen(
    onScanButtonClicked: () -> Unit
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
