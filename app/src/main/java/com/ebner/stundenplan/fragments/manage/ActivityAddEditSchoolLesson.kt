package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ActivityAddEditSchoolLesson : AppCompatActivity() {

    companion object {
        const val EXTRA_SLID = "com.ebner.stundenplan.fragments.manage.EXTRA_SLID"
        const val EXTRA_SLNUMBER = "com.ebner.stundenplan.fragments.manage.EXTRA_SLNUMBER"
        const val EXTRA_SLSTARTHOUR = "com.ebner.stundenplan.fragments.manage.EXTRA_SLSTARTHOUR"
        const val EXTRA_SLSTARTMINUTE = "com.ebner.stundenplan.fragments.manage.EXTRA_SLSTARTMINUTE"
        const val EXTRA_SLENDHOUR = "com.ebner.stundenplan.fragments.manage.EXTRA_SLENDHOUR"
        const val EXTRA_SLENDMINUTE = "com.ebner.stundenplan.fragments.manage.EXTRA_SLENDMINUTE"
    }

    private var selectedStartHour = -1
    private var selectedStartMinute = -1
    private var selectedEndHour = -1
    private var selectedEndMinute = -1

    private lateinit var tietNumber: TextInputEditText
    private lateinit var tilNumber: TextInputLayout
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_school_lesson)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tietNumber = findViewById(R.id.tiet_schoollesson_number)
        tilNumber = findViewById(R.id.til_schoollesson_number)
        btnStart = findViewById(R.id.btn_schoollesson_start)
        btnEnd = findViewById(R.id.btn_schoollesson_end)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SLID)) {
            title = getString(R.string.fragment_schoollesson) + " bearbeiten"
            val slnumber = intent.getIntExtra(EXTRA_SLNUMBER, -1)
            val slstarthour = intent.getIntExtra(EXTRA_SLSTARTHOUR, -1)
            val slstartminute = intent.getIntExtra(EXTRA_SLSTARTMINUTE, -1)
            val slendhour = intent.getIntExtra(EXTRA_SLENDHOUR, -1)
            val slendminute = intent.getIntExtra(EXTRA_SLENDMINUTE, -1)

            selectedStartHour = slstarthour
            selectedStartMinute = slstartminute
            selectedEndHour = slendhour
            selectedEndMinute = slendminute

            btnStart.text = "$selectedStartHour:$selectedStartMinute"
            btnEnd.text = "$selectedEndHour:$selectedEndMinute"
            tietNumber.setText(slnumber.toString())
        } else {
            title = "Neue " + getString(R.string.fragment_schoollesson)
        }

        val cal = Calendar.getInstance()
        val calHourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val calMinute = cal.get(Calendar.MINUTE)

        //On Button click opens a TimePickerDialog, to select easy lesson time
        btnStart.setOnClickListener {
            val tpd = TimePickerDialog(this, { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                selectedStartHour = hourOfDay
                selectedStartMinute = minute

                val returnStartMinute = if (selectedStartMinute < 10) "0$selectedStartMinute" else "$selectedStartMinute"

                btnStart.text = "$selectedStartHour:$returnStartMinute"

                //Add 45 Minutes to Start Time, and detects if minute >= 60 and hour >=24
                selectedEndMinute = selectedStartMinute + 45
                //Hour is still the same
                selectedEndHour = selectedStartHour

                if (selectedEndMinute >= 60) {
                    selectedEndMinute -= 60
                    selectedEndHour = selectedStartHour + 1
                    if (selectedEndHour >= 24) selectedEndHour -= 24
                }

                //IF minute is less then 10, add a 0 in front of it (just for the view)
                val returnEndMinute = if (selectedEndMinute < 10) "0$selectedEndMinute" else "$selectedEndMinute"

                //Set calculated time to end button
                btnEnd.text = "$selectedEndHour:$returnEndMinute"

            }, calHourOfDay, calMinute, true)

            //If selected... is already set, go to selected time
            if (selectedStartHour != -1 || selectedStartMinute != -1) {
                tpd.updateTime(selectedStartHour, selectedStartMinute)
            }

            tpd.show()

        }

        //On Button click opens a TimePickerDialog, to select easy lesson time
        btnEnd.setOnClickListener {
            val tpd = TimePickerDialog(this, { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                selectedEndHour = hourOfDay
                selectedEndMinute = minute

                //IF minute is less then 10, add a 0 in front of it (just for the view)
                val returnEndMinute = if (selectedEndMinute < 10) "0$selectedEndMinute" else "$selectedEndMinute"
                btnEnd.text = "$selectedEndHour:$returnEndMinute"

            }, calHourOfDay, calMinute, true)

            //If selected... is already set, go to selected time
            if (selectedEndHour != -1 || selectedEndMinute != -1) {
                tpd.updateTime(selectedEndHour, selectedEndMinute)
            }

            tpd.show()
        }

        //Remove the error message, if user starts typing
        tietNumber.addTextChangedListener {
            tilNumber.error = ""
        }

    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveSchoolLesson() {
        var error = false

        /*---------------------If EditText is empty--------------------------*/
        if (TextUtils.isEmpty(tietNumber.text.toString()) || TextUtils.getTrimmedLength(tietNumber.text.toString()) == 0 || tietNumber.text.toString().toIntOrNull() == null) {
            tilNumber.error = "Gib eine Stunde ein!"
            error = true
        }
        if (selectedStartHour == -1 || selectedStartMinute == -1 || selectedEndHour == -1 || selectedEndMinute == -1) {
            Toast.makeText(this, "Stundenbeginn und ende fehlt!", Toast.LENGTH_LONG).show()
            error = true
        }
        if (error) return

        val number = tietNumber.text.toString().toInt()

        val data = Intent()
        data.putExtra(EXTRA_SLNUMBER, number)
        data.putExtra(EXTRA_SLSTARTHOUR, selectedStartHour)
        data.putExtra(EXTRA_SLSTARTMINUTE, selectedStartMinute)
        data.putExtra(EXTRA_SLENDHOUR, selectedEndHour)
        data.putExtra(EXTRA_SLENDMINUTE, selectedEndMinute)
        val id = intent.getIntExtra(EXTRA_SLID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_SLID, id)
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
                saveSchoolLesson()
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