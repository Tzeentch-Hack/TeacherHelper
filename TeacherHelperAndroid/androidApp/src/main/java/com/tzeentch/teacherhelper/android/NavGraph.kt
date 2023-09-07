package com.tzeentch.teacherhelper.android

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class AuthenticationSections(val destination: String) {
    object LoginSection : AuthenticationSections(AuthenticationSection.LOGIN_ROUTE)
}

sealed class MainSections(val destination: String) {
    object OptionSection : MainSections(MainSection.OPTION_ROUTE)
    object CameraSection : MainSections(MainSection.CAMERA_ROUTE)
    object DetailsSection : MainSections(MainSection.DETAILS_ROUTE)
}

sealed class DetailsTabItems(
    open val screen: @Composable () -> Unit,
    val title: String
) {
    class DetailsTab1(override val screen: @Composable () -> Unit) :
        DetailsTabItems({}, title = "Derails Tab 1")

    class DetailsTab2(override val screen: @Composable () -> Unit) :
        DetailsTabItems({}, title = "Derails Tab 2")

    class DetailsTab3(override val screen: @Composable () -> Unit) :
        DetailsTabItems({}, title = "Derails Tab 3")

    class DetailsTab4(override val screen: @Composable () -> Unit) :
        DetailsTabItems({}, title = "Derails Tab 4")

    class DetailsTab5(override val screen: @Composable () -> Unit) :
        DetailsTabItems({}, title = "Derails Tab 5")
}

@Composable
fun NavGraph(
    modifier: Modifier,
    startDestination: String = AuthenticationSections.LoginSection.destination
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
        composable(route = AuthenticationSections.LoginSection.destination) {
            LoginScreen(navController = navController)
        }
        composable(route = MainSections.OptionSection.destination) {
            OptionScreen(navController = navController)
        }
        composable(route = MainSections.CameraSection.destination) {
            CameraScreen(navController = navController)
        }
        composable(route = MainSections.DetailsSection.destination) { backStackArgument ->
            val requestId = backStackArgument.arguments?.getString(REQUEST_ID_KEY) ?: ""
            DetailsScreen(navController = navController, requestId = requestId)
        }
    }
}
