package com.example.mediatest.decoder

interface DecoderStateListener {

    fun onPrepared(decoder: BaseDecoder)

    fun onPause(decoder: BaseDecoder)

    fun onRunning(decoder: BaseDecoder)

    fun onError(decoder: BaseDecoder, msg: String)

    fun onFinish(decoder: BaseDecoder)
}