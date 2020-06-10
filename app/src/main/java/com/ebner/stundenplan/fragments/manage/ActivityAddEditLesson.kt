package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonViewModel
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import kotlinx.coroutines.*

class ActivityAddEditLesson : AppCompatActivity() {


    companion object {
        const val EXTRA_LID = "com.ebner.stundenplan.fragments.manage.EXTRA_LID"
        const val EXTRA_LDAY = "com.ebner.stundenplan.fragments.manage.EXTRA_LDAY"
        const val EXTRA_L_SLID = "com.ebner.stundenplan.fragments.manage.EXTRA_L_SLID"
        const val EXTRA_L_SID = "com.ebner.stundenplan.fragments.manage.EXTRA_L_SID"
    }

    var selectedSLID: Int = -1
    var selectedSID: Int = -1

    private lateinit var mdp: MaterialDayPicker
    private lateinit var spSid: MaterialSpinner
    private lateinit var spSlid: MaterialSpinner
    private lateinit var pbLesson: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_lesson)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        mdp = findViewById(R.id.mdp_lesson_day)
        spSid = findViewById(R.id.sp_lesson_sid)
        spSlid = findViewById(R.id.sp_lesson_slid)
        pbLesson = findViewById(R.id.pb_lesson)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_LID)) {
            title = getString(R.string.fragment_lesson) + " bearbeiten"
            //Save extras to vars
            val lday = intent.getIntExtra(EXTRA_LDAY, -1)
            val lslid = intent.getIntExtra(EXTRA_L_SLID, -1)
            val lsid = intent.getIntExtra(EXTRA_L_SID, -1)

            when (lday) {
                1 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.MONDAY)
                2 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.TUESDAY)
                3 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.WEDNESDAY)
                4 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.THURSDAY)
                5 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.FRIDAY)
                6 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.SATURDAY)
                7 -> mdp.setSelectedDays(MaterialDayPicker.Weekday.SUNDAY)
            }

            //Fetch Lesson and SchoolLesson list, and pass values to set in spinner
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(lsid, lslid)
            }


        } else {
            title = "Neue " + getString(R.string.fragment_lesson)

            //Fetch Lesson and SchoolLesson list, and pass values to set in spinner
            CoroutineScope(Dispatchers.IO).launch {
                fetchFromDatabase(1, 1)
            }
        }

    }


    /*---------------------Fetch Subjects and SchoolLessons in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase(sid: Int, slid: Int) {
        /*---------------------get access to subject and schoolLesson table --------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        val schoolLessonViewModel = ViewModelProvider(this).get(SchoolLessonViewModel::class.java)

        /** ---------------------Create some simple Arrayadapters, to add each item...--------------------------
         * 2 Adapters for each Foreignkey, one for the Name to display, and one for the ID
         * For setting the item to the spinner:
         *  1. the *_*id Adapter is compared with the given id, and returns the position where the id is located
         *  2. this position is set to the spinner, so i get the correct name
         * For getting the selected id, it is vice versa
         */
        val subjectSname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val subjectSid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val schoolLessonTime = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val schoolLessonSlid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)


        /*---------------------get the list with all items in subject and schoolLesson--------------------------*/
        val subjectAll = subjectViewModel.allSubjectList()
        val schoolLessonAll = schoolLessonViewModel.allSchoolLessonList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        subjectAll.forEach {
            subjectSid.add(it.sid)
            subjectSname.add(it.sname)
        }

        schoolLessonAll.forEach {
            schoolLessonSlid.add(it.slid)
            //IF minute is less then 10, add a 0 in front of it (just for the view)
            val returnStartMinute = if (it.slstartminute < 10) "0${it.slstartminute}" else "${it.slstartminute}"
            val returnEndMinute = if (it.slendminute < 10) "0${it.slendminute}" else "${it.slendminute}"

            schoolLessonTime.add("${it.slnumber}: ${it.slstarthour}:$returnStartMinute - ${it.slendhour}:$returnEndMinute")
        }

        /*---------------------Set list of all subjects / schoolLessons to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //First Define some Properties
            spSid.setLabel("Fach")
            spSlid.setLabel("Stunde")

            // Set layout to use when the list of choices appear
            subjectSname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            schoolLessonTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            spSid.setAdapter(subjectSname)
            spSlid.setAdapter(schoolLessonTime)
            //Set gived id's to spinner
            //How this works is explained a few lines above
            val selectedSubjectPos = subjectSid.getPosition(sid)
            selectedSubjectPos.let { spSid.getSpinner().setSelection(it) }

            val selectedSchoolLessonPos = schoolLessonSlid.getPosition(slid)
            selectedSchoolLessonPos.let { spSlid.getSpinner().setSelection(it) }


            //After 0,5s disable the progressbar
            delay(500)
            pbLesson.visibility = View.INVISIBLE

            //Save current selected ID, because when nothing changed, id == -1, as declared on top
            selectedSID = sid
            selectedSLID = slid

            //save new ID, when other item is choosen
            spSid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on subject_sid
                    selectedSID = subjectSid.getItem(position)!!
                }
            }

            spSlid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on schoolLesson_slid
                    selectedSLID = schoolLessonSlid.getItem(position)!!
                }
            }


        }
    }


    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveLesson() {

        val selectedDay = mdp.selectedDays.singleOrNull()

        val selctedDayInt: Int

        selctedDayInt = when (selectedDay) {
            MaterialDayPicker.Weekday.MONDAY -> 1
            MaterialDayPicker.Weekday.TUESDAY -> 2
            MaterialDayPicker.Weekday.WEDNESDAY -> 3
            MaterialDayPicker.Weekday.THURSDAY -> 4
            MaterialDayPicker.Weekday.FRIDAY -> 5
            MaterialDayPicker.Weekday.SATURDAY -> 6
            MaterialDayPicker.Weekday.SUNDAY -> 7
            else -> -1
        }

        if (selctedDayInt == -1) {
            Toast.makeText(this, "Bitte wähle einen Tag aus", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        data.putExtra(EXTRA_LDAY, selctedDayInt)
        data.putExtra(EXTRA_L_SID, selectedSID)
        data.putExtra(EXTRA_L_SLID, selectedSLID)

        val id = intent.getIntExtra(EXTRA_LID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_LID, id)
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
                saveLesson()
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