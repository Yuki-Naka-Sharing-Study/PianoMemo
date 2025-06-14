package com.example.pianomemo

import androidx.compose.runtime.getValue
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.pianomemo.data.local.MusicInfoDao
import com.example.pianomemo.data.local.MusicInfoDatabase
import com.example.pianomemo.data.remote.RetrofitInstance
import com.example.pianomemo.data.remote.SpotifyApiService
import com.example.pianomemo.data.repository.MusicInfoRepository
import com.example.pianomemo.screen.confirm.ConfirmScreen
import com.example.pianomemo.screen.onboarding.OnboardingScreen
import com.example.pianomemo.screen.record.RecordScreen
import com.example.pianomemo.screen.settings.SettingsScreen
import com.example.pianomemo.viewmodel.MusicInfoViewModel
import com.example.pianomemo.viewmodel.MusicInfoViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var musicInfoDao: MusicInfoDao
    private lateinit var repository: MusicInfoRepository
    private val dataStore by preferencesDataStore(name = "musicDataScreen")
    private lateinit var retrofitService: SpotifyApiService

    private val viewModel: MusicInfoViewModel by lazy {
        MusicInfoViewModelFactory(
            repository,
            musicInfoDao,
            dataStore
        ).create(MusicInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val database = Room.databaseBuilder(
            application,
            MusicInfoDatabase::class.java, "music_info_database"
        ).build()
        musicInfoDao = database.musicInfoDao()
        repository = MusicInfoRepository(musicInfoDao)
        retrofitService = RetrofitInstance.api

        setContent {
            MyApp(
                viewModel = viewModel,
                retrofitService = retrofitService
            )
        }
    }
}

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
        NavHost(
            navController = navController,
            startDestination = if (isFirstLaunchState.value == true)
                "onboarding" else "musicDataScreen",
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