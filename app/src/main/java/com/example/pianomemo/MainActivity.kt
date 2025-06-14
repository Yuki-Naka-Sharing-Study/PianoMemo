package com.example.pianomemo

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.pianomemo.data.local.MusicInfoDao
import com.example.pianomemo.data.local.MusicInfoDatabase
import com.example.pianomemo.data.remote.RetrofitInstance
import com.example.pianomemo.data.remote.SpotifyApiService
import com.example.pianomemo.data.repository.MusicInfoRepository
import com.example.pianomemo.navigation.MyApp
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