package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class ActivityAddEditTask : AppCompatActivity() {

    companion object {
        const val EXTRA_TKID = "com.ebner.stundenplan.fragments.main.EXTRA_TKID"
        const val EXTRA_TKNAME = "com.ebner.stundenplan.fragments.main.EXTRA_TKNAME"
        const val EXTRA_TKNOTE = "com.ebner.stundenplan.fragments.main.EXTRA_TKNOTE"
        const val EXTRA_TKDATEDAY = "com.ebner.stundenplan.fragments.main.EXTRA_TKDATEDAY"
        const val EXTRA_TKDATEMONTH = "com.ebner.stundenplan.fragments.main.EXTRA_TKDATEMONTH"
        const val EXTRA_TKDATEYEAR = "com.ebner.stundenplan.fragments.main.EXTRA_TKDATEYEAR"
        const val EXTRA_TKLID = "com.ebner.stundenplan.fragments.main.EXTRA_TKLID"
        const val EXTRA_TKFINISHED = "com.ebner.stundenplan.fragments.main.EXTRA_TKFINISHED"
    }

    private var selectedLID: Int = -1
    private var selectedDateDay: Int = -1
    private var selectedDateMonth: Int = -1
    private var selectedDateYear: Int = -1
    private var selectedLday: Int = -1
    private var selectedLslid: Int = -1
    private var selectedLsid: Int = -1

    private lateinit var tietName: TextInputEditText
    private lateinit var tilName: TextInputLayout
    private lateinit var dropdownSid: AutoCompleteTextView
    private lateinit var tilSid: TextInputLayout
    private lateinit var dropdownSlid: AutoCompleteTextView
    private lateinit var tilSlid: TextInputLayout
    private lateinit var tietNote: TextInputEditText
    private lateinit var tilNote: TextInputLayout
    private lateinit var pbTask: ProgressBar
    private lateinit var btnDatepicker: Button
    private lateinit var btnCreateCalendar: Button
    private lateinit var cbFinished: MaterialCheckBox
    private lateinit var lessonViewModel: LessonViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)

        /*---------------------Link items to Layout--------------------------*/
        tietName = findViewById(R.id.tiet_task_tkname)
        tilName = findViewById(R.id.til_task_tkname)
        dropdownSid = findViewById(R.id.actv_dropdown_task_sid)
        tilSid = findViewById(R.id.til_dropdown_task_sid)
        dropdownSlid = findViewById(R.id.actv_dropdown_task_slid)
        tilSlid = findViewById(R.id.til_dropdown_task_slid)
        tietNote = findViewById(R.id.tiet_task_tknote)
        tilNote = findViewById(R.id.til_task_tknote)
        pbTask = findViewById(R.id.pb_task)
        btnDatepicker = findViewById(R.id.btn_datepicker)
        btnCreateCalendar = findViewById(R.id.btn_task_create_calendar)
        cbFinished = findViewById(R.id.cb_finished)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_TKID)) {
            title = getString(R.string.fragment_task) + " bearbeiten"
            //Save extras to vars
            val tkname = intent.getStringExtra(EXTRA_TKNAME)
            val tknote = intent.getStringExtra(EXTRA_TKNOTE)
            val tkdateday = intent.getIntExtra(EXTRA_TKDATEDAY, -1)
            val tkdatemonth = intent.getIntExtra(EXTRA_TKDATEMONTH, -1)
            val tkdateyear = intent.getIntExtra(EXTRA_TKDATEYEAR, -1)
            val tkfinished = intent.getBooleanExtra(EXTRA_TKFINISHED, false)
            val tklid = intent.getIntExtra(EXTRA_TKLID, -1)

            selectedLID = tklid
            selectedDateDay = tkdateday
            selectedDateMonth = tkdatemonth
            selectedDateYear = tkdateyear

            cbFinished.isChecked = tkfinished
            tietName.setText(tkname)
            tietNote.setText(tknote)

            //Fetch lists, and pass values to set in spinner etc.
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(selectedLID, selectedDateDay, selectedDateMonth, selectedDateYear)
            }

        } else {
            title = "Neue " + getString(R.string.fragment_task)

            //Fetch lists, and pass values to set in spinner etc.
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(-1, -1, -1, -1)
            }
        }

        btnCreateCalendar.setOnClickListener {
            createCalendarEntry()
        }

        tietName.addTextChangedListener {
            tilName.error = ""
        }
    }

    /*---------------------Create Calendar Entry for current Exam--------------------------*/
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun createCalendarEntry() {

        if (checkBeforeSave()) return

        val selectedSubjectName: String
        runBlocking {
            selectedSubjectName = lessonViewModel.singleLesson(selectedLID).subject.sname
        }

        //Pass the selected Date
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val dateString = "$selectedDateDay-${selectedDateMonth + 1}-$selectedDateYear 09:00:00"
        //formatting the dateString to convert it into a Date
        val date = sdf.parse(dateString)!!

        saveTask()

        val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "${tietName.text.toString()} | $selectedSubjectName")
                .putExtra(CalendarContract.Events.DESCRIPTION, tietNote.text.toString())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        startActivity(insertCalendarIntent)
    }


    /*---------------------Fetch Rooms and Teachers in another "thread" and set them to the spinner--------------------------*/
    @SuppressLint("SetTextI18n")
    private suspend fun fetchFromDatabase(lid: Int, tkdateday: Int, tkdatemonth: Int, tkdateyear: Int) {
        /*---------------------get access to subject table --------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)

        /*---------------------get the list with all items in subject and lesson--------------------------*/
        val subjectList = subjectViewModel.allSubjectList()
        val lessonList = lessonViewModel.allLessonList()

        val dropDownAdapterSubjects = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, subjectList)

        /*---------------------fill vars with selected id's--------------------------*/
        if (lid != -1) {
            val selectedLesson = lessonList.first { it.lid == lid }
            selectedLday = selectedLesson.lday
            selectedLslid = selectedLesson.lslid
            selectedLsid = selectedLesson.lsid
        }
        /*---------------------Set list of all subjects to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //Add the ArrayAdapter to the Dropdown menu
            dropdownSid.setAdapter(dropDownAdapterSubjects)


            //If edit task, or new task
            if (lid != -1) {
                //Set given subject ID to Dropdown menu
                val selectedSubject: Subject = subjectList.first { it.sid == selectedLsid }
                dropdownSid.setText(selectedSubject.sname, false)

                btnDatepicker.text = "$tkdateday.${tkdatemonth + 1}.$tkdateyear"

                showSelectDaysBySubject(selectedLsid)
                showSelectLessonsBySubjectDay(selectedLsid, selectedLday, selectedLslid)

            } else {
                showSelectDaysBySubject(selectedLsid)
            }

            //After 0,5s disable the progressbar
            delay(500)
            pbTask.visibility = View.INVISIBLE

            /*---------------------Listener, if Subject changed, and if true "days + lessons" below will be updated--------------------------*/
            dropdownSid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedLsid = (parent.adapter.getItem(position) as Subject).sid
                tilSid.error = ""

                //Subject changed, set selected day and schoollesson to -1
                selectedLday = -1
                selectedLslid = -1

                showSelectDaysBySubject(selectedLsid)
            }
        }
    }

    /**
     * Show available Days for this Subject, and maybe select day
     *
     * @param sid selected Subject --> Show all available days for this Subject
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    @SuppressLint("SetTextI18n")
    private fun showSelectDaysBySubject(sid: Int) {
        var datePickerDialog: DatePickerDialog
        val calendar = Calendar.getInstance()

        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        if (selectedDateDay != -1 && selectedDateMonth != -1 && selectedDateYear != -1) {
            year = selectedDateYear
            month = selectedDateMonth
            day = selectedDateDay
        }

        btnDatepicker.setOnClickListener {

            if (sid != -1) {

                datePickerDialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
                    val date = "$dayOfMonth.${monthOfYear + 1}.$year"
                    btnDatepicker.text = date
                    btnDatepicker.setBackgroundColor(getColor(R.color.colorPrimary))

                    val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    //Add 0 in front of all dates < 10
                    val date2 = LocalDate.parse("$year-${if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else monthOfYear + 1}-${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}", firstApiFormat)

                    showSelectLessonsBySubjectDay(sid, date2.dayOfWeek.value, -1)

                    selectedDateDay = dayOfMonth
                    selectedDateMonth = monthOfYear
                    selectedDateYear = year

                }, year, month, day)

                val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    val lessonBySubject = lessonViewModel.lessonBySubject(sid)

                    withContext(Dispatchers.Main) {

                        //https://www.freakyjolly.com/android-material-datepicker-and-timepicker-by-wdullaer-tutorial-by-example/#more-2649
                        // Setting Min Date to 1 year ago
                        val minDateC = Calendar.getInstance()
                        minDateC[Calendar.YEAR] = year - 1
                        datePickerDialog.minDate = minDateC

                        // Setting Max Date to next 2 years
                        val maxDateC = Calendar.getInstance()
                        maxDateC[Calendar.YEAR] = year + 2
                        datePickerDialog.maxDate = maxDateC

                        //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
                        var loopdate = minDateC
                        while (minDateC.before(maxDateC)) {
                            val dayOfWeek = loopdate[Calendar.DAY_OF_WEEK]
                            //IF dayOfWeek not in lessonBySubject.lday disable this day
                            //if (it.lday==7){1} else {it.lday+1} Calendar.Monday ist 2, und wenn it.lday montag is ist es dort 1, deswegen überall +1 und Sonntag wird auf 1 gesetzt
                            if (lessonBySubject.none {
                                        if (it.lday == 7) {
                                            1
                                        } else {
                                            it.lday + 1
                                        } == dayOfWeek
                                    }) {
                                val disabledDays = arrayOfNulls<Calendar>(1)
                                disabledDays[0] = loopdate
                                datePickerDialog.disabledDays = disabledDays
                            }
                            minDateC.add(Calendar.DATE, 1)
                            loopdate = minDateC
                        }

                        datePickerDialog.show(supportFragmentManager, "Datepickerdialog")
                    }


                }
            } else {
                btnDatepicker.isAllCaps = false
                btnDatepicker.text = "Bitte wähle zuerst ein Fach"
            }
        }

    }

    /**
     * Show available Lessons for this Subject with Day, and maybe select lesson
     *
     * @param sid selected Subject
     * @param day selected Day --> Show all available lessons for this Subject + Day
     * @param lesson if !=-1 set specific lesson as selected
     */
    private fun showSelectLessonsBySubjectDay(sid: Int, day: Int, lesson: Int) {
        val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val lessonSSlYBySubjectDayList = lessonViewModel.lessonBySubjectDay(sid, day)
            val lessonBySubjectDayList = lessonSSlYBySubjectDayList.mapIndexed { _, lessonSubjectSchoollessonYear -> lessonSubjectSchoollessonYear.schoolLesson }

            withContext(Dispatchers.Main) {
                val dropDownAdapterLessons = ArrayAdapter(this@ActivityAddEditTask, R.layout.dropdown_menu_popup_item, lessonBySubjectDayList)
                //Set available list of school lessons to Spinner
                dropdownSlid.setAdapter(dropDownAdapterLessons)

                //Select selected school lesson
                if (lesson != -1) {
                    val selectedSchoollesson = lessonBySubjectDayList.first { it.slid == lesson }
                    dropdownSlid.setText(selectedSchoollesson.toString(), false)
                }

                /*---------------------Listener, if Lesson changed, and if true selectedLID will be updated--------------------------*/
                dropdownSlid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                    selectedLslid = (parent.adapter.getItem(position) as SchoolLesson).slid
                    tilSlid.error = ""

                    CoroutineScope(Dispatchers.IO).launch {
                        val lessons = lessonViewModel.lessonbySubjectDaySchoollesson(sid, day, selectedLslid)

                        val selectedLesson = lessons.singleOrNull()

                        if (selectedLesson != null) {
                            selectedLID = selectedLesson.lid
                        } else {
                            withContext(Dispatchers.Main) {}
                            Toast.makeText(this@ActivityAddEditTask, "no or more than 1 entry was found ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveTask() {

        if (checkBeforeSave()) return

        val tkname = tietName.text.toString()
        val tknote = tietNote.text.toString()
        val tkfinished = cbFinished.isChecked

        val data = Intent()
        data.putExtra(EXTRA_TKNAME, tkname)
        data.putExtra(EXTRA_TKNOTE, tknote)
        data.putExtra(EXTRA_TKDATEDAY, selectedDateDay)
        data.putExtra(EXTRA_TKDATEMONTH, selectedDateMonth)
        data.putExtra(EXTRA_TKDATEYEAR, selectedDateYear)
        data.putExtra(EXTRA_TKFINISHED, tkfinished)
        data.putExtra(EXTRA_TKLID, selectedLID)

        val id = intent.getIntExtra(EXTRA_TKID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_TKID, id)
        }
        setResult(Activity.RESULT_OK, data)
        finish()

    }

    @SuppressLint("SetTextI18n")
    private fun checkBeforeSave(): Boolean {
        //false = everything is ok
        //true = input is mising
        var error = false
        /*---------------------If EditText is empty return error--------------------------*/
        if (TextUtils.isEmpty(tietName.text.toString())) {
            tilName.error = "Gib einen Namen ein!"
            error = true
        }
        if (selectedLsid == -1) {
            tilSid.error = "Bitte wähle ein Fach"
            error = true
        }
        if (selectedLslid == -1) {
            tilSlid.error = "Bitte wähle eine Schulstunde"
            error = true
        }
        if (selectedDateDay == -1 || selectedDateMonth == -1 || selectedDateYear == -1) {
            btnDatepicker.text = "Bitte wähle ein Datum"
            btnDatepicker.isAllCaps = false
            btnDatepicker.setBackgroundColor(getColor(R.color.red_400))
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
                saveTask()
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