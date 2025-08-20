package com.acnteam.crashybird.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.acnteam.crashybird.presentation.navigation.SetupNavGraph
import com.acnteam.crashybird.presentation.screen.splash.SplashViewModel
import com.acnteam.crashybird.presentation.ui.theme.CrashyBirdTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel: SplashViewModel by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            !viewModel.isLoading.value
        }
        enableEdgeToEdge()
        setContent {
            val startScreen by viewModel.startScreen.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            CrashyBirdTheme {
                SetupNavGraph(
                    navController = navController,
                    startDestination = startScreen,
                )
            }
        }
    }
}


