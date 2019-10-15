package com.example.mediatest

interface DecoderStateListener {

    fun onPrepared()

    fun onPause()

    fun onRunning()
}