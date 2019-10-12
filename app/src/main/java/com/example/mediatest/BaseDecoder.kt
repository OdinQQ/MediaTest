package com.example.mediatest

import android.media.MediaCodec
import java.nio.ByteBuffer

abstract class BaseDecoder : IDecoder {

    var mMediaCodec: MediaCodec? = null

    var mExtractor: IExtractor? = null

    var mInputBuffers: Array<ByteBuffer>? = null

    var mOutputBuffers: Array<ByteBuffer>? = null

    var mIsEndOfStream: Boolean = false
}
