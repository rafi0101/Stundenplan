package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class ActivityAddEditRoom : AppCompatActivity() {
    private val TAG = "manuallog_ActivityAddEditRoom"

    companion object {
        val EXTRA_RID = "com.ebner.stundenplan.fragments.manage.EXTRA_RID"
        val EXTRA_RNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_RNAME"
    }

    private lateinit var tiet_rname: TextInputEditText
    private lateinit var til_rname: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_room)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            Log.d(TAG, "actionbar != null")
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tiet_rname = findViewById(R.id.tiet_room_rname)
        til_rname = findViewById(R.id.til_room_rname)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_RID)) {
            title = getString(R.string.fragment_room) + " bearbeiten"
            tiet_rname.setText(intent.getStringExtra(EXTRA_RNAME))
        } else {
            title = "Neuer " + getString(R.string.fragment_room)
        }

        tiet_rname.addTextChangedListener {
            til_rname.error = ""
        }

    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveRoom() {

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tiet_rname.text.toString()) || TextUtils.getTrimmedLength(tiet_rname.text.toString()) == 0) {
            til_rname.error = "Gib einen Namen ein!"
            return

        }

        val rname = tiet_rname.text.toString()

        val data = Intent()
        data.putExtra(EXTRA_RNAME, rname)
        val id = intent.getIntExtra(EXTRA_RID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_RID, id)
        }
        setResult(Activity.RESULT_OK, data)
        finish()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //Save Button
            R.id.nav_save -> {
                saveRoom()
                true
            }
            //Back Button
            android.R.id.home -> {
                super.onBackPressed()
                Log.d(TAG, "nav_home")
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
