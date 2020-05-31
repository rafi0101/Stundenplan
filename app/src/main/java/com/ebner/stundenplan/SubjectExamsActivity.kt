package com.ebner.stundenplan

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.customAdapter.SubjectExamsExamsListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.settings.SettingsViewModel

class SubjectExamsActivity : AppCompatActivity() {

    companion object {
        val EXTRA_SID = "com.ebner.stundenplan.EXTRA_SID"
        val EXTRA_SNAME = "com.ebner.stundenplan.EXTRA_SNAME"
        val EXTRA_SCOLOR = "com.ebner.stundenplan.EXTRA_SCOLOR"
    }

    private lateinit var rv_subjectexams_tasks: RecyclerView
    private lateinit var rv_subjectexams_exams: RecyclerView

    private lateinit var examViewModel: ExamViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private var activeYearID: Int = -1


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_exams)

        /*---------------------Add back Button to the toolbar--------------------------*/
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(intent.getIntExtra(EXTRA_SCOLOR, -1))
        supportActionBar!!.setBackgroundDrawable(colorDrawable)
        window.statusBarColor = manipulateColor(intent.getIntExtra(EXTRA_SCOLOR, -1), 0.8f)

        /*---------------------Link items to Layout--------------------------*/
        rv_subjectexams_tasks = findViewById(R.id.rv_subjectexams_tasks)
        rv_subjectexams_exams = findViewById(R.id.rv_subjectexams_exams)

        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SID)) {
            title = intent.getStringExtra(EXTRA_SNAME)
            //Save extras to vars
            val sid = intent.getIntExtra(EXTRA_SID, -1)


            val adapter = SubjectExamsExamsListAdapter()
            rv_subjectexams_exams.adapter = adapter
            rv_subjectexams_exams.layoutManager = LinearLayoutManager(this)

            examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
            settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
            //Automatic update the recyclerlayout

            //Get current activeYearID
            settingsViewModel.allSettings.observe(this, Observer { setting ->
                activeYearID = setting.settings.setyid

                examViewModel.subjectExams(activeYearID, sid).observe(this, Observer {
                    adapter.submitList(it)
                })


            })


        } else {
            Toast.makeText(this, "failed to fetch Subject", Toast.LENGTH_SHORT).show();
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //Back Button
            android.R.id.home -> {
                super.onBackPressed()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }
}