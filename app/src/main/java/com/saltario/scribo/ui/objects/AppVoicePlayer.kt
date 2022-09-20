package com.saltario.scribo.ui.objects

import android.media.MediaPlayer
import com.saltario.scribo.database.getFileFromStorage
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.showToast
import java.io.File

class AppVoicePlayer {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile: File

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {

        // Если файл уже есть на телефоне
        mFile = File(APP_ACTIVITY.filesDir, messageKey)
        if (mFile.exists() && mFile.length() > 0 && mFile.isFile) {
            startPlay() {
                function()
            }
        // Если нету, то скачаем из БД
        } else {
            mFile.createNewFile()
            getFileFromStorage(mFile, fileUrl) {
                startPlay() {
                    function()
                }
            }
        }
    }

    // Проигрываем аудио
    private fun startPlay(function: () -> Unit) {

        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            // По окончанию останавливаем
            mMediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun stop(function: () -> Unit) {

        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun release(){
        mMediaPlayer.release()
    }

    fun init() {
        mMediaPlayer = MediaPlayer()
    }
}