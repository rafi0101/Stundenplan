package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.room.Room
import com.ebner.stundenplan.database.table.room.RoomViewModel
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.teacher.TeacherViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ActivityAddEditSubject : AppCompatActivity() {


    companion object {
        const val EXTRA_SID = "com.ebner.stundenplan.fragments.manage.EXTRA_SID"
        const val EXTRA_SNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_SNAME"
        const val EXTRA_SNAMESHORT = "com.ebner.stundenplan.fragments.manage.EXTRA_SNAMESHORT"
        const val EXTRA_S_TID = "com.ebner.stundenplan.fragments.manage.EXTRA_S_TID"
        const val EXTRA_S_RID = "com.ebner.stundenplan.fragments.manage.EXTRA_S_RID"
        const val EXTRA_SNOTE = "com.ebner.stundenplan.fragments.manage.EXTRA_SNOTE"
        const val EXTRA_SCOLOR = "com.ebner.stundenplan.fragments.manage.EXTRA_SCOLOR"
        const val EXTRA_SINACTIVE = "com.ebner.stundenplan.fragments.manage.EXTRA_SINACTIVE"
    }

    private var selectedTID: Int = -1
    private var selectedRID: Int = -1
    private var selectedColor: Int = 0

    private lateinit var tietSname: TextInputEditText
    private lateinit var tilSname: TextInputLayout
    private lateinit var dropdownRid: AutoCompleteTextView
    private lateinit var tilRid: TextInputLayout
    private lateinit var dropdownTid: AutoCompleteTextView
    private lateinit var tilTid: TextInputLayout
    private lateinit var btnScolor: Button
    private lateinit var swInactive: SwitchMaterial
    private lateinit var tietSnmaeshort: TextInputEditText
    private lateinit var tilSnameshort: TextInputLayout
    private lateinit var tietSnote: TextInputEditText
    private lateinit var tilSnote: TextInputLayout
    private lateinit var pbSubject: ProgressBar


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_subject)

        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }


        /*---------------------Link items to Layout--------------------------*/
        tietSname = findViewById(R.id.tiet_subject_sname)
        tilSname = findViewById(R.id.til_subject_sname)
        dropdownRid = findViewById(R.id.actv_dropdown_subject_rid)
        tilRid = findViewById(R.id.til_dropdown_subject_rid)
        dropdownTid = findViewById(R.id.actv_dropdown_subject_tid)
        tilTid = findViewById(R.id.til_dropdown_subject_tid)
        btnScolor = findViewById(R.id.btn_subject_color)
        swInactive = findViewById(R.id.sw_subject_inaktiv)
        tietSnmaeshort = findViewById(R.id.tiet_subject_snameshort)
        tilSnameshort = findViewById(R.id.til_subject_snameshort)
        tietSnote = findViewById(R.id.tiet_subject_note)
        tilSnote = findViewById(R.id.til_subject_note)
        pbSubject = findViewById(R.id.pb_subject)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SID)) {
            title = getString(R.string.fragment_subject) + " bearbeiten"
            //Save extras to vars
            val sname = intent.getStringExtra(EXTRA_SNAME)
            val snameshort = intent.getStringExtra(EXTRA_SNAMESHORT)
            val snote = intent.getStringExtra(EXTRA_SNOTE)
            val scolor = intent.getIntExtra(EXTRA_SCOLOR, 0)
            val sinactive = intent.getBooleanExtra(EXTRA_SINACTIVE, false)
            val sRid = intent.getIntExtra(EXTRA_S_RID, -1)
            val sTid = intent.getIntExtra(EXTRA_S_TID, -1)

            tietSname.setText(sname)
            tietSnmaeshort.setText(snameshort)
            tietSnote.setText(snote)
            btnScolor.setBackgroundColor(scolor)
            selectedColor = scolor
            swInactive.isChecked = sinactive

            selectedRID = sRid
            selectedTID = sTid

        } else {
            title = "Neues " + getString(R.string.fragment_subject)

            //Set Random color to new Objects
            val rnd = Random()
            selectedColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            btnScolor.setBackgroundColor(selectedColor)

        }

        //Fetch teacher and room list
        CoroutineScope(IO).launch {
            fetchFromDatabase()
        }

        //Color Picker
        btnScolor.setOnClickListener {
            ColorPickerDialog
                    .Builder(this)                    // Pass Activity Instance
                    .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                    .setDefaultColor(selectedColor) //Default selected Color
                    .setColorListener { color, _ ->
                        // Handle Color Selection
                        btnScolor.setBackgroundColor(color)
                        selectedColor = color
                    }
                    .show()
        }

        //Remove the error message, if user starts typing
        tietSname.addTextChangedListener { text ->
            //Set text to SNAMESHORT,if both EditTexts are plain
            if (TextUtils.isEmpty(tietSnmaeshort.text.toString()) && !TextUtils.isEmpty(text)) {
                val first = text?.substring(0, 1)!!
                first.toUpperCase(Locale.getDefault())
                tietSnmaeshort.setText(first)
            }
            tilSname.error = ""
        }

        tietSnmaeshort.addTextChangedListener {
            tilSnameshort.error = ""
        }


    }

    /*---------------------Fetch Rooms and Teachers in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase() {
        /*---------------------get access to room and teacher table --------------------------*/
        val roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val teacherViewModel = ViewModelProvider(this).get(TeacherViewModel::class.java)

        /*---------------------get the list with all items in room and teacher--------------------------*/
        val roomList = roomViewModel.allRoomList()
        val teacheList = teacherViewModel.allTeacherList()

        val dropDownAdapterRoom = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, roomList)
        val dropDownAdapterTeacher = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, teacheList)

        /*---------------------Set list of all rooms / teachers to spinner (back in the Main thread)--------------------------*/
        withContext(Main) {

            //Add the ArrayAdapter to the Dropdown menu
            dropdownRid.setAdapter(dropDownAdapterRoom)
            dropdownTid.setAdapter(dropDownAdapterTeacher)

            //Preselect Dropdown menu
            if (selectedRID != -1) {
                val selectedRoom = roomList.first { it.rid == selectedRID }
                dropdownRid.setText(selectedRoom.toString(), false)

            }
            if (selectedTID != -1) {
                val selectedTeacher = teacheList.first { it.tid == selectedTID }
                dropdownTid.setText(selectedTeacher.toString(), false)
            }


            //After 0,5s disable the progressbar
            delay(500)
            pbSubject.visibility = View.INVISIBLE

            //save new ID, when other item is chosen
            dropdownRid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedRID = (parent.adapter.getItem(position) as Room).rid
                tilRid.error = ""
            }

            dropdownTid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedTID = (parent.adapter.getItem(position) as Teacher).tid
                tilTid.error = ""
            }
        }
    }

    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveSubject() {
        var error = false
        /*---------------------If EditText is empty return error--------------------------*/
        if (TextUtils.isEmpty(tietSname.text.toString())) {
            tilSname.error = "Gib einen Namen ein!"
            error = true
        }
        if (selectedRID == -1) {
            tilRid.error = "Bitte wähle einen Raum"
            error = true
        }
        if (selectedTID == -1) {
            tilTid.error = "Bitte wähle einen Lehrer"
            error = true
        }
        if (TextUtils.isEmpty(tietSnmaeshort.text.toString())) {
            tilSnameshort.error = "Gib eine Abkürzung ein"
            error = true
        }
        if (error) return


        val sname = tietSname.text.toString()
        val snameshort = tietSnmaeshort.text.toString()
        val snote = tietSnote.text.toString()
        val sinactive = swInactive.isChecked


        val data = Intent()
        data.putExtra(EXTRA_SNAME, sname)
        data.putExtra(EXTRA_SNAMESHORT, snameshort)
        data.putExtra(EXTRA_SNOTE, snote)
        data.putExtra(EXTRA_SCOLOR, selectedColor)
        data.putExtra(EXTRA_SINACTIVE, sinactive)
        data.putExtra(EXTRA_S_RID, selectedRID)
        data.putExtra(EXTRA_S_TID, selectedTID)

        val id = intent.getIntExtra(EXTRA_SID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_SID, id)
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
                saveSubject()
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




