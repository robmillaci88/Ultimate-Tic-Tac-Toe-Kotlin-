package com.example.robmillaci.ultimatetictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout

class GameActivity : AppCompatActivity() {
    private var mGameFragment: GameFragment? = null
    private var mMediaPlayer: MediaPlayer? = null
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        mGameFragment = supportFragmentManager.findFragmentById(R.id.fragment_game) as GameFragment?

        val restore = intent.getBooleanExtra(KEY_RESTORE, false)
        if (restore) {
            val gameData = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_RESTORE, null)
            if (gameData != null) {
                mGameFragment!!.putState(gameData)
            }
        }
    }


    internal fun restartGame() {
        mGameFragment!!.restartGame()
    }

    fun reportWinner(winner: Tile.Owner) {
        val builder = AlertDialog.Builder(this)

        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
        }

        builder.setMessage(getString(R.string.declare_winner, winner))
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.ok_label) { dialog, which -> finish() }
        val dialog = builder.create()

        mHandler.postDelayed({
            mMediaPlayer = MediaPlayer.create(this@GameActivity,
                    if (winner == Tile.Owner.X)
                        R.raw.oldedgar_winner
                    else if (winner == Tile.Owner.O)
                        R.raw.notr_loser
                    else
                        R.raw.department64_draw
            )
            mMediaPlayer!!.start()
            dialog.show()
        }, 500)

        mGameFragment!!.initGame()
    }


    fun startThinking() {
        val thinkView : FrameLayout = findViewById(R.id.thinking)
        thinkView.setVisibility(View.VISIBLE)
    }

    fun stopThinking() {
        val thinkView : FrameLayout= findViewById(R.id.thinking)
        thinkView.setVisibility(View.GONE)
    }


    override fun onResume() {
        super.onResume()
        mMediaPlayer = MediaPlayer.create(this, R.raw.frankum_loop001e)
        mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.start()
    }

    @SuppressLint("ApplySharedPref")
    override fun onPause() {
        super.onPause()

        mHandler.removeCallbacks(null)
        mMediaPlayer!!.stop()
        mMediaPlayer!!.reset()
        mMediaPlayer!!.release()

        val gameData = mGameFragment!!.state
        getPreferences(Context.MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit()
    }

    companion object {

        val KEY_RESTORE = "key_restore"
        val PREF_RESTORE = "pref_restore"
    }
}
