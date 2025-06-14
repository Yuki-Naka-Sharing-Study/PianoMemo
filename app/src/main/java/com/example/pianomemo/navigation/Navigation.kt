package com.example.pianomemo.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pianomemo.BuildConfig
import com.example.pianomemo.screen.confirm.ConfirmScreen
import com.example.pianomemo.screen.record.RecordScreen
import com.example.pianomemo.screen.settings.SettingsScreen
import com.example.pianomemo.viewmodel.MusicInfoViewModel
import com.example.pianomemo.data.remote.SpotifyApiService
import com.example.pianomemo.screen.onboarding.OnboardingScreen

@Composable
fun MyApp(
    viewModel: MusicInfoViewModel,
    retrofitService: SpotifyApiService
) {
    val navController = rememberNavController()
    val isFirstLaunchState = viewModel.isFirstLaunch.collectAsState(initial = null)

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isFirstLaunchState.value == false) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            viewModel = viewModel,
            retrofitService = retrofitService,
            innerPadding = innerPadding,
            isFirstLaunch = isFirstLaunchState.value ?: true
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: MusicInfoViewModel,
    retrofitService: SpotifyApiService,
    innerPadding: PaddingValues,
    isFirstLaunch: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isFirstLaunch) "onboarding" else "musicDataScreen",
        modifier = Modifier.padding(innerPadding)
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.completeOnboarding()
                    navController.navigate("musicDataScreen") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("musicDataScreen") {
            ConfirmScreen(viewModel = viewModel)
        }
        composable("musicRecordScreen") {
            RecordScreen(
                viewModel = viewModel,
                retrofitService = retrofitService,
                authToken = BuildConfig.AUTH_TOKEN
            )
        }
        composable("settingScreen") {
            SettingsScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedColor = Color(0xFF9C27B0)
    val unselectedColor = Color.Gray

    BottomNavigation(backgroundColor = Color.White) {
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "確認",
                    tint = if (currentRoute == "musicDataScreen") selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "確認",
                    color = if (currentRoute == "musicDataScreen") selectedColor else unselectedColor
                )
            },
            selected = currentRoute == "musicDataScreen",
            onClick = {
                if (currentRoute != "musicDataScreen") {
                    navController.navigate("musicDataScreen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "記録",
                    tint = if (currentRoute == "musicRecordScreen") selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "記録",
                    color = if (currentRoute == "musicRecordScreen") selectedColor else unselectedColor
                )
            },
            selected = currentRoute == "musicRecordScreen",
            onClick = {
                if (currentRoute != "musicRecordScreen") {
                    navController.navigate("musicRecordScreen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "設定",
                    tint = if (currentRoute == "settingScreen") selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "設定",
                    color = if (currentRoute == "settingScreen") selectedColor else unselectedColor
                )
            },
            selected = currentRoute == "settingScreen",
            onClick = {
                if (currentRoute != "settingScreen") {
                    navController.navigate("settingScreen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}