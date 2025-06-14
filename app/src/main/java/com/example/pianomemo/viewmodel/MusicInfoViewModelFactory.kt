package com.example.pianomemo.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pianomemo.data.local.MusicInfoDao
import com.example.pianomemo.data.repository.MusicInfoRepository

class MusicInfoViewModelFactory(
    private val repository: MusicInfoRepository,
    private val musicInfoDao: MusicInfoDao,
    private val dataStore: DataStore<Preferences>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicInfoViewModel(repository, musicInfoDao, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}