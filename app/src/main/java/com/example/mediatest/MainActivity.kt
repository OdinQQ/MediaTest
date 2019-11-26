package com.example.mediatest

import android.media.MediaFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.mediatest.extractor.MMExtractor

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.txt)
//        var extractor =
//            MMExtractor("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
//        extractor.extract()
//        val videoMIME = extractor.videoFormat?.getString(MediaFormat.KEY_MIME)
//        val audioMIME = extractor.audioFormat?.getString(MediaFormat.KEY_MIME)
//        textView.text = videoMIME + "\n" + audioMIME
    }

    fun onClick(view : View) {

    }
}
