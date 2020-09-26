package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class ActivityAddEditLesson : AppCompatActivity() {


    companion object {
        const val EXTRA_LID = "com.ebner.stundenplan.fragments.manage.EXTRA_LID"
        const val EXTRA_LDAY = "com.ebner.stundenplan.fragments.manage.EXTRA_LDAY"
        const val EXTRA_L_SLID = "com.ebner.stundenplan.fragments.manage.EXTRA_L_SLID"
        const val EXTRA_L_SID = "com.ebner.stundenplan.fragments.manage.EXTRA_L_SID"
    }

    private var selectedSLID: Int = -1
    private var selectedSID: Int = -1

    private lateinit var mdp: MaterialDayPicker
    private lateinit var dropdownSid: AutoCompleteTextView
    private lateinit var tilSid: TextInputLayout
    private lateinit var dropdownSlid: AutoCompleteTextView
    private lateinit var tilSlid: TextInputLayout
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
        dropdownSid = findViewById(R.id.actv_dropdown_lesson_sid)
        tilSid = findViewById(R.id.til_dropdown_lesson_sid)
        dropdownSlid = findViewById(R.id.actv_dropdown_lesson_slid)
        tilSlid = findViewById(R.id.til_dropdown_lesson_slid)
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

            selectedSID = lsid
            selectedSLID = lslid


        } else {
            title = "Neue " + getString(R.string.fragment_lesson)
        }

        //Fetch Lesson and SchoolLesson list
        CoroutineScope(Dispatchers.IO).launch {
            fetchFromDatabase()
        }

    }


    /*---------------------Fetch Subjects and SchoolLessons in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase() {
        /*---------------------get access to subject and schoolLesson table --------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        val schoolLessonViewModel = ViewModelProvider(this).get(SchoolLessonViewModel::class.java)

        /*---------------------get the list with all items in subject and schoolLesson--------------------------*/
        val subjectList = subjectViewModel.allSubjectList()
        val schoollessonList = schoolLessonViewModel.allSchoolLessonList()

        val dropDownAdapterSubjects = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, subjectList)
        val dropDownAdapterSchoollessons = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, schoollessonList)

        /*---------------------Set list of all subjects / schoolLessons to spinner (back in the Main thread)--------------------------*/
        withContext(Dispatchers.Main) {

            //Add the ArrayAdapter to the Dropdown menu
            dropdownSid.setAdapter(dropDownAdapterSubjects)
            dropdownSlid.setAdapter(dropDownAdapterSchoollessons)

            //Set gived id's to spinner

            if (selectedSID != -1) {
                val selectedSubject = subjectList.first { it.sid == selectedSID }
                dropdownSid.setText(selectedSubject.sname, false)
            }
            if (selectedSLID != -1) {
                val selectedSchoollesson = schoollessonList.first { it.slid == selectedSLID }
                dropdownSlid.setText(selectedSchoollesson.toString(), false)
            }

            //After 0,5s disable the progressbar
            delay(500)
            pbLesson.visibility = View.INVISIBLE


            //save new ID, when other item is chosen
            dropdownSid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedSID = (parent.adapter.getItem(position) as Subject).sid
                tilSid.error = ""
            }

            dropdownSlid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedSLID = (parent.adapter.getItem(position) as SchoolLesson).slid
                tilSlid.error = ""
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

        var error = false
        if (selctedDayInt == -1) {
            Toast.makeText(this, "Bitte wähle einen Tag aus", Toast.LENGTH_SHORT).show()
            error = true
        }
        if (selectedSID == -1) {
            tilSid.error = "Bitte wähle ein Fach"
            error = true
        }
        if (selectedSLID == -1) {
            tilSlid.error = "Bitte wähle eine Schulstunde"
            error = true
        }
        if (error) return

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