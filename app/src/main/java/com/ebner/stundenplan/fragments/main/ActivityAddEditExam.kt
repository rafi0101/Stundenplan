package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.examtype.ExamtypeViewModel
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.util.*

class ActivityAddEditExam : AppCompatActivity() {

    companion object {
        const val EXTRA_EID = "com.ebner.stundenplan.fragments.main.EXTRA_EID"
        const val EXTRA_E_SID = "com.ebner.stundenplan.fragments.main.EXTRA_E_SID"
        const val EXTRA_E_ETID = "com.ebner.stundenplan.fragments.main.EXTRA_E_ETID"
        const val EXTRA_GRADE = "com.ebner.stundenplan.fragments.main.EXTRA_GRADE"
        const val EXTRA_DATEYEAR = "com.ebner.stundenplan.fragments.main.EXTRA_DATEYEAR"
        const val EXTRA_DATEMONTH = "com.ebner.stundenplan.fragments.main.EXTRA_DATEMONTH"
        const val EXTRA_DATEDAY = "com.ebner.stundenplan.fragments.main.EXTRA_DATEDAY"

    }

    var selectedSID: Int = -1
    var selectedETID: Int = -1

    private var selectedYear: Int = -1
    private var selectedMonth: Int = -1
    private var selectedDay: Int = -1


    private lateinit var spSid: MaterialSpinner
    private lateinit var spEtid: MaterialSpinner
    private lateinit var tietGrade: TextInputEditText
    private lateinit var tilGrade: TextInputLayout
    private lateinit var btnDate: Button
    private lateinit var btnDateToday: Button
    private lateinit var pbExam: ProgressBar


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_exam)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }


        /*---------------------Link items to Layout--------------------------*/
        spSid = findViewById(R.id.sp_exam_sid)
        spEtid = findViewById(R.id.sp_exam_etid)
        tietGrade = findViewById(R.id.tiet_exam_grade)
        tilGrade = findViewById(R.id.til_exam_grade)
        btnDate = findViewById(R.id.btn_exam_date)
        btnDateToday = findViewById(R.id.btn_exam_date_today)
        pbExam = findViewById(R.id.pb_exam)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_EID)) {
            title = getString(R.string.fragment_exam) + " bearbeiten"
            //Save extras to vars
            val sid = intent.getIntExtra(EXTRA_E_SID, -1)
            val etid = intent.getIntExtra(EXTRA_E_ETID, -1)
            val egrade = intent.getIntExtra(EXTRA_GRADE, -1)
            val edateyear = intent.getIntExtra(EXTRA_DATEYEAR, -1)
            val edatemonth = intent.getIntExtra(EXTRA_DATEMONTH, -1)
            val edateday = intent.getIntExtra(EXTRA_DATEDAY, -1)

            if (egrade != -1) {
                tietGrade.setText(egrade.toString())
            }
            selectedYear = edateyear
            selectedMonth = edatemonth
            selectedDay = edateday

            btnDate.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"

            //Fetch subject and examtype list, and pass values to set in spinner
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(sid, etid)
            }


        } else {
            title = "Neue " + getString(R.string.fragment_exam)


            //Fetch subject and examtype list, and pass values to set in spinner
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(1, 1)
            }
        }
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        btnDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month + 1
                selectedDay = dayOfMonth

                btnDate.text = "$selectedDay.$selectedMonth.$selectedYear"
            }, year, month, day)

            //If a date is already selected, select this in the datepicker
            if (selectedYear != -1 || selectedMonth != -1 || selectedDay != -1) {
                dpd.updateDate(selectedYear, selectedMonth, selectedDay)
            }
            dpd.show()

        }

        //easy way to set date to today
        btnDateToday.setOnClickListener {

            selectedYear = year
            selectedMonth = month + 1
            selectedDay = day
            btnDate.text = "$selectedDay.$selectedMonth.$selectedYear"
        }


    }


    /*---------------------Fetch Subjects and Examtypes in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase(sid: Int, etid: Int) {
        /*---------------------get access to room and teacher table --------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        val examtypeViewModel = ViewModelProvider(this).get(ExamtypeViewModel::class.java)

        /** ---------------------Create some simple Arrayadapters, to add each item...--------------------------
         * 2 Adapters for each Foreignkey, one for the Name to display, and one for the ID
         * For setting the item to the spinner:
         *  1. the *_*id Adapter is compared with the given id, and returns the position where the id is located
         *  2. this position is set to the spinner, so i get the correct name
         * For getting the selected id, it is vice versa
         */
        val subjectSname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val subjectSid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val examtypeEtname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val examtypeEtid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)


        /*---------------------get the list with all items in room and teacher--------------------------*/
        val subjectAll = subjectViewModel.allSubjectList()
        val examtypeAll = examtypeViewModel.allExamtypeList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        subjectAll.forEach {
            subjectSid.add(it.sid)
            subjectSname.add(it.sname)
        }

        examtypeAll.forEach {
            examtypeEtid.add(it.etid)
            examtypeEtname.add(it.etname)
        }

        /*---------------------Set list of all rooms / teachers to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //First Define some Properties
            spSid.setLabel(getString(R.string.fragment_subject))
            spEtid.setLabel(getString(R.string.fragment_examtype))

            // Set layout to use when the list of choices appear
            subjectSname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            examtypeEtname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            spSid.setAdapter(subjectSname)
            spEtid.setAdapter(examtypeEtname)

            //Set gived id's to spinner
            //How this works is explained a few lines above
            val selectedSubjectPos = subjectSid.getPosition(sid)
            selectedSubjectPos.let { spSid.getSpinner().setSelection(it) }

            val selectedExamtypePos = examtypeEtid.getPosition(etid)
            selectedExamtypePos.let { spEtid.getSpinner().setSelection(it) }

            //After 0,5s disable the progressbar
            delay(500)
            pbExam.visibility = View.INVISIBLE

            //Save current selected ID, because when nothing changed, id == -1, as declared on top
            selectedSID = sid
            selectedETID = etid

            //save new ID, when other item is choosen
            spSid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on room_rid
                    selectedSID = subjectSid.getItem(position)!!
                }
            }

            spEtid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on teacher_tid
                    selectedETID = examtypeEtid.getItem(position)!!
                }
            }


        }
    }


    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveExam() {

        var egrade = tietGrade.text.toString().toIntOrNull()
        if (egrade == null) egrade = -1

        if (selectedYear == -1 || selectedMonth == -1 || selectedDay == -1) {
            Toast.makeText(this, "select a date!", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        data.putExtra(EXTRA_E_SID, selectedSID)
        data.putExtra(EXTRA_E_ETID, selectedETID)
        data.putExtra(EXTRA_GRADE, egrade)
        data.putExtra(EXTRA_DATEYEAR, selectedYear)
        data.putExtra(EXTRA_DATEMONTH, selectedMonth)
        data.putExtra(EXTRA_DATEDAY, selectedDay)

        val id = intent.getIntExtra(EXTRA_EID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_EID, id)
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
                saveExam()
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