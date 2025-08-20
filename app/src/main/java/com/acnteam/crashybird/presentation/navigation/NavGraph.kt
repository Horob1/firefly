package com.acnteam.crashybird.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.acnteam.crashybird.presentation.screen.home.HomeScreen
import com.acnteam.crashybird.presentation.screen.play.PlayScreen
import com.acnteam.crashybird.presentation.screen.setting.SettingScreen
import com.acnteam.crashybird.presentation.screen.user.UserScreen
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeScreen
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalAnimationApi
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: NavScreen,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
    ) {
        composable<NavScreen.Home> {
            HomeScreen(
                navController = navController
            )
        }
        composable<NavScreen.Welcome> {
            val viewModel = koinViewModel<WelcomeViewModel>()
            WelcomeScreen(
                markDoneOnBoarding = { viewModel.markDoneOnBoarding() }
            )
        }
        composable<NavScreen.Play> {
            PlayScreen()
        }
        composable<NavScreen.Setting> {
            SettingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<NavScreen.User> {
            UserScreen()
        }
    }
}