package com.example.pianomemo.data.repository

import com.example.pianomemo.data.local.MusicInfo
import com.example.pianomemo.data.local.MusicInfoDao

class MusicInfoRepository(private val musicInfoDao: MusicInfoDao) {
    suspend fun saveMusicInfo(
        textOfMusic: String,
        textOfArtist: String,
        textOfGenre: String,
        textOfStyle: String,
        textOfMemo: String,
        numOfRightHand: Float,
        numOfLeftHand: Float
    ){
        val musicInfo = MusicInfo(
            nameOfMusic = textOfMusic,
            nameOfArtist = textOfArtist,
            nameOfJenre = textOfGenre,
            nameOfStyle = textOfStyle,
            nameOfMemo = textOfMemo,
            levelOfRightHand = numOfRightHand.toInt(),
            levelOfLeftHand = numOfLeftHand.toInt()
        )
        musicInfoDao.insertMusicInfo(musicInfo)
    }
    suspend fun getAllMusicInfo(): List<MusicInfo> {
        return musicInfoDao.getAllMusicInfo()
    }
    suspend fun deleteMusicInfo(musicInfo: MusicInfo) {
        musicInfoDao.deleteMusicInfo(musicInfo)
    }
    suspend fun updateMusicInfo(musicInfo: MusicInfo) {
        musicInfoDao.updateMusicInfo(musicInfo)
    }
}