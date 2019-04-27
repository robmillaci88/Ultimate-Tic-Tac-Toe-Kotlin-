package com.example.robmillaci.ultimatetictactoe

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MainFragment : Fragment() {

    private var mDialog: AlertDialog? = null
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        createButtons()

        return rootView
    }

    private fun createButtons() {
        createAlertDialog()
        createNewAndContinueButtons()
    }

    private fun createNewAndContinueButtons() {
        val newButton : Button = rootView!!.findViewById(R.id.new_button)
        val continueButton: Button = rootView!!.findViewById(R.id.continue_button)

        newButton.setOnClickListener(View.OnClickListener {
            val newGameIntent = Intent(activity, GameActivity::class.java)
            startActivity(newGameIntent)
        })

        continueButton.setOnClickListener(View.OnClickListener {
            val continueIntent = Intent(activity, GameActivity::class.java)
            continueIntent.putExtra(GameActivity.KEY_RESTORE, true)
            startActivity(continueIntent)
        })
    }


    private fun createAlertDialog() {
        val aboutButton : Button = rootView!!.findViewById(R.id.about_button)
        aboutButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle(R.string.about_title)
                builder.setMessage(R.string.about_text)
                builder.setCancelable(false)
                builder.setPositiveButton(R.string.ok_label
                ) { dialog, which ->
                    //nothing
                }
                mDialog = builder.show()
            }
        })
    }


    override fun onPause() {
        super.onPause()

        //get rid of the about dialog if it is still visible.
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
    }
}
