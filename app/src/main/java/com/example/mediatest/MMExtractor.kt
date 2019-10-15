package com.example.mediatest

import android.media.AudioTrack
import android.media.MediaExtractor
import android.media.MediaFormat

class MMExtractor(path: String) {

    var mMediaExtractor: MediaExtractor? = null

    var mAudioTrack = -1

    var mAudioFormat: MediaFormat? = null

    var mVideoTrack = -1

    var mVideoFormat: MediaFormat? = null

    init {
        mMediaExtractor = MediaExtractor()
        mMediaExtractor?.setDataSource(path)
    }

    fun extract() {
        mAudioFormat = null
        mAudioTrack = -1
        mVideoFormat = null
        mVideoTrack = -1
        for (i in 0 until mMediaExtractor!!.trackCount){
            val mime = mMediaExtractor?.getTrackFormat(i)?.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                mAudioTrack = i
                mAudioFormat = mMediaExtractor?.getTrackFormat(i)
            } else if (mime?.startsWith("video/") == true) {

            }
        }
    }

    fun getAudioFormat() {

    }
}