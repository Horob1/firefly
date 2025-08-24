package com.acnteam.crashybird.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.acnteam.crashybird.presentation.screen.home.HomeScreen
import com.acnteam.crashybird.presentation.screen.home.HomeViewModel
import com.acnteam.crashybird.presentation.screen.play.PlayScreen
import com.acnteam.crashybird.presentation.screen.play.PlayViewModel
import com.acnteam.crashybird.presentation.screen.setting.SettingScreen
import com.acnteam.crashybird.presentation.screen.setting.SettingViewModel
import com.acnteam.crashybird.presentation.screen.user.UserScreen
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeScreen
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalAnimationApi
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: NavScreen,
    viewModel: NavViewModel,
) {

    val musicSettings by viewModel.musicSettings.collectAsStateWithLifecycle()
    BackgroundMusicPlayer(
        musicResId = musicSettings.music.music,
        isPlaying = musicSettings.isEnabled,
        volume = musicSettings.volume
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
    ) {
        composable<NavScreen.Home> {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable<NavScreen.Welcome> {
            val viewModel = koinViewModel<WelcomeViewModel>()
            WelcomeScreen(
                markDoneOnBoarding = { viewModel.markDoneOnBoarding() }
            )
        }
        composable<NavScreen.Play> {
            val viewModel = koinViewModel<PlayViewModel>()
            val arg = it.toRoute<NavScreen.Play>()
            PlayScreen(
                levelNumber = arg.levelNumber,
                characterName = arg.characterName,
                viewModel = viewModel,
                navController = navController
            )
        }
        composable<NavScreen.Setting> {
            val viewModel = koinViewModel<SettingViewModel>()
            SettingScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable<NavScreen.User> {
            UserScreen()
        }
    }
}