package com.example.mediatest

import android.media.MediaCodec
import android.media.MediaFormat
import android.text.TextUtils
import java.nio.ByteBuffer

/**
 * 解码器
 */
abstract class BaseDecoder : IDecoder, Runnable {

    var mIsRunning = false

    var mMediaCodec: MediaCodec? = null

    var mExtractor: IExtractor? = null

    var mInputBuffers: Array<ByteBuffer>? = null

    var mOutputBuffers: Array<ByteBuffer>? = null

    var mIsEndOfStream: Boolean = false

    var mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

    var mState = DecodeState.STOP

    var mLock = Object()

    var mStateListener: DecoderStateListener? = null

    override fun run() {
        mState = DecodeState.START
        if (!init()) {
            return
        }
        mIsRunning = true
        while(mIsRunning) {
            if (needWait()) {
                waitDecode()
            }
            if (!mIsRunning || mState == DecodeState.STOP) {
                mIsRunning = false
                break
            }
            if (!mIsEndOfStream) {
                pushBufferToDecoder()
            }
            // 读取解码器数据
            val index = pullBufferFromDecoder()
            if (index >= 0) {
                // 渲染
                render(mOutputBuffers!![index], mBufferInfo)

                mMediaCodec?.releaseOutputBuffer(index, true)
            }
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH
            }
        }
        release()
    }

    private fun pushBufferToDecoder(): Boolean {
        val index = mMediaCodec?.dequeueInputBuffer(2000)
        var isEndOfStream = false
        if (index != null && index >= 0) {
            val inputBuffer = mInputBuffers!![index]
            //
            val size = mExtractor?.readBuffer(inputBuffer)
            if (size != null) {
                if (size < 0) {
                    mMediaCodec?.queueInputBuffer(index, 0,0,0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    isEndOfStream = true
                } else {
                    mMediaCodec?.queueInputBuffer(index, 0, size,
                        mExtractor!!.getCurrentTimestamp(), 0)
                }
            }
        }
        return isEndOfStream
    }

    private fun pullBufferFromDecoder(): Int {
        var index = mMediaCodec?.dequeueOutputBuffer(mBufferInfo, 1000)
        when(index) {
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                mOutputBuffers = mMediaCodec?.outputBuffers
            }
            else -> return index!!
        }
        return -1
    }

    abstract fun render(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)

    private fun waitDecode() {
        synchronized(mLock) {
            mLock.wait()
        }
    }

    private fun init(): Boolean {

        return true
    }

    abstract fun check(): Boolean

    fun initExtractor() {
//        mExtractor =
    }

    fun initParams(): Boolean {
        return true
    }

    abstract fun initRender(): Boolean

    fun initCodec(): Boolean {
        val type = mExtractor?.getMediaFormat()?.getString(MediaFormat.KEY_MIME)
        if (TextUtils.isEmpty(type)) {
            return false
        } else {
            mMediaCodec = MediaCodec.createDecoderByType(type!!)
            if (!configCodec(mMediaCodec, mExtractor?.getMediaFormat())) {
                waitDecode()
            }
            mMediaCodec?.start()
            mInputBuffers = mMediaCodec?.inputBuffers
            mOutputBuffers = mMediaCodec?.outputBuffers
        }
        return true
    }

    private fun needWait(): Boolean {
        return mState != DecodeState.START &&
                mState != DecodeState.DECODING &&
                mState != DecodeState.SEEKED
    }

    private fun release() {

    }

    abstract fun configCodec(codec: MediaCodec?, format: MediaFormat?): Boolean
}
