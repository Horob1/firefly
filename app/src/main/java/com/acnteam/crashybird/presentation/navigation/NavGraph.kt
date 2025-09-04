package com.acnteam.crashybird.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.acnteam.crashybird.presentation.screen.home.HomeScreen
import com.acnteam.crashybird.presentation.screen.home.HomeViewModel
import com.acnteam.crashybird.presentation.screen.home.signin.SignInViewModel
import com.acnteam.crashybird.presentation.screen.play.PlayScreen
import com.acnteam.crashybird.presentation.screen.play.PlayViewModel
import com.acnteam.crashybird.presentation.screen.rank.RankScreen
import com.acnteam.crashybird.presentation.screen.rank.RankViewModel
import com.acnteam.crashybird.presentation.screen.rankdetail.RankDetailScreen
import com.acnteam.crashybird.presentation.screen.rankdetail.RankDetailViewModel
import com.acnteam.crashybird.presentation.screen.setting.SettingScreen
import com.acnteam.crashybird.presentation.screen.setting.SettingViewModel
import com.acnteam.crashybird.presentation.screen.user.UserScreen
import com.acnteam.crashybird.presentation.screen.user.UserViewModel
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeScreen
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.R)
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
        enterTransition = {
            scaleIn(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                ),
                initialScale = 0.6f,
                transformOrigin = TransformOrigin.Center
            ) + fadeIn(
                animationSpec = tween(250)
            )
        },
        exitTransition = {
            scaleOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ),
                targetScale = 0.85f,
                transformOrigin = TransformOrigin.Center
            ) + fadeOut(
                animationSpec = tween(200)
            )
        }
    ) {
        composable<NavScreen.Home> {
            val viewModel = koinViewModel<HomeViewModel>()
            val authViewModel = koinViewModel<SignInViewModel>()
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                authViewModel = authViewModel
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
            val viewModel = koinViewModel<UserViewModel>()
            UserScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onToRankingsPage = {
                    navController.navigate(NavScreen.Rank)
                },
                viewModel = viewModel
            )
        }
        composable<NavScreen.Rank> {
            val viewModel = koinViewModel<RankViewModel>()
            RankScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                onClick = { levelNumber ->
                    navController.navigate(NavScreen.RankDetail(levelNumber))
                }
            )
        }

        composable<NavScreen.RankDetail> {
            val viewModel = koinViewModel<RankDetailViewModel>()
            val arg = it.toRoute<NavScreen.RankDetail>()
            RankDetailScreen(
                viewModel = viewModel,
                levelNumber = arg.levelNumber
            )
        }
    }
}