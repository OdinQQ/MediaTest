package com.example.mediatest.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 */
class VideoExtractor(path: String) : IExtractor {

    private val mExtractor = MMExtractor(path)

    init {
        mExtractor.extract()
    }

    override fun getMediaFormat(): MediaFormat {
        return mExtractor.videoFormat!!
    }

    override fun getCurrentTimestamp(): Long {
        return mExtractor.currentTimestamp
    }

    override fun readBuffer(byteBuffer: ByteBuffer): Int {
        return mExtractor.readVideoBuffer(byteBuffer)
    }

    override fun seekTo(position: Long): Long {
        return mExtractor.seekTo(position)!!
    }

    override fun setStartPosition(position: Long) {
        mExtractor.setStartPosition(position)
    }

    override fun stop() {
        mExtractor.stop()
    }
}
