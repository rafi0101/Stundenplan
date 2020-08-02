package com.ebner.stundenplan.fragments.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class ActivityAddEditTask : AppCompatActivity() {

    companion object {
        const val EXTRA_TKID = "com.ebner.stundenplan.fragments.main.EXTRA_TKID"
        const val EXTRA_TKNAME = "com.ebner.stundenplan.fragments.main.EXTRA_TKNAME"
        const val EXTRA_TKNOTE = "com.ebner.stundenplan.fragments.main.EXTRA_TKNOTE"
        const val EXTRA_TKLID = "com.ebner.stundenplan.fragments.main.EXTRA_TKLID"

    }

    var selectedLID: Int = -1

    private lateinit var tietName: TextInputEditText
    private lateinit var tilName: TextInputLayout
    private lateinit var spSid: MaterialSpinner
    private lateinit var mdpTask: MaterialDayPicker
    private lateinit var spSlid: MaterialSpinner
    private lateinit var tietNote: TextInputEditText
    private lateinit var tilNote: TextInputLayout
    private lateinit var pbTask: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }


        /*---------------------Link items to Layout--------------------------*/
        spSid = findViewById(R.id.sp_task_sid)
        spSlid = findViewById(R.id.sp_task_slid)
        tietName = findViewById(R.id.tiet_task_tkname)
        tilName = findViewById(R.id.til_task_tkname)
        tietNote = findViewById(R.id.tiet_task_tknote)
        tilNote = findViewById(R.id.til_task_tknote)
        mdpTask = findViewById(R.id.mdp_task_day)
        pbTask = findViewById(R.id.pb_task)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_TKID)) {
            title = getString(R.string.fragment_task) + " bearbeiten"
            //Save extras to vars
            val tkid = intent.getIntExtra(EXTRA_TKID, -1)
            val tkname = intent.getStringExtra(EXTRA_TKNAME)
            val tknote = intent.getStringExtra(EXTRA_TKNOTE)
            val tklid = intent.getIntExtra(EXTRA_TKLID, -1)


            selectedLID = tklid

            tietName.setText(tkname)
            tietNote.setText(tknote)

            //Fetch lists, and pass values to set in spinner etc.
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(tklid)
            }

        } else {
            title = "Neue " + getString(R.string.fragment_task)

            //Fetch lists, and pass values to set in spinner etc.
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(-1)
            }
        }
    }

    /*---------------------Fetch Rooms and Teachers in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase(lid: Int) {
        var selectedLday: Int = -1
        var selectedLslid: Int = -1
        var selectedLsid: Int = -1

        /*---------------------get access to room and teacher table --------------------------*/
        val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
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
            selectedLsid = subjectSid.getItem(1)!!
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

                showSelectDaysBySubject(selectedLsid, selectedLday)
                showSelectLessonsBySubjectDay(selectedLsid, selectedLday, -1)

            } else {
                showSelectDaysBySubject(selectedLsid, -1)
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

                    //Subject changed, set selected day and schoollesson to -1
                    selectedLday = -1
                    selectedLslid = -1

                    showSelectDaysBySubject(selectedLsid, -1)
                }
            }

            /*---------------------Listener, if Day selection change, and if true "lessons" will be updated--------------------------*/
            mdpTask.daySelectionChangedListener = object : MaterialDayPicker.DaySelectionChangedListener {
                override fun onDaySelectionChanged(selectedDays: List<MaterialDayPicker.Weekday>) {

                    if (selectedDays.isEmpty()) {
                        spSlid.getSpinner().adapter = null
                        return
                    }

                    val selectedDay = selectedDays.single()

                    selectedLday = when (selectedDay) {
                        MaterialDayPicker.Weekday.MONDAY -> 1
                        MaterialDayPicker.Weekday.TUESDAY -> 2
                        MaterialDayPicker.Weekday.WEDNESDAY -> 3
                        MaterialDayPicker.Weekday.THURSDAY -> 4
                        MaterialDayPicker.Weekday.FRIDAY -> 5
                        MaterialDayPicker.Weekday.SATURDAY -> 6
                        MaterialDayPicker.Weekday.SUNDAY -> 7
                        else -> -1
                    }
                    showSelectLessonsBySubjectDay(selectedLsid, selectedLday, selectedLslid)
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
    private fun showSelectDaysBySubject(sid: Int, selected: Int) {
        val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val lessonBySubject = lessonViewModel.lessonBySubject(sid)

            withContext(Dispatchers.Main) {
                mdpTask.disableAllDays()
                lessonBySubject.forEach {
                    //Enable all Available Days
                    when (it.lday) {
                        1 -> mdpTask.enableDay(MaterialDayPicker.Weekday.MONDAY)
                        2 -> mdpTask.enableDay(MaterialDayPicker.Weekday.TUESDAY)
                        3 -> mdpTask.enableDay(MaterialDayPicker.Weekday.WEDNESDAY)
                        4 -> mdpTask.enableDay(MaterialDayPicker.Weekday.THURSDAY)
                        5 -> mdpTask.enableDay(MaterialDayPicker.Weekday.FRIDAY)
                        6 -> mdpTask.enableDay(MaterialDayPicker.Weekday.SATURDAY)
                        7 -> mdpTask.enableDay(MaterialDayPicker.Weekday.SUNDAY)
                    }
                }

                //Select selected Day
                when (selected) {
                    1 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.MONDAY)
                    2 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.TUESDAY)
                    3 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.WEDNESDAY)
                    4 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.THURSDAY)
                    5 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.FRIDAY)
                    6 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.SATURDAY)
                    7 -> mdpTask.setSelectedDays(MaterialDayPicker.Weekday.SUNDAY)
                    else -> mdpTask.clearSelection()
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

        var error = false
        /*---------------------If EditText is empty return error--------------------------*/
        if (TextUtils.isEmpty(tietName.text.toString())) {
            tilName.error = "Gib einen Namen ein!"
            error = true
        }

        if (error) return


        val tkname = tietName.text.toString()
        val tknote = tietNote.text.toString()


        val data = Intent()
        data.putExtra(EXTRA_TKNAME, tkname)
        data.putExtra(EXTRA_TKNOTE, tknote)
        data.putExtra(EXTRA_TKLID, selectedLID)

        val id = intent.getIntExtra(EXTRA_TKID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_TKID, id)
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