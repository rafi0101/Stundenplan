package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.room.RoomViewModel
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

    private val TAG = "debug_ActivityAddEditSubject"

    companion object {
        val EXTRA_SID = "com.ebner.stundenplan.fragments.manage.EXTRA_SID"
        val EXTRA_SNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_SNAME"
        val EXTRA_SNAMESHORT = "com.ebner.stundenplan.fragments.manage.EXTRA_SNAMESHORT"
        val EXTRA_S_TID = "com.ebner.stundenplan.fragments.manage.EXTRA_S_TID"
        val EXTRA_S_RID = "com.ebner.stundenplan.fragments.manage.EXTRA_S_RID"
        val EXTRA_SNOTE = "com.ebner.stundenplan.fragments.manage.EXTRA_SNOTE"
        val EXTRA_SCOLOR = "com.ebner.stundenplan.fragments.manage.EXTRA_SCOLOR"
        val EXTRA_SINACTIVE = "com.ebner.stundenplan.fragments.manage.EXTRA_SINACTIVE"
    }

    var selectedTID: Int = -1
    var selectedRID: Int = -1
    var selectedColor: Int = 0

    private lateinit var tiet_sname: TextInputEditText
    private lateinit var til_sname: TextInputLayout
    private lateinit var tiet_snmaeshort: TextInputEditText
    private lateinit var til_snameshort: TextInputLayout
    private lateinit var tiet_snote: TextInputEditText
    private lateinit var til_snote: TextInputLayout
    private lateinit var sp_tid: MaterialSpinner
    private lateinit var sp_rid: MaterialSpinner
    private lateinit var btn_scolor: Button
    private lateinit var sw_inactive: SwitchMaterial
    private lateinit var pb_subject: ProgressBar


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
        tiet_sname = findViewById(R.id.tiet_subject_sname)
        til_sname = findViewById(R.id.til_subject_sname)
        tiet_snmaeshort = findViewById(R.id.tiet_subject_snameshort)
        til_snameshort = findViewById(R.id.til_subject_snameshort)
        tiet_snote = findViewById(R.id.tiet_subject_note)
        til_snote = findViewById(R.id.til_subject_note)
        sp_rid = findViewById(R.id.sp_subject_rid)
        sp_tid = findViewById(R.id.sp_subject_tid)
        btn_scolor = findViewById(R.id.btn_subject_color)
        sw_inactive = findViewById(R.id.sw_subject_inaktiv)
        pb_subject = findViewById(R.id.pb_subject)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SID)) {
            title = getString(R.string.fragment_subject) + " bearbeiten"
            //Save extras to vars
            val sname = intent.getStringExtra(EXTRA_SNAME)
            val snameshort = intent.getStringExtra(EXTRA_SNAMESHORT)
            val snote = intent.getStringExtra(EXTRA_SNOTE)
            val scolor = intent.getIntExtra(EXTRA_SCOLOR, 0)
            val sinactive = intent.getBooleanExtra(EXTRA_SINACTIVE, false)
            val s_rid = intent.getIntExtra(EXTRA_S_RID, -1)
            val s_tid = intent.getIntExtra(EXTRA_S_TID, -1)

            tiet_sname.setText(sname)
            tiet_snmaeshort.setText(snameshort)
            tiet_snote.setText(snote)
            btn_scolor.setBackgroundColor(scolor)
            selectedColor = scolor
            sw_inactive.isChecked = sinactive

            //Fetch teacher and room list, and pass values to set in spinner
            CoroutineScope(IO).launch {
                fetchFromDatabase(s_rid, s_tid)
            }


        } else {
            title = "Neues " + getString(R.string.fragment_subject)

            //Set Random color to new Objects
            val rnd = Random()
            selectedColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            btn_scolor.setBackgroundColor(selectedColor)

            //Fetch teacher and room list, and pass values to set in spinner
            CoroutineScope(IO).launch {
                fetchFromDatabase(1, 1)
            }
        }

        //Color Picker
        btn_scolor.setOnClickListener {
            ColorPickerDialog
                    .Builder(this)                    // Pass Activity Instance
                    .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                    .setDefaultColor(selectedColor) //Default selected Color
                    .setColorListener { color, colorHex ->
                        // Handle Color Selection
                        btn_scolor.setBackgroundColor(color)
                        selectedColor = color
                    }
                    .show()
        }

        //Remove the error message, if user starts typing
        tiet_sname.addTextChangedListener { text ->
            //Set text to SNAMESHORT,if both EditTexts are plain
            if (TextUtils.isEmpty(tiet_snmaeshort.text.toString()) && !TextUtils.isEmpty(text)) {
                val first = text?.substring(0, 1)!!
                first.toUpperCase(Locale.getDefault())
                tiet_snmaeshort.setText(first)
            }
            til_sname.error = ""
        }

        tiet_snmaeshort.addTextChangedListener {
            til_snameshort.error = ""
        }


    }

    /*---------------------Fetch Rooms and Teachers in another "thread" and set them to the spinner--------------------------*/
    private suspend fun fetchFromDatabase(rid: Int, tid: Int) {
        /*---------------------get access to room and teacher table --------------------------*/
        val roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val teacherViewModel = ViewModelProvider(this).get(TeacherViewModel::class.java)

        /** ---------------------Create some simple Arrayadapters, to add each item...--------------------------
         * 2 Adapters for each Foreignkey, one for the Name to display, and one for the ID
         * For setting the item to the spinner:
         *  1. the *_*id Adapter is compared with the given id, and returns the position where the id is located
         *  2. this position is set to the spinner, so i get the correct name
         * For getting the selected id, it is vice versa
         */
        val room_rname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val room_rid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)
        val teacher_tname = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val teacher_tid = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item)


        /*---------------------get the list with all items in room and teacher--------------------------*/
        val room_all = roomViewModel.allRoomList()
        val teacher_all = teacherViewModel.allTeacherList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        room_all.forEach {
            room_rid.add(it.rid)
            room_rname.add(it.rname)
        }

        teacher_all.forEach {
            teacher_tid.add(it.tid)
            teacher_tname.add(it.tname)
        }

        /*---------------------Set list of all rooms / teachers to spinner (back in the Main thread)--------------------------*/
        withContext(Main) {

            //First Define some Properties
            sp_rid.setLabel("Raum")
            sp_tid.setLabel("Lehrer")

            // Set layout to use when the list of choices appear
            room_rname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            teacher_tname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            sp_rid.setAdapter(room_rname)
            sp_tid.setAdapter(teacher_tname)

            //Set gived id's to spinner
            //How this works is explained a few lines above
            val selectedRoomPos = room_rid.getPosition(rid)
            selectedRoomPos.let { sp_rid.getSpinner().setSelection(it) }

            val selectedTeacherPos = teacher_tid.getPosition(tid)
            selectedTeacherPos.let { sp_tid.getSpinner().setSelection(it) }

            //After 0,5s disable the progressbar
            delay(500)
            pb_subject.visibility = View.INVISIBLE

            //Save current selected ID, because when nothing changed, id == -1, as declared on top
            selectedRID = rid
            selectedTID = tid

            //save new ID, when other item is choosen
            sp_rid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on room_rid
                    selectedRID = room_rid.getItem(position)!!
                }
            }

            sp_tid.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Get position, and with this run query on teacher_tid
                    selectedTID = teacher_tid.getItem(position)!!
                    Log.d(TAG, "new selectedTID: $selectedTID")
                }
            }


        }
    }


    /*---------------------Save current entries, and return to Fragment--------------------------*/
    private fun saveSubject() {
        var error = false
        /*---------------------If EditText is empty return error--------------------------*/
        if (TextUtils.isEmpty(tiet_sname.text.toString())) {
            til_sname.error = "Gib einen Namen ein!"
            error = true
        }
        if (TextUtils.isEmpty(tiet_snmaeshort.text.toString())) {
            til_snameshort.error = "Gib eine Abkürzung ein"
            error = true
        }
        if (error) return


        val sname = tiet_sname.text.toString()
        val snameshort = tiet_snmaeshort.text.toString()
        val snote = tiet_snote.text.toString()
        val sinactive = sw_inactive.isChecked


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




