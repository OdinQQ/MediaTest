package com.example.mediatest.extractor

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * 对官方 MediaExtractor 的封装
 */
class MMExtractor(path: String) {

    // MediaExtractor
    private var mMediaExtractor: MediaExtractor? = null

    var audioTrackIndex = NO_TRACK

    var audioFormat: MediaFormat? = null

    var videoTrackIndex = NO_TRACK

    var videoFormat: MediaFormat? = null

    var currentTimestamp: Long = 0

    init {
        mMediaExtractor = MediaExtractor()
        mMediaExtractor?.setDataSource(path)
    }

    companion object {
        const val NO_TRACK = -1
    }

    /**
     * 音视频分离操作
     */
    fun extract() {
        audioFormat = null
        audioTrackIndex = NO_TRACK
        videoFormat = null
        videoTrackIndex = NO_TRACK
        // 遍历轨道
        for (i in 0 until mMediaExtractor!!.trackCount) {
            if (audioTrackIndex != NO_TRACK && videoTrackIndex != NO_TRACK) {
                break
            }
            // 读取轨道信息
            val mediaFormat = mMediaExtractor?.getTrackFormat(i)
            val mime = mediaFormat?.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                if (audioTrackIndex == NO_TRACK) {
                    audioTrackIndex = i
                    audioFormat = mediaFormat
                }
            } else if (mime?.startsWith("video/") == true) {
                if (videoTrackIndex == NO_TRACK) {
                    videoTrackIndex = i
                    videoFormat = mediaFormat
                }
            }
        }
    }

    /**
     * 读取音视频数据到 ByteBuffer
     */
    private fun readBuffer(byteBuffer: ByteBuffer, index: Int): Int {
        byteBuffer.clear()
        mMediaExtractor?.selectTrack(index)
        // 读取当前采样数据
        var size: Int = mMediaExtractor?.readSampleData(byteBuffer, 0)?:-1
        if (size < 0) {
            return -1
        }
        // 更新时间戳
        currentTimestamp = mMediaExtractor?.sampleTime?:0
        // 前进到下一个采样
        mMediaExtractor?.advance()
        return size
    }

    fun readVideoBuffer(byteBuffer: ByteBuffer): Int {
        return readBuffer(byteBuffer, videoTrackIndex)
    }

    fun readAudioBuffer(byteBuffer: ByteBuffer): Int {
        return readBuffer(byteBuffer, audioTrackIndex)
    }

    /**
     * 跳转
     */
    fun seekTo(position: Long): Long? {
        /*
        * 通常只能 seek 到 I 帧，但是 I 帧通常和指定的播放位置有一定误差，因此需要指定 seek 靠近哪个关键帧
        *
        * SEEK_TO_PREVIOUS_SYNC：跳播位置的上一个关键帧
        * SEEK_TO_NEXT_SYNC：跳播位置的下一个关键帧
        * SEEK_TO_CLOSEST_SYNC：距离跳播位置的最近的关键帧
        * */
        mMediaExtractor?.seekTo(position, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
        return mMediaExtractor?.sampleTime
    }

    fun stop() {
        mMediaExtractor?.release()
        mMediaExtractor = null
    }

    fun setStartPosition(position: Long) {

    }
}