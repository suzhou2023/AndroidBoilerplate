package com.bbt2000.boilerplate.demos.gles.camera_gl

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.view.Surface
import java.io.IOException

/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class Player(context: Context, surface: Surface, sizeChangedListener: OnVideoSizeChangedListener) {
    private var mMediaPlayer: MediaPlayer? = null

    init {
        initMediaPlayer(context, surface, sizeChangedListener)
    }

    private fun initMediaPlayer(
        context: Context,
        surface: Surface,
        sizeChangedListener: OnVideoSizeChangedListener
    ) {
        mMediaPlayer = MediaPlayer()
        try {
            val afd = context.assets.openFd("car_race.mp4")
            mMediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            //            String path = "http://192.168.1.254:8192";
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.setDataSource(TextureViewMediaActivity.videoPath);
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.setOnVideoSizeChangedListener(sizeChangedListener)
        mMediaPlayer!!.setSurface(surface)
        mMediaPlayer!!.prepareAsync()
        mMediaPlayer!!.setOnPreparedListener { mediaPlayer -> mediaPlayer.start() }
        mMediaPlayer!!.setVolume(0.1f, 0.1f)
    }
}
