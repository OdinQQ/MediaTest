package com.example.mediatest

import android.media.MediaFormat

interface IDecoder {

    fun start()

    fun pause()

    fun contune()

    fun stop()

    fun isDecoding() : Boolean

    fun isSeeked() : Boolean

    fun isStop(): Boolean

    fun setStateListener(listener: DecoderStateListener)

    fun getWidth() : Int

    fun getHeight() : Int

    fun getDuration() : Long

    fun getMediaFormat() : MediaFormat


}
