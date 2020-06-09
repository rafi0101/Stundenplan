package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class ActivityAddEditRoom : AppCompatActivity() {

    companion object {
        const val EXTRA_RID = "com.ebner.stundenplan.fragments.manage.EXTRA_RID"
        const val EXTRA_RNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_RNAME"
    }

    private lateinit var tietRname: TextInputEditText
    private lateinit var tilRname: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_room)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tietRname = findViewById(R.id.tiet_room_rname)
        tilRname = findViewById(R.id.til_room_rname)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_RID)) {
            title = getString(R.string.fragment_room) + " bearbeiten"
            tietRname.setText(intent.getStringExtra(EXTRA_RNAME))
        } else {
            title = "Neuer " + getString(R.string.fragment_room)
        }

        tietRname.addTextChangedListener {
            tilRname.error = ""
        }

    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveRoom() {

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tietRname.text.toString()) || TextUtils.getTrimmedLength(tietRname.text.toString()) == 0) {
            tilRname.error = "Gib einen Namen ein!"
            return

        }

        val rname = tietRname.text.toString()

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
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
