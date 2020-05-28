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

class ActivityAddEditTeacher : AppCompatActivity() {

    companion object {
        val EXTRA_TID = "com.ebner.stundenplan.fragments.manage.EXTRA_TID"
        val EXTRA_TNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_TNAME"
    }

    private lateinit var tiet_tname: TextInputEditText
    private lateinit var til_tname: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)


        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tiet_tname = findViewById(R.id.tiet_teacher_tname)
        til_tname = findViewById(R.id.til_teacher_tname)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_TID)) {
            title = getString(R.string.fragment_teacher) + " bearbeiten"
            tiet_tname.setText(intent.getStringExtra(EXTRA_TNAME))
        } else {
            title = "Neuer " + getString(R.string.fragment_teacher)
        }

        //Remove the error message, if user starts typing
        tiet_tname.addTextChangedListener {
            til_tname.error = ""
        }

    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveTeacher() {

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tiet_tname.text.toString()) || TextUtils.getTrimmedLength(tiet_tname.text.toString()) == 0) {
            til_tname.error = "Gib einen Namen ein!"
            return
        }

        val tname = tiet_tname.text.toString()

        val data = Intent()
        data.putExtra(EXTRA_TNAME, tname)
        val id = intent.getIntExtra(EXTRA_TID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_TID, id)
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
                saveTeacher()
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