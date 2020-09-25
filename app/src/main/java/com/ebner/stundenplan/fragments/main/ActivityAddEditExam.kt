package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.examtype.ExamtypeViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
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
    private lateinit var examtypeViewModel: ExamtypeViewModel
    private lateinit var subjectViewModel: SubjectViewModel

    private lateinit var dropdownSid: AutoCompleteTextView
    private lateinit var tilSid: TextInputLayout
    private lateinit var dropdownEtid: AutoCompleteTextView
    private lateinit var tilSEtid: TextInputLayout
    private lateinit var tietGrade: TextInputEditText
    private lateinit var tilGrade: TextInputLayout
    private lateinit var btnDate: Button
    private lateinit var btnDateToday: Button
    private lateinit var btnCreateCalendar: Button
    private lateinit var pbExam: ProgressBar


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_exam)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        examtypeViewModel = ViewModelProvider(this).get(ExamtypeViewModel::class.java)
        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)


        /*---------------------Link items to Layout--------------------------*/
        dropdownSid = findViewById(R.id.actv_dropdown_exam_sid)
        tilSid = findViewById(R.id.til_dropdown_exam_sid)
        dropdownEtid = findViewById(R.id.actv_dropdown_exam_etid)
        tilSEtid = findViewById(R.id.til_dropdown_exam_etid)
        tietGrade = findViewById(R.id.tiet_exam_grade)
        tilGrade = findViewById(R.id.til_exam_grade)
        btnDate = findViewById(R.id.btn_exam_date)
        btnDateToday = findViewById(R.id.btn_exam_date_today)
        btnCreateCalendar = findViewById(R.id.btn_exam_create_calendar)
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
            selectedSID = sid
            selectedETID = etid

            btnDate.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"

        } else {
            title = "Neue " + getString(R.string.fragment_exam)

        }

        //Fetch subject and examtype list, and pass values to dropdown menu
        CoroutineScope(Dispatchers.IO).launch {
            fetchFromDatabase()
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        btnDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month
                selectedDay = dayOfMonth

                btnDate.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                btnDate.setBackgroundColor(getColor(R.color.colorPrimary))

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
            selectedMonth = month
            selectedDay = day
            btnDate.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"
            btnDate.setBackgroundColor(getColor(R.color.colorPrimary))
        }

        btnCreateCalendar.setOnClickListener {
            createCalendarEntry()
        }


    }


    /*---------------------Fetch Subjects and Examtypes in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase() {
        /*---------------------get the list with all items in room and teacher--------------------------*/
        val subjectList = subjectViewModel.allSubjectList()
        val examtypeList = examtypeViewModel.allExamtypeList()

        val dropDownAdapterSubjects = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, subjectList)
        val dropDownAdapterExamtypes = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, examtypeList)

        /*---------------------Set list of all rooms / teachers to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //Add the ArrayAdapter to the Dropdown
            dropdownSid.setAdapter(dropDownAdapterSubjects)
            dropdownEtid.setAdapter(dropDownAdapterExamtypes)

            //Preselect Dropdown menu
            if (selectedSID != -1) {
                val selectedSubject: Subject = subjectList.first { it.sid == selectedSID }
                dropdownSid.setText(selectedSubject.sname, false)

            }
            if (selectedETID != -1) {
                val selectedExamtype: Examtype = examtypeList.first { it.etid == selectedETID }
                dropdownEtid.setText(selectedExamtype.etname, false)
            }

            //After 0,5s disable the progressbar
            delay(500)
            pbExam.visibility = View.INVISIBLE

            //save new ID, when other item is chosen
            dropdownSid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedSID = (parent.adapter.getItem(position) as Subject).sid
                tilSid.error = ""
            }

            dropdownEtid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedETID = (parent.adapter.getItem(position) as Examtype).etid
                tilSEtid.error = ""
            }

        }
    }

    /*---------------------Create Calendar Entry for current Exam--------------------------*/
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun createCalendarEntry() {

        if (checkBeforeSave()) return

        //Get Subject and Examtype name
        val selectedSubjectName = dropdownSid.text.toString()
        val selectedExamtypeName = dropdownEtid.text.toString()

        //Pass the selected Date
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val dateString = "$selectedDay-${selectedMonth + 1}-$selectedYear 09:00:00"
        //formatting the dateString to convert it into a Date
        val date = sdf.parse(dateString)!!

        saveExam()

        val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "$selectedExamtypeName: $selectedSubjectName")
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        startActivity(insertCalendarIntent)
    }


    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveExam() {

        if (checkBeforeSave()) return

        var egrade = tietGrade.text.toString().toIntOrNull()
        if (egrade == null) egrade = -1

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

    @SuppressLint("SetTextI18n")
    private fun checkBeforeSave(): Boolean {
        //false = everything is ok
        //true = input is mising
        var error = false
        if (selectedYear == -1 || selectedMonth == -1 || selectedDay == -1) {
            btnDate.text = "Bitte wähle ein Datum"
            btnDate.isAllCaps = false
            btnDate.setBackgroundColor(getColor(R.color.red_400))
            error = true
        }
        if (selectedSID == -1) {
            tilSid.error = "Bitte wähle ein Fach"
            error = true
        }
        if (selectedETID == -1) {
            tilSEtid.error = "Bitte wähle eine Prüfungsart"
            error = true
        }
        return error
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