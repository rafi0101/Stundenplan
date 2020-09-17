package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ActivityAddEditTeacher : AppCompatActivity() {

    companion object {
        const val EXTRA_TID = "com.ebner.stundenplan.fragments.manage.EXTRA_TID"
        const val EXTRA_TNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_TNAME"
        const val EXTRA_TGENDER = "com.ebner.stundenplan.fragments.manage.EXTRA_TGENDER"
    }

    private val TAG = "debug_ActivityAddEditTeacher"

    var listOfGenders = arrayOf("Herr", "Frau")
    var selectedGender = -1

    private lateinit var tietTname: TextInputEditText
    private lateinit var tilTname: TextInputLayout
    private lateinit var spTeacherTgender: MaterialSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_teacher)


        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tietTname = findViewById(R.id.tiet_teacher_tname)
        tilTname = findViewById(R.id.til_teacher_tname)
        spTeacherTgender = findViewById(R.id.sp_teacher_tgender)

        /*---------------------Initialize Spinner--------------------------*/
        spTeacherTgender.setLabel("Geschlecht")
        val teacherGenderList = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfGenders)
        teacherGenderList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTeacherTgender.setAdapter(teacherGenderList)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_TID)) {
            title = getString(R.string.fragment_teacher) + " bearbeiten"
            tietTname.setText(intent.getStringExtra(EXTRA_TNAME))
            selectedGender = intent.getIntExtra(EXTRA_TGENDER, -1)
            spTeacherTgender.getSpinner().setSelection(selectedGender)
        } else {
            title = "Neuer " + getString(R.string.fragment_teacher)
        }

        //Remove the error message, if user starts typing
        tietTname.addTextChangedListener {
            tilTname.error = ""
        }

        spTeacherTgender.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                selectedGender = position
                Log.d(TAG, "position Gender: $position");
            }
        }

    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveTeacher() {

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tietTname.text.toString()) || TextUtils.getTrimmedLength(tietTname.text.toString()) == 0) {
            tilTname.error = "Gib einen Namen ein!"
            return
        }

        val tname = tietTname.text.toString()

        val data = Intent()
        data.putExtra(EXTRA_TNAME, tname)
        data.putExtra(EXTRA_TGENDER, selectedGender)
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