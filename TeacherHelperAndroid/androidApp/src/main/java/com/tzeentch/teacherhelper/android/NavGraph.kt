package com.tzeentch.teacherhelper.android

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class MainSections(val destination: String) {
    object OptionSection : MainSections(MainSection.OPTION_ROUTE)
    object CameraSection : MainSections(MainSection.CAMERA_ROUTE)
}

@Composable
fun NavGraph(
    modifier: Modifier,
    startDestination: String = MainSections.CameraSection.destination
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        composable(route = MainSections.OptionSection.destination) {
            OptionScreen(navController = navController)
        }
        composable(route = MainSections.CameraSection.destination) {
            CameraScreen(navController = navController)
        }
    }
}
