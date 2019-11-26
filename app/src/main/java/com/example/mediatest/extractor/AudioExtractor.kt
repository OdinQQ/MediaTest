package com.example.mediatest.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

class AudioExtractor(path: String) : IExtractor {

    var mExtractor: MMExtractor =
        MMExtractor(path)

    override fun getMediaFormat(): MediaFormat {
        return mExtractor.audioFormat!!
    }

    override fun getCurrentTimestamp(): Long {
        return mExtractor.currentTimestamp
    }

    override fun readBuffer(byteBuffer: ByteBuffer): Int {
        return mExtractor.readAudioBuffer(byteBuffer)
    }

    override fun seekTo(position: Long): Long {
        return mExtractor.seekTo(position)!!
    }

    override fun setStartPosition(position: Long) {
        return mExtractor.setStartPosition(position)
    }

    override fun stop() {
        mExtractor.stop()
    }
}
