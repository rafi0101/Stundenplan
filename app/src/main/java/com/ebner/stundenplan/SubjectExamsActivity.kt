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
import kotlin.math.min
import kotlin.math.roundToInt

class SubjectExamsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SID = "com.ebner.stundenplan.EXTRA_SID"
        const val EXTRA_SNAME = "com.ebner.stundenplan.EXTRA_SNAME"
        const val EXTRA_SCOLOR = "com.ebner.stundenplan.EXTRA_SCOLOR"
    }

    private lateinit var rvSubjectexamsTasks: RecyclerView
    private lateinit var rvSubjectexamsExams: RecyclerView

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
        window.statusBarColor = manipulateColor(intent.getIntExtra(EXTRA_SCOLOR, -1))

        /*---------------------Link items to Layout--------------------------*/
        rvSubjectexamsTasks = findViewById(R.id.rv_subjectexams_tasks)
        rvSubjectexamsExams = findViewById(R.id.rv_subjectexams_exams)

        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SID)) {
            title = intent.getStringExtra(EXTRA_SNAME)
            //Save extras to vars
            val sid = intent.getIntExtra(EXTRA_SID, -1)


            val adapter = SubjectExamsExamsListAdapter()
            rvSubjectexamsExams.adapter = adapter
            rvSubjectexamsExams.layoutManager = LinearLayoutManager(this)

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
            Toast.makeText(this, "failed to fetch Subject", Toast.LENGTH_SHORT).show()
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

    private fun manipulateColor(color: Int): Int {
        val factor = 0.8F
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).roundToInt()
        val g = (Color.green(color) * factor).roundToInt()
        val b = (Color.blue(color) * factor).roundToInt()
        return Color.argb(a,
                min(r, 255),
                min(g, 255),
                min(b, 255))
    }
}