package com.example.mediatest.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import com.example.mediatest.extractor.IExtractor
import com.example.mediatest.extractor.VideoExtractor
import java.nio.ByteBuffer

class VideoDecoder : BaseDecoder() {

    override fun check(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buildExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }

    override fun initMediaFormat(mediaFormat: MediaFormat?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initRender(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun configCodec(codec: MediaCodec?, format: MediaFormat?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
