package com.ebner.stundenplan

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.customAdapter.SubjectExamsExamsListAdapter
import com.ebner.stundenplan.customAdapter.SubjectExamsTasksListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.task.TaskViewModel
import kotlinx.coroutines.runBlocking
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
    private lateinit var tvSubjectexamsGrade: TextView

    private lateinit var tasksViewModel: TaskViewModel
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
        tvSubjectexamsGrade = findViewById(R.id.tv_subjectexams_grade)

        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_SID)) {
            title = intent.getStringExtra(EXTRA_SNAME)
            //Save extras to vars
            val sid = intent.getIntExtra(EXTRA_SID, -1)


            val tasksListAdapter = SubjectExamsTasksListAdapter()
            val examsListAdapter = SubjectExamsExamsListAdapter()
            rvSubjectexamsTasks.adapter = tasksListAdapter
            rvSubjectexamsExams.adapter = examsListAdapter
            rvSubjectexamsTasks.layoutManager = LinearLayoutManager(this)
            rvSubjectexamsExams.layoutManager = LinearLayoutManager(this)

            tasksViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
            examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
            settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
            //Automatic update the recyclerlayout

            //Get current activeYearID
            settingsViewModel.allSettings.observe(this, Observer { setting ->
                activeYearID = setting.settings.setyid

                tasksViewModel.subjectTasks(activeYearID, sid).observe(this, Observer {
                    tasksListAdapter.submitList(it)
                })

                examViewModel.subjectExams(activeYearID, sid).observe(this, Observer {
                    examsListAdapter.submitList(it)
                })

                calculateAndApplySubjectGrade(sid)
            })


        } else {
            Toast.makeText(this, "failed to fetch Subject", Toast.LENGTH_SHORT).show()
        }


    }

    private fun calculateAndApplySubjectGrade(sid: Int) {
        //runBlocked, so the adapter needs to wait with submitList until this action has finished
        runBlocking {
            /**
             * Get a List of all Exams for this Subject (and the activeYear)
             * This function does not return LiveData with all Exams, so in to update the adapter automatic
             * this happens [onResume]
             */
            /**
             * Get a List of all Exams for this Subject (and the activeYear)
             * This function does not return LiveData with all Exams, so in to update the adapter automatic
             * this happens [onResume]
             */
            val exams = examViewModel.subjectExamsSuspend(activeYearID, sid)

            //allGradesCounted are all exam grades counted together (with exam weight multiplied)
            var allGradesCounted = 0.0
            //items is the count of exams multiplied with the exam weight
            var items = 0.0

            //now add each exam to allGradesCounted and items when grade for this exam is already present
            exams.forEach {
                if (it.exam.egrade != -1) {
                    allGradesCounted += (it.exam.egrade * it.examtype.etweight)
                    items += it.examtype.etweight
                }
            }
            var result = 0.0
            /**
             * If all Exams have no grades or no exams saved for this subject, the resultGrade = 0.0
             */

            if (items != 0.0) {
                result = allGradesCounted / items
                result = (result * 100.0).roundToInt() / 100.0

            }

            //if result == 0.0, set text to "-" else the calculated grade
            tvSubjectexamsGrade.text = if (result > 0.0) "$result" else "-"


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