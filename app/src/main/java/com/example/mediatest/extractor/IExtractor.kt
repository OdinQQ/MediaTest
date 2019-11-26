package com.example.mediatest.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * 音视频分离器
 */
interface IExtractor {

    fun getMediaFormat(): MediaFormat

    fun getCurrentTimestamp(): Long

    fun readBuffer(byteBuffer: ByteBuffer): Int

    fun seekTo(position: Long): Long

    fun setStartPosition(position: Long)

    fun stop()
}
