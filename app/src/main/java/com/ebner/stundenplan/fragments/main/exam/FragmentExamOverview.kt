package com.ebner.stundenplan.fragments.main.exam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.SubjectExamsActivity
import com.ebner.stundenplan.customAdapter.ExamOverviewListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.mergedEntities.SubjectGrade
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import kotlinx.coroutines.*
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentExamOverview : Fragment(), ExamOverviewListAdapter.OnItemClickListener {

    private lateinit var examViewModel: ExamViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var adapter: ExamOverviewListAdapter
    private lateinit var tvGradeArithmetic: TextView
    private lateinit var tvGradeWeighted: TextView

    private var activeYearID: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam_overview, container, false)

        /*---------------------Link items to Layout--------------------------*/
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_exam_overview)
        tvGradeArithmetic = root.findViewById(R.id.tv_examoverview_grade_arithmetic)
        tvGradeWeighted = root.findViewById(R.id.tv_examoverview_grade_weighted)

        adapter = ExamOverviewListAdapter(this)
        //Disable the Update Animation, because of updating the adapter manual the list flash very often up
        recyclerView.itemAnimator = null
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        //Automatic update the recyclerlayout

        //Get current activeYearID
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid

            fetchAndSupplySubjectsWithGrades()
            fetchAndSupplyTotalGrades()


        })
        return root
    }

    /**
     * get all Exams for all Subjects, and calculate the total grade with Arithmetic and Weighted method
     */
    @SuppressLint("SetTextI18n")
    private fun fetchAndSupplyTotalGrades() {
        CoroutineScope(Dispatchers.IO).launch {
            var arithmeticValue = 0.0
            var arithmeticCount = 0.0
            var arithmeticResult = 0.0
            var weightedValue = 0.0
            var weightedCount = 0.0
            var weightedResult = 0.0

            //Get a List of all Exams
            val weightedExams = examViewModel.allExamSuspend(activeYearID)
            weightedExams.forEach {
                if (it.exam.egrade != -1) {
                    weightedValue += (it.exam.egrade * it.examtype.etweight)
                    weightedCount += it.examtype.etweight
                }
            }

            //Get a List of all Subjects
            val subjects = subjectViewModel.allSubjectList()
            //For Each Subject run -->
            subjects.forEach { subject ->

                /**
                 * Get a List of all Exams for this Subject (and the activeYear)
                 * This function does not return LiveData with all Exams, so in to update the adapter automatic
                 * this happens [onResume]
                 */
                var arithmeticSubjectValue = 0.0
                var arithmeticSubjectCount = 0.0

                val exams = examViewModel.subjectExamsSuspend(activeYearID, subject.sid)

                exams.forEach {
                    if (it.exam.egrade != -1) {
                        arithmeticSubjectValue += (it.exam.egrade * it.examtype.etweight)
                        arithmeticSubjectCount += it.examtype.etweight
                    }
                }

                if (arithmeticSubjectCount != 0.0) {
                    arithmeticValue += (arithmeticSubjectValue / arithmeticSubjectCount)
                    arithmeticCount++
                }

            }

            if (arithmeticCount != 0.0) {
                arithmeticResult = arithmeticValue / arithmeticCount
                arithmeticResult = (arithmeticResult * 100.0).roundToInt() / 100.0

            }
            if (weightedCount != 0.0) {
                weightedResult = weightedValue / weightedCount
                weightedResult = (weightedResult * 100.0).roundToInt() / 100.0
            }

            withContext(Dispatchers.Main) {
                tvGradeArithmetic.text = "Arithmetisch: $arithmeticResult"
                tvGradeWeighted.text = "Gewichtet: $weightedResult"
            }

        }

    }

    /**
     * get all Subjects, and for each subject all exams then add the Subject and the calculated grade as new object to a List,
     * and then submit the list to the adapter
     */
    private fun fetchAndSupplySubjectsWithGrades() {
        val subjectGrade: MutableList<SubjectGrade> = ArrayList()

        //runBlocked, so the adapter needs to wait with submitList until this action has finished
        runBlocking {

            //Get a List of all Subjects
            val subjects: MutableList<Subject> = subjectViewModel.allSubjectList() as MutableList<Subject>
            //For Each Subject run -->
            subjects.forEach { subject ->

                /**
                 * Get a List of all Exams for this Subject (and the activeYear)
                 * This function does not return LiveData with all Exams, so in to update the adapter automatic
                 * this happens [onResume]
                 */
                val exams = examViewModel.subjectExamsSuspend(activeYearID, subject.sid)

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
                 * The [ExamOverviewListAdapter] checks if result == 0.0, and set text to "-" or the calculated grade
                 */

                if (items != 0.0) {
                    result = allGradesCounted / items
                    result = (result * 100.0).roundToInt() / 100.0

                }
                subjectGrade.add(SubjectGrade(subject, result))

            }
        }
        //After everything in runBlocking has finishd, submit the List to the adapter
        adapter.submitList(subjectGrade)

    }

    override fun onItemClicked(subjectGrade: SubjectGrade) {
        val intent = Intent(context, SubjectExamsActivity::class.java)
        intent.putExtra(SubjectExamsActivity.EXTRA_SID, subjectGrade.subject.sid)
        intent.putExtra(SubjectExamsActivity.EXTRA_SNAME, subjectGrade.subject.sname)
        intent.putExtra(SubjectExamsActivity.EXTRA_SCOLOR, subjectGrade.subject.scolor)
        startActivity(intent)
    }

    //The return from fetching all Exams is no LiveData, so i need to manual update the SubjectsWithGrades list, and this happens with this action
    override fun onResume() {
        super.onResume()
        fetchAndSupplySubjectsWithGrades()
        fetchAndSupplyTotalGrades()
    }
}