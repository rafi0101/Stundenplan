package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
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
        private val TAG = "debug_ActivityAddEditTask"
    }

    var selectedLID: Int = -1
    var selectedDateDay: Int = -1
    var selectedDateMonth: Int = -1
    var selectedDateYear: Int = -1

    private lateinit var tietName: TextInputEditText
    private lateinit var tilName: TextInputLayout
    private lateinit var spSid: MaterialSpinner
    private lateinit var spSlid: MaterialSpinner
    private lateinit var tietNote: TextInputEditText
    private lateinit var tilNote: TextInputLayout
    private lateinit var pbTask: ProgressBar
    private lateinit var btn_datepicker: Button
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
        spSid = findViewById(R.id.sp_task_sid)
        spSlid = findViewById(R.id.sp_task_slid)
        tietName = findViewById(R.id.tiet_task_tkname)
        tilName = findViewById(R.id.til_task_tkname)
        tietNote = findViewById(R.id.tiet_task_tknote)
        tilNote = findViewById(R.id.til_task_tknote)
        pbTask = findViewById(R.id.pb_task)
        btn_datepicker = findViewById(R.id.btn_datepicker)
        btnCreateCalendar = findViewById(R.id.btn_task_create_calendar)
        cbFinished = findViewById(R.id.cb_finished)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_TKID)) {
            title = getString(R.string.fragment_task) + " bearbeiten"
            //Save extras to vars
            val tkid = intent.getIntExtra(EXTRA_TKID, -1)
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
    private suspend fun fetchFromDatabase(lid: Int, tkdateday: Int, tkdatemonth: Int, tkdateyear: Int) {
        var selectedLday: Int = -1
        var selectedLslid: Int = -1
        var selectedLsid: Int = -1

        /*---------------------get access to room and teacher table --------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)

        /** ---------------------Create some simple Arrayadapters, to add each item...--------------------------
         * 2 Adapters for each Foreignkey, one for the Name to display, and one for the ID
         * For setting the item to the spinner:
         *  1. the *_*id Adapter is compared with the given id, and returns the position where the id is located
         *  2. this position is set to the spinner, so i get the correct name
         * For getting the selected id, it is vice versa
         */
        val subjectSname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val subjectSid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val lessonLday = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val lessonLslid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val lessonLsid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val lessonLid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)


        /*---------------------get the list with all items in room and teacher--------------------------*/
        val subjectAll = subjectViewModel.allSubjectList()
        val lessonAll = lessonViewModel.allLessonList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        subjectAll.forEach {
            subjectSname.add(it.sname)
            subjectSid.add(it.sid)
        }

        lessonAll.forEach {
            lessonLday.add(it.lday)
            lessonLslid.add(it.lslid)
            lessonLsid.add(it.lsid)
            lessonLid.add(it.lid)
        }

        /*---------------------fill vars with selected id's--------------------------*/
        if (lid != -1) {
            val posLid = lessonLid.getPosition(lid)
            selectedLday = lessonLday.getItem(posLid)!!
            selectedLslid = lessonLslid.getItem(posLid)!!
            selectedLsid = lessonLsid.getItem(posLid)!!
        } else {
            selectedLsid = subjectSid.getItem(0)!!
        }

        /*---------------------Set list of all subjects to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //First Define some Properties
            spSid.setLabel("Fach")
            spSlid.setLabel("Schulstunde")

            // Set layout to use when the list of choices appear
            subjectSname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            spSid.setAdapter(subjectSname)

            //If edit task, or new task
            if (lid != -1) {
                //Set given subject ID to Spinner
                val selectedSubjectPos = subjectSid.getPosition(selectedLsid)
                selectedSubjectPos.let { spSid.getSpinner().setSelection(it) }

                btn_datepicker.text = "$tkdateday.${tkdatemonth + 1}.$tkdateyear"

                showSelectDaysBySubject(selectedLsid)
                showSelectLessonsBySubjectDay(selectedLsid, selectedLday, -1)

            } else {
                showSelectDaysBySubject(selectedLsid)
            }

            //After 0,5s disable the progressbar
            delay(500)
            pbTask.visibility = View.INVISIBLE

            /*---------------------Listener, if Subject changed, and if true "days + lessons" below will be updated--------------------------*/
            spSid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on room_rid
                    selectedLsid = subjectSid.getItem(position)!!
                    Log.d(TAG, "spSid: selectedLsid: $selectedLsid")

                    //Subject changed, set selected day and schoollesson to -1
                    selectedLday = -1
                    selectedLslid = -1

                    showSelectDaysBySubject(selectedLsid)
                }
            }

        }
    }

    /**
     * Show available Days for this Subject, and maybe select day
     *
     * @param sid selected Subject --> Show all available days for this Subject
     * @param selected if !=-1 set specific day as selected
     */
    private fun showSelectDaysBySubject(sid: Int) {
        var datePickerDialog: DatePickerDialog
        val calendar = Calendar.getInstance()

        var Year = calendar.get(Calendar.YEAR)
        var Month = calendar.get(Calendar.MONTH)
        var Day = calendar.get(Calendar.DAY_OF_MONTH)

        if (selectedDateDay != -1 && selectedDateMonth != -1 && selectedDateYear != -1) {
            Year = selectedDateYear
            Month = selectedDateMonth
            Day = selectedDateDay
        }

        btn_datepicker.setOnClickListener {

            datePickerDialog = DatePickerDialog.newInstance(object : DatePickerDialog.OnDateSetListener {
                @SuppressLint("ResourceAsColor")
                override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    val date = "$dayOfMonth.${monthOfYear + 1}.$year"
                    btn_datepicker.text = date
                    btn_datepicker.setBackgroundColor(getColor(R.color.colorPrimary))

                    val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    //Add 0 in front of all dates < 10
                    val date2 = LocalDate.parse("$year-${if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else monthOfYear + 1}-${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}", firstApiFormat)

                    showSelectLessonsBySubjectDay(sid, date2.dayOfWeek.value, -1)

                    selectedDateDay = dayOfMonth
                    selectedDateMonth = monthOfYear
                    selectedDateYear = year

                }

            }, Year, Month, Day)

            Log.d(TAG, "Subject id: $sid")

            val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                val lessonBySubject = lessonViewModel.lessonBySubject(sid)

                withContext(Dispatchers.Main) {

                    //https://www.freakyjolly.com/android-material-datepicker-and-timepicker-by-wdullaer-tutorial-by-example/#more-2649
                    // Setting Min Date to 1 year ago
                    val min_date_c = Calendar.getInstance()
                    min_date_c[Calendar.YEAR] = Year - 1
                    datePickerDialog.minDate = min_date_c

                    // Setting Max Date to next 2 years
                    val max_date_c = Calendar.getInstance()
                    max_date_c[Calendar.YEAR] = Year + 2
                    datePickerDialog.maxDate = max_date_c

                    //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
                    var loopdate = min_date_c
                    while (min_date_c.before(max_date_c)) {
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
                        min_date_c.add(Calendar.DATE, 1)
                        loopdate = min_date_c
                    }

                    datePickerDialog.show(supportFragmentManager, "Datepickerdialog")
                }


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
        val schoollessonSlname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val schoollessonSlid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)

        schoollessonSlname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        CoroutineScope(Dispatchers.IO).launch {
            val lessonBySubjectDay = lessonViewModel.lessonBySubjectDay(sid, day)

            withContext(Dispatchers.Main) {

                schoollessonSlname.clear()
                schoollessonSlid.clear()
                lessonBySubjectDay.forEach {
                    val returnStartMinute = if (it.schoolLesson.slstartminute < 10) "0${it.schoolLesson.slstartminute}" else "${it.schoolLesson.slstartminute}"
                    val returnEndMinute = if (it.schoolLesson.slendminute < 10) "0${it.schoolLesson.slendminute}" else "${it.schoolLesson.slendminute}"

                    schoollessonSlname.add("${it.schoolLesson.slnumber}: ${it.schoolLesson.slstarthour}:$returnStartMinute - ${it.schoolLesson.slendhour}:$returnEndMinute")
                    schoollessonSlid.add(it.schoolLesson.slid)
                }
                //Set available list of school lessons to Spinner
                spSlid.setAdapter(schoollessonSlname)

                //Select selected school lesson
                if (lesson != -1) {
                    val selectedSchoollessonPos = schoollessonSlid.getPosition(lesson)
                    selectedSchoollessonPos.let { spSlid.getSpinner().setSelection(it) }
                }

                /*---------------------Listener, if Lesson changed, and if true selectedLID will be updated--------------------------*/
                spSlid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val slid = schoollessonSlid.getItem(position)!!

                        CoroutineScope(Dispatchers.IO).launch {
                            val lessons = lessonViewModel.lessonbySubjectDaySchoollesson(sid, day, slid)

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
        if (selectedDateDay == -1 || selectedDateMonth == -1 || selectedDateYear == -1) {
            btn_datepicker.text = "Bitte wähle ein Datum"
            btn_datepicker.isAllCaps = false
            btn_datepicker.setBackgroundColor(getColor(R.color.red_400))
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