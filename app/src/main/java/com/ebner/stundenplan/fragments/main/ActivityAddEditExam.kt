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

    private val TAG = "debug_ActivityAddEditExam"

    companion object {
        val EXTRA_EID = "com.ebner.stundenplan.fragments.main.EXTRA_EID"
        val EXTRA_E_SID = "com.ebner.stundenplan.fragments.main.EXTRA_E_SID"
        val EXTRA_E_ETID = "com.ebner.stundenplan.fragments.main.EXTRA_E_ETID"
        val EXTRA_GRADE = "com.ebner.stundenplan.fragments.main.EXTRA_GRADE"
        val EXTRA_DATEYEAR = "com.ebner.stundenplan.fragments.main.EXTRA_DATEYEAR"
        val EXTRA_DATEMONTH = "com.ebner.stundenplan.fragments.main.EXTRA_DATEMONTH"
        val EXTRA_DATEDAY = "com.ebner.stundenplan.fragments.main.EXTRA_DATEDAY"

    }

    var selectedSID: Int = -1
    var selectedETID: Int = -1

    var selectedYear: Int = -1
    var selectedMonth: Int = -1
    var selectedDay: Int = -1


    private lateinit var sp_sid: MaterialSpinner
    private lateinit var sp_etid: MaterialSpinner
    private lateinit var tiet_grade: TextInputEditText
    private lateinit var til_grade: TextInputLayout
    private lateinit var btn_date: Button
    private lateinit var btn_date_today: Button
    private lateinit var pb_exam: ProgressBar


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
        sp_sid = findViewById(R.id.sp_exam_sid)
        sp_etid = findViewById(R.id.sp_exam_etid)
        tiet_grade = findViewById(R.id.tiet_exam_grade)
        til_grade = findViewById(R.id.til_exam_grade)
        btn_date = findViewById(R.id.btn_exam_date)
        btn_date_today = findViewById(R.id.btn_exam_date_today)
        pb_exam = findViewById(R.id.pb_exam)


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
                tiet_grade.setText(egrade.toString())
            }
            selectedYear = edateyear
            selectedMonth = edatemonth
            selectedDay = edateday

            btn_date.text = "$selectedDay.$selectedMonth.$selectedYear"

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

        btn_date.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month + 1
                selectedDay = dayOfMonth

                btn_date.text = "$selectedDay.$selectedMonth.$selectedYear"
            }, year, month, day)

            //If a date is already selected, select this in the datepicker
            if (selectedYear != -1 || selectedMonth != -1 || selectedDay != -1) {
                dpd.updateDate(selectedYear, selectedMonth, selectedDay)
            }
            dpd.show()

        }

        //easy way to set date to today
        btn_date_today.setOnClickListener {

            selectedYear = year
            selectedMonth = month + 1
            selectedDay = day
            btn_date.text = "$selectedDay.$selectedMonth.$selectedYear"
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
        val subject_sname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val subject_sid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val examtype_etname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val examtype_etid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)


        /*---------------------get the list with all items in room and teacher--------------------------*/
        val subject_all = subjectViewModel.allSubjectList()
        val examtype_all = examtypeViewModel.allExamtypeList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        subject_all.forEach {
            subject_sid.add(it.sid)
            subject_sname.add(it.sname)
        }

        examtype_all.forEach {
            examtype_etid.add(it.etid)
            examtype_etname.add(it.etname)
        }

        /*---------------------Set list of all rooms / teachers to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //First Define some Properties
            sp_sid.setLabel(getString(R.string.fragment_subject))
            sp_etid.setLabel(getString(R.string.fragment_examtype))

            // Set layout to use when the list of choices appear
            subject_sname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            examtype_etname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            sp_sid.setAdapter(subject_sname)
            sp_etid.setAdapter(examtype_etname)

            //Set gived id's to spinner
            //How this works is explained a few lines above
            val selectedSubjectPos = subject_sid.getPosition(sid)
            selectedSubjectPos.let { sp_sid.getSpinner().setSelection(it) }

            val selectedExamtypePos = examtype_etid.getPosition(etid)
            selectedExamtypePos.let { sp_etid.getSpinner().setSelection(it) }

            //After 0,5s disable the progressbar
            delay(500)
            pb_exam.visibility = View.INVISIBLE

            //Save current selected ID, because when nothing changed, id == -1, as declared on top
            selectedSID = sid
            selectedETID = etid

            //save new ID, when other item is choosen
            sp_sid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on room_rid
                    selectedSID = subject_sid.getItem(position)!!
                }
            }

            sp_etid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on teacher_tid
                    selectedETID = examtype_etid.getItem(position)!!
                }
            }


        }
    }


    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveExam() {

        var egrade = tiet_grade.text.toString().toIntOrNull()
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