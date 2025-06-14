package com.example.pianomemo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_info")
data class MusicInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameOfMusic: String,
    val nameOfArtist: String,
    val nameOfJenre: String,
    val nameOfStyle: String,
    val nameOfMemo: String,
    val levelOfRightHand: Int,
    val levelOfLeftHand: Int,
)