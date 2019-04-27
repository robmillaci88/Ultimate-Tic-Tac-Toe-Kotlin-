package com.example.robmillaci.ultimatetictactoe

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    internal var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        mMediaPlayer = MediaPlayer.create(this, R.raw.a_guy_1_epicbuilduploop)
        mMediaPlayer!!.setVolume(0.5f, 0.5f)
        mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.start()
    }

    override fun onPause() {
        super.onPause()
        mMediaPlayer!!.stop()
        mMediaPlayer!!.reset()
        mMediaPlayer!!.release()
    }
}
