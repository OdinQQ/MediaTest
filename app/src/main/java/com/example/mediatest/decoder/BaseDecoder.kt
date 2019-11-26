package com.example.mediatest.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.text.TextUtils
import com.example.mediatest.extractor.IExtractor
import java.nio.ByteBuffer

/**
 * 解码器
 */
abstract class BaseDecoder : IDecoder, Runnable {

    var mPath: String = ""

    var mIsRunning = false

    var mMediaCodec: MediaCodec? = null

    var mExtractor: IExtractor? = null
    //
    var mInputBuffers: Array<ByteBuffer>? = null
    //
    var mOutputBuffers: Array<ByteBuffer>? = null

    var mIsEndOfStream: Boolean = false

    var mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

    var mState = DecodeState.STOP

    var mLock = Object()

    var mStateListener: DecoderStateListener? = null

    override var videoWidth: Int = 0

    override var videoHeight: Int = 0

    override var duration: Long = 0

    override var mediaFormat: MediaFormat? = null

    override var trackIndex: Int = -1

    override fun run() {
        mState = DecodeState.START
        if (!init()) {
            return
        }
        mIsRunning = true
        while (mIsRunning) {
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
                // 释放输出缓存
                mMediaCodec?.releaseOutputBuffer(index, true)
            }
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH
                mStateListener?.onFinish(this)
            }
        }
        release()
    }

    /**
     * 将缓存加入解码器
     */
    private fun pushBufferToDecoder(): Boolean {
        //
        val index = mMediaCodec?.dequeueInputBuffer(2000)
        var isEndOfStream = false
        if (index != null && index >= 0) {
            //
            val inputBuffer = mInputBuffers!![index]
            // 读取数据到 ByteBuffer
            val size = mExtractor?.readBuffer(inputBuffer)
            if (size != null) {
                if (size < 0) {
                    // 加入输入队列
                    mMediaCodec?.queueInputBuffer(
                        index, 0, 0, 0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                    isEndOfStream = true
                } else {
                    // 加入输入队列
                    mMediaCodec?.queueInputBuffer(
                        index, 0, size,
                        mExtractor!!.getCurrentTimestamp(), 0
                    )
                }
            }
        }
        return isEndOfStream
    }

    /**
     * 从解码器读取数据到缓存
     */
    private fun pullBufferFromDecoder(): Int {
        var index = mMediaCodec?.dequeueOutputBuffer(mBufferInfo, 1000)
        when (index) {
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                mOutputBuffers = mMediaCodec?.outputBuffers
            }
            else -> return index!!
        }
        return -1
    }

    private fun waitDecode() {
        synchronized(mLock) {
            mLock.wait()
        }
    }

    /**
     * 初始化操作
     */
    private fun init(): Boolean {
        if (mPath.isEmpty()) {
            mStateListener?.onError(this, "播放地址为空")
            return false
        }
        if (!check()) {
//            mStateListener?.onError(this, "")
            return false
        }
        if (!initExtractor()) {
//            mStateListener?.onError(this, "")
            return false
        }
        if (!initParams()) {
            return false
        }
        if (!initRender()) {
            return false
        }
        if (!initCodec()) {
            return false
        }
        return true
    }

    /**
     * 初始化分离器
     */
    fun initExtractor(): Boolean {
        mExtractor = buildExtractor(mPath)
        if (mExtractor == null) {
            return false
        }
        return true
    }

    /**
     * 初始化参数
     */
    fun initParams(): Boolean {
        val format = mExtractor?.getMediaFormat()
        duration = format?.getLong(MediaFormat.KEY_DURATION)?:0
        initMediaFormat(format)
        return true
    }

    /**
     * 初始化 MediaCodec
     */
    fun initCodec(): Boolean {
        val type = mExtractor?.getMediaFormat()?.getString(MediaFormat.KEY_MIME)
        if (TextUtils.isEmpty(type)) {
            return false
        } else {
            // 创建 MediaCodec
            mMediaCodec = MediaCodec.createDecoderByType(type!!)
            if (!configCodec(mMediaCodec, mExtractor?.getMediaFormat())) {
                waitDecode()
            }
            //
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

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contune() {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        mState = DecodeState.STOP
        mIsRunning = false
    }

    override fun isDecoding(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSeeked(): Boolean {
        return mState == DecodeState.SEEKED
    }

    override fun isStop(): Boolean {
        return mState == DecodeState.STOP
    }

    override fun setStateListener(listener: DecoderStateListener) {
        mStateListener = listener
    }

    abstract fun check(): Boolean

    abstract fun buildExtractor(path: String): IExtractor

    abstract fun initMediaFormat(mediaFormat: MediaFormat?)

    abstract fun initRender(): Boolean

    abstract fun configCodec(codec: MediaCodec?, format: MediaFormat?): Boolean

    abstract fun render(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)
}
