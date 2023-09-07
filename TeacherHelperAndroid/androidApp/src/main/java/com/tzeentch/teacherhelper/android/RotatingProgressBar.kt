package com.tzeentch.teacherhelper.android

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun RotatingProgressBar(
    modifier: Modifier = Modifier,
    rotationDurationMillis: Int = 2000
) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = rotationDurationMillis,
                easing = LinearEasing
            )
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Gray.copy(alpha = 0.1f)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Image(
            modifier = Modifier.rotate(rotation),
            painter = painterResource(id = R.drawable.ic_progress_bar),
            contentDescription = stringResource(id = R.string.image_description)
        )
    }
}