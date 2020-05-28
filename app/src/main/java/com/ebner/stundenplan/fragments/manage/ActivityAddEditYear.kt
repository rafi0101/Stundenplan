package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ActivityAddEditYear : AppCompatActivity() {

    companion object {
        val EXTRA_YID = "com.ebner.stundenplan.fragments.manage.EXTRA_YID"
        val EXTRA_YNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_YNAME"
    }

    private lateinit var tiet_yname: TextInputEditText
    private lateinit var til_yname: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_year)


        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tiet_yname = findViewById(R.id.tiet_year_yname)
        til_yname = findViewById(R.id.til_year_yname)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_YID)) {
            title = getString(R.string.fragment_year) + " bearbeiten"
            tiet_yname.setText(intent.getStringExtra(EXTRA_YNAME))
        } else {
            title = "Neue " + getString(R.string.fragment_year)
        }


    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveYear() {

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tiet_yname.text.toString())) {
            til_yname.error = "Gib einen Namen ein!"
            return
        }

        val yname = tiet_yname.text.toString()

        val data = Intent()
        data.putExtra(EXTRA_YNAME, yname)
        val id = intent.getIntExtra(EXTRA_YID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_YID, id)
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
                saveYear()
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
