package com.example.robmillaci.ultimatetictactoe

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import java.util.*


class GameFragment : Fragment() {

    private var mEntireBoard = Tile(this)
    private val mLargeTiles = arrayOfNulls<Tile?>(9)
    private val mSmallTiles = Array<Array<Tile?>>(9) { arrayOfNulls(9) }
    private var mPlayer: Tile.Owner = Tile.Owner.X
    private val mAvailable = HashSet<Tile?>()
    private var mLastLarge: Int = 0
    private var mLastSmall: Int = 0

    private val mHandler = Handler()


    //Create a string containing the state of the game
    val state: String
        get() {
            val builder = StringBuilder()
            builder.append(mLastLarge).append(",")
            builder.append(mLastSmall).append(",")

            for (large in 0..8) {
                for (small in 0..8) {
                    builder.append(mSmallTiles[large][small]!!.mOwner.name)
                    builder.append(",")
                }
            }

            return builder.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        initGame()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.large_board, container, false)
        initViews(rootView)
        updateAllTiles()
        return rootView
    }


    fun initGame() {
        mEntireBoard = Tile(this)

        for (large in 0..8) {
            mLargeTiles[large] = Tile(this)
            for (small in 0..8) {
                mSmallTiles[large][small] = Tile(this)
            }

            mLargeTiles[large]!!.setSubTiles(mSmallTiles[large])
        }

        mEntireBoard.setSubTiles(mLargeTiles)

        //create a last move (this will be a fake last move when the game is initialized
        mLastSmall = -1
        mLastLarge = -1
        setAvailableFromLastMove(mLastSmall)
    }


    private fun initViews(rootView: View?) {
        mEntireBoard.mView = rootView
        for (large in 0..8) {
            val outer = rootView!!.findViewById<View>(mLargeIds[large])
            mLargeTiles[large]!!.mView = outer

            for (small in 0..8) {
                val inner = outer.findViewById<View>(mSmallIds[small]) as ImageButton
                val smallTile = mSmallTiles[large][small]
                smallTile!!.mView = inner
                inner.setOnClickListener {
                    if (isAvailable(smallTile)) {
                        makeMove(large, small)
                        think()
                    }
                }
            }
        }
    }

    private fun think() {
        (activity as GameActivity).startThinking()
        mHandler.postDelayed(Runnable {
            if (activity == null) return@Runnable
            if (mEntireBoard.mOwner == Tile.Owner.NEITHER) {
                val move = IntArray(2)
                pickMove(move)
                if (move[0] != -1 && move[1] != -1) {
                    switchTurns()
                    makeMove(move[0], move[1])
                    switchTurns()
                }
            }
            (activity as GameActivity).stopThinking()
        }, 1000)
    }

    private fun pickMove(move: IntArray) {
        val opponent = if (mPlayer == Tile.Owner.X) Tile.Owner.O else Tile.Owner.X
        var bestLarge = -1
        var bestSmall = -1
        var bestValue = Integer.MAX_VALUE

        for (large in 0..8) {
            for (small in 0..8) {
                val smallTile = mSmallTiles[large][small]
                if (isAvailable(smallTile)) {
                    //try the move and get the score
                    val newBoard = mEntireBoard.deepCopy()
                    newBoard.getSubTiles()?.get(large)?.getSubTiles()?.get(small)?.mOwner = opponent
                    val value = newBoard.evaluate()

                    if (value < bestValue) {
                        bestLarge = large
                        bestSmall = small
                        bestValue = value
                    }
                }
            }
        }
        move[0] = bestLarge
        move[1] = bestSmall
    }

    private fun makeMove(large: Int, small: Int) {
        mLastLarge = large
        mLastSmall = small
        val smallTile = mSmallTiles[large][small]
        val largeTile = mLargeTiles[large]
        smallTile?.mOwner = (mPlayer)

        val oldWinner = largeTile?.mOwner
        var winner: Tile.Owner = largeTile!!.findWinner()

        if (winner != oldWinner) {
            largeTile.mOwner = winner
        }

        setAvailableFromLastMove(large)


        winner = mEntireBoard.findWinner()
        mEntireBoard.mOwner = winner
        updateAllTiles()
        if (winner != Tile.Owner.NEITHER) {
            (activity as GameActivity).reportWinner(winner)
        }
    }


    private fun switchTurns() {
        mPlayer = if (mPlayer == Tile.Owner.X) Tile.Owner.O else Tile.Owner.X
    }


    fun restartGame() {
        initGame()
        initViews(view)
        //todo rotate tiles
        updateAllTiles()
    }

    private fun clearAvailable() {
        mAvailable.clear()
    }

    private fun addAvailable(tile: Tile) {
        mAvailable.add(tile)
    }

    fun isAvailable(tile: Tile?): Boolean {
        return mAvailable.contains(tile)
    }

    private fun setAvailableFromLastMove(large: Int) {
        clearAvailable()

        if (large != -1) {
            for (dest in 0..8) {
                val tile = mSmallTiles[large][dest]

                if (mLargeTiles[large]!!.mOwner == Tile.Owner.NEITHER) {
                    if (tile!!.mOwner == Tile.Owner.NEITHER) addAvailable(tile)
                }
            }
        }

        if (mAvailable.isEmpty()) {
            setAllAvailable()
        }
    }

    private fun setAllAvailable() {
        for (large in 0..8) {
            val largeTile = mLargeTiles[large]
            if (largeTile!!.mOwner == Tile.Owner.NEITHER) {
                for (small in 0..8) {
                    val tile = mSmallTiles[large][small]
                    if (tile!!.mOwner == Tile.Owner.NEITHER) {
                        addAvailable(tile)
                    }
                }
            }
        }
    }

    fun putState(gameData: String) {
        val fields = gameData.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray();
        var index = 0
        mLastLarge = Integer.parseInt(fields[index++])
        mLastSmall = Integer.parseInt(fields[index++])

        for (large in 0..8) {
            for (small in 0..8) {
                val owner = Tile.Owner.valueOf(fields[index++])
                mSmallTiles[large][small]!!.mOwner = owner
            }
        }

        setAvailableFromLastMove(mLastSmall)
        updateAllTiles()
    }



    private fun updateAllTiles() {
        mEntireBoard.updateDrawableState()
        for (large in 0..8) {
            mLargeTiles[large]!!.updateDrawableState()
            for (small in 0..8) {
                mSmallTiles[large][small]!!.updateDrawableState()
            }
        }
    }

    companion object {

        private val mLargeIds = intArrayOf(R.id.large1, R.id.large2, R.id.large3, R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9)
        private val mSmallIds = intArrayOf(R.id.small1, R.id.small2, R.id.small3, R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9)
    }
}
