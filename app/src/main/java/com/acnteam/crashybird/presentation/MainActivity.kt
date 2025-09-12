package com.acnteam.crashybird.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.acnteam.crashybird.presentation.navigation.NavViewModel
import com.acnteam.crashybird.presentation.navigation.SetupNavGraph
import com.acnteam.crashybird.presentation.screen.splash.SplashViewModel
import com.acnteam.crashybird.presentation.ui.theme.CrashyBirdTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel: SplashViewModel by inject()

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            !viewModel.isLoading.value
        }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            val startScreen by viewModel.startScreen.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val navViewModel = koinViewModel<NavViewModel>()
            CrashyBirdTheme {
                SetupNavGraph(
                    navController = navController,
                    startDestination = startScreen,
                    viewModel = navViewModel
                )
            }
        }
    }
}


