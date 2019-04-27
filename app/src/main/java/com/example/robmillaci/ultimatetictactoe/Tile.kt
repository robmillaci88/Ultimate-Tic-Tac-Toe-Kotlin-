package com.example.robmillaci.ultimatetictactoe

import android.view.View
import android.widget.ImageButton

class Tile (val game : GameFragment) {

    enum class Owner {
        X, O, NEITHER, BOTH
    }

    private val LEVEL_X = 0
    private val LEVEL_O = 1
    private val LEVEL_BLANK = 2
    private val LEVEL_AVAILABLE = 3
    private val LEVEL_TIE = 3

    var mOwner = Owner.NEITHER
    var mView: View? = null
    private var mSubTiles: Array<Tile?>? = null




    fun updateDrawableState() {
        if (mView == null) return
        val level = getLevel()
        if (mView!!.background != null) {
            mView!!.background.level = level
        }

        if (mView is ImageButton) {
            val drawable = (mView as ImageButton).drawable
            drawable.level = level
        }
    }


    private fun getLevel(): Int {
        var level = LEVEL_BLANK
        when (mOwner) {
            Tile.Owner.X -> level = LEVEL_X
            Tile.Owner.O -> level = LEVEL_O
            Tile.Owner.BOTH -> level = LEVEL_TIE
            Tile.Owner.NEITHER -> level = if (game.isAvailable(this)) LEVEL_AVAILABLE else LEVEL_BLANK
        }
        return level
    }


    fun findWinner(): Owner {
        if (mOwner != Owner.NEITHER)
            return mOwner

        val totalX = IntArray(4)
        val totalO = IntArray(4)
        countCaptures(totalX, totalO)

        if (totalX[3] > 0) return Owner.X
        if (totalO[3] > 0) return Owner.O

        //check for draw
        var total = 0
        for (row in 0..2) {
            for (col in 0..2) {
                val owner = mSubTiles!![3 * row + col]!!.mOwner
                if (owner != Owner.NEITHER) total++
            }

            if (total == 9) return Owner.BOTH
        }

        //Neither player has won this tile
        return Owner.NEITHER
    }


    fun getSubTiles(): Array<Tile?>? {
        return mSubTiles
    }

    fun setSubTiles(subTiles: Array<Tile?>?) {
        mSubTiles = subTiles
    }


    private fun countCaptures(totalX: IntArray, totalO: IntArray) {
        var capturedX: Int
        var capturedO: Int

        //check horizontal
        for (row in 0..2) {
            capturedO = 0
            capturedX = capturedO
            for (col in 0..2) {
                val owner = mSubTiles!![3 * row + col]!!.mOwner
                if (owner == Owner.X || owner == Owner.BOTH) capturedX++
                if (owner == Owner.O || owner == Owner.BOTH) capturedO++
            }
            totalX[capturedX]++
            totalO[capturedO]++
        }

        //check the vertical
        for (col in 0..2) {
            capturedO = 0
            capturedX = capturedO
            for (row in 0..2) {
                val owner = mSubTiles!![3 * row + col]!!.mOwner
                if (owner == Owner.X || owner == Owner.BOTH) capturedX++
                if (owner == Owner.O || owner == Owner.BOTH) capturedO++
            }
            totalX[capturedX]++
            totalO[capturedO]++
        }

        //check diags
        capturedO = 0
        capturedX = capturedO
        for (diag in 0..2) {
            val owner = mSubTiles!![3 * diag + diag]!!.mOwner
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++
        }
        totalX[capturedX]++
        totalO[capturedO]++
        capturedO = 0
        capturedX = capturedO
        for (diag in 0..2) {
            val owner = mSubTiles!![3 * diag + (2 - diag)]!!.mOwner
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++
        }
        totalX[capturedX]++
        totalO[capturedO]++
    }


    fun evaluate(): Int {
        when (mOwner) {
            Tile.Owner.X -> return 100
            Tile.Owner.O -> return -100
            Tile.Owner.NEITHER -> {
                var total = 0
                if (getSubTiles() != null) {
                    for (tile in 0..8) {
                        total += getSubTiles()!![tile]!!.evaluate()
                    }
                    val totalX = IntArray(4)
                    val totalO = IntArray(4)
                    countCaptures(totalX, totalO)
                    total = total * 100 + totalX[1] + 2 * totalX[2] + 8 * totalX[3] - totalO[1] - 2 * totalO[2] - 8 * totalO[3]


                }
                return total
            }
        }
        return 0
    }

    fun isNextOpponentMoveAWin(): Boolean {
        val subTiles = getSubTiles()
        var adjacentTile = 0
        for (tilePos in 0..8) {
            if (subTiles != null) {
                //check if any adjacent tiles horizontally
                if (tilePos < 2 || tilePos >= 3 && tilePos < 5 || tilePos >= 6 && tilePos < 8) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + 1]!!.mOwner) {
                        adjacentTile++
                    }
                }
                //check adjacent tiles vertically
                if (tilePos < 6) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + 3]!!.mOwner) {
                        adjacentTile++
                    }
                }

                //check diagonal
                if (tilePos == 0) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + 4]!!.mOwner) {
                        adjacentTile++
                    }
                }
                if (tilePos == 2) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + 2]!!.mOwner) {
                        adjacentTile++
                    }
                }

                if (tilePos == 6) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + -2]!!.mOwner) {
                        adjacentTile++
                    }
                }

                if (tilePos == 8) {
                    if (subTiles[tilePos]!!.mOwner != Owner.NEITHER && subTiles[tilePos]!!.mOwner == subTiles[tilePos + -4]!!.mOwner) {
                        adjacentTile++
                    }
                }
            }
        }

        return adjacentTile >= 2
    }

    fun deepCopy(): Tile {
        val tile= Tile(game)
        tile.mOwner = mOwner
        if (getSubTiles() != null) {

            val oldTiles = getSubTiles()
            val newTiles = Array<Tile?>(9, {t -> null})
            for (child in 0..8) {
                newTiles[child] = oldTiles!![child]!!.deepCopy()
            }
            tile.setSubTiles(newTiles)
        }

        return tile
    }

}

