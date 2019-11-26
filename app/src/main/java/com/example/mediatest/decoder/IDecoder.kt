package com.example.mediatest.decoder

import android.media.MediaFormat

/***
 * 解码器接口
 */
interface IDecoder {

    /*----------------- 操作 -------------------*/

    fun start()

    fun pause()

    fun contune()

    fun stop()

    /*------------------- 状态 ---------------------*/

    fun isDecoding() : Boolean

    fun isSeeked() : Boolean

    fun isStop(): Boolean

    fun setStateListener(listener: DecoderStateListener)

    /*------------------- 信息 ---------------------*/

    var videoWidth: Int

    var videoHeight: Int

    var duration: Long

    var mediaFormat: MediaFormat?

    var trackIndex: Int
}
