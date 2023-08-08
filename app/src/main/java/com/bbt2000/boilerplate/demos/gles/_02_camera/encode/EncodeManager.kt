package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.os.Handler
import android.os.HandlerThread
import android.view.Surface

/**
 *  author : sz
 *  date : 2023/8/8
 *  description :
 */
class EncodeManager(videoEnabled: Boolean = false, audioEnabled: Boolean = false, width: Int = 0, height: Int = 0) {
    private val mVideoEnabled = videoEnabled
    private val mAudioEnabled = audioEnabled

    private var mVideoEncoder: VideoEncoder? = null
    private var mAudioEncoder: AudioEncoder? = null
    private var mMp4Muxer: Mp4Muxer? = null

    private val mEncodeThread: HandlerThread by lazy { HandlerThread("encode").apply { start() } }
    private val mEncodeHandler: Handler by lazy { Handler(mEncodeThread.looper) }

    private var mVideoEncodeState: EncodeState = EncodeState.STOPPED
    private var mAudioEncodeState: EncodeState = EncodeState.STOPPED
    private var mEncodeState: EncodeState = EncodeState.STOPPED // 总体状态
    private var mStateCallback: ((state: EncodeState) -> Unit)? = null

    fun getEncodeState(): EncodeState = mEncodeState

    fun setStateCallback(callback: (state: EncodeState) -> Unit) {
        mStateCallback = callback
    }

    init {
        mMp4Muxer = Mp4Muxer(videoEnabled = videoEnabled, audioEnabled = audioEnabled)
        if (mVideoEnabled) {
            mVideoEncoder = VideoEncoder(width, height, mEncodeHandler)
            mVideoEncoder?.setMuxer(mMp4Muxer!!)
            mVideoEncoder?.setStateCallback {
                when (it) {
                    EncodeState.STOPPED -> {
                        mVideoEncodeState = EncodeState.STOPPED
                        if (!mAudioEnabled || mAudioEncodeState == EncodeState.STOPPED) {
                            mEncodeState = EncodeState.STOPPED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }

                    EncodeState.STARTED -> {
                        mVideoEncodeState = EncodeState.STARTED
                        if (!mAudioEnabled || mAudioEncodeState == EncodeState.STARTED) {
                            mEncodeState = EncodeState.STARTED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }

                    EncodeState.PAUSED -> {
                        mVideoEncodeState = EncodeState.PAUSED
                        if (!mAudioEnabled || mAudioEncodeState == EncodeState.PAUSED) {
                            mEncodeState = EncodeState.PAUSED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }
                }
            }
        }
        if (mAudioEnabled) {
            mAudioEncoder = AudioEncoder(mEncodeHandler)
            mAudioEncoder?.setMuxer(mMp4Muxer!!)
            mAudioEncoder?.setStateCallback {
                when (it) {
                    EncodeState.STOPPED -> {
                        mAudioEncodeState = EncodeState.STOPPED
                        if (!mVideoEnabled || mVideoEncodeState == EncodeState.STOPPED) {
                            mEncodeState = EncodeState.STOPPED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }

                    EncodeState.STARTED -> {
                        mAudioEncodeState = EncodeState.STARTED
                        if (!mVideoEnabled || mVideoEncodeState == EncodeState.STARTED) {
                            mEncodeState = EncodeState.STARTED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }

                    EncodeState.PAUSED -> {
                        mAudioEncodeState = EncodeState.PAUSED
                        if (!mVideoEnabled || mVideoEncodeState == EncodeState.PAUSED) {
                            mEncodeState = EncodeState.PAUSED
                            mStateCallback?.invoke(mEncodeState)
                        }
                    }
                }
            }
        }
    }

    fun start(callback: ((surface: Surface) -> Unit)? = null) {
        if (mVideoEnabled) {
            mVideoEncoder?.start(callback)
        }
        if (mAudioEnabled) {
            mAudioEncoder?.start()
        }
    }

    fun pause() {
        if (mVideoEnabled) {
            mVideoEncoder?.pause()
        }
        if (mAudioEnabled) {
            mAudioEncoder?.pause()
        }
    }

    fun resume() {
        if (mVideoEnabled) {
            mVideoEncoder?.resume()
        }
        if (mAudioEnabled) {
            mAudioEncoder?.resume()
        }
    }

    fun stop() {
        if (mVideoEnabled) {
            mVideoEncoder?.stop()
        }
        if (mAudioEnabled) {
            mAudioEncoder?.stop()
        }
    }

    fun release() {
        if (mVideoEnabled) {
            mVideoEncoder?.release()
        }
        if (mAudioEnabled) {
            mAudioEncoder?.release()
        }
        mEncodeThread.quitSafely()
    }
}