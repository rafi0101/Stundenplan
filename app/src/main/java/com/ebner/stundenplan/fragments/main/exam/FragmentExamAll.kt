package com.ebner.stundenplan.fragments.main.exam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.exam.Exam
import com.ebner.stundenplan.database.table.exam.ExamListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.ebner.stundenplan.fragments.main.ActivityAddEditExam
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class FragmentExamAll(private val fragType: Int) : Fragment(), ExamListAdapter.OnItemClickListener, ExamListAdapter.OnItemLongClickListener {

    /**
     * FragTypes:
     *  0: ShowAllExams
     *  1: ShowExamswithoutGrade
     */
    private val TAG = "debug_FragmentExamAll"

    private lateinit var examViewModel: ExamViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var clExam: CoordinatorLayout
    private lateinit var adapter: ExamListAdapter
    private lateinit var recyclerView: RecyclerView


    private var activeYearID: Int = -1
    private var selectedSubject: Int = -1 //subject ID
    private var selectedOrder: Int = -1 //0 = Fach, 1 = Prüfungs Art, (2 = Note)

    companion object {
        private const val ADD_EXAM_REQUEST = 1
        private const val EDIT_EXAM_REQUEST = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam_all, container, false)

        /*---------------------Link items to Layout--------------------------*/
        clExam = activity?.findViewById(R.id.cl_exam)!!
        val fab = activity?.findViewById<FloatingActionButton>(R.id.btn_exam_addExam)
        recyclerView = root.findViewById(R.id.rv_exam_all)
        val dropdownSubject: AutoCompleteTextView = root.findViewById(R.id.actv_dropdown_subject)
        val dropdownSort: AutoCompleteTextView = root.findViewById(R.id.actv_dropdown_sort)
        val tilDropdownSort: TextInputLayout = root.findViewById(R.id.til_dropdown_sort)

        /*---------------------Disable "Sort" Dropdown menu, if fragType == Exams WITHOUT Grade--------------------------*/
        if (fragType == 1) tilDropdownSort.visibility = View.GONE

        adapter = ExamListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        //Automatic update the recyclerlayout

        //Get current activeYearID
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid

            updateRecyclerView()
        })

        val subjectsList: MutableList<Subject> = mutableListOf(Subject("Auswählen", "", -1, "", true, -1, -1))
        val sortOrders = mutableListOf("Auswählen", "Fach", "Prüfungs Art", "Note")

        runBlocking {
            //Get a List of all Subjects
            subjectsList.addAll(subjectViewModel.allSubjectList() as MutableList<Subject>)
        }


        val dropDownAdapterSubject = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, subjectsList)
        dropdownSubject.setAdapter(dropDownAdapterSubject)
        val dropDownAdapterSortOrder = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, sortOrders)
        dropdownSort.setAdapter(dropDownAdapterSortOrder)

        dropdownSubject.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedSubjectSubject = parent.adapter.getItem(position) as Subject
            selectedSubject = selectedSubjectSubject.sid

            if (selectedSubjectSubject.sname == "Auswählen" && selectedSubjectSubject.scolor == -1 && selectedSubjectSubject.sinactive == true) {
                selectedSubject = -1
            }

            updateRecyclerView()
        }
        dropdownSort.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedOrder = position
            if (parent.adapter.getItem(position).toString() == "Auswählen") {
                selectedOrder = -1
            }
            updateRecyclerView()
        }


        /*---------------------FAB Add Button--------------------------*/
        fab?.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditExam::class.java)
            startActivityForResult(intent, ADD_EXAM_REQUEST)
        }


        //Return the inflated layout
        return root
    }

    /*---------------------Update Recycler View--------------------------*/
    private fun updateRecyclerView() {

        if (fragType == 1) {
            if (selectedSubject == -1) {
                examViewModel.allExamPending(activeYearID).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            } else {
                examViewModel.allExamPendingBySubject(activeYearID, selectedSubject).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            }
        } else {
            if (selectedSubject == -1 && selectedOrder == -1) {
                examViewModel.allExam(activeYearID).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            } else if (selectedSubject != -1 && selectedOrder == -1) {
                examViewModel.allExamBySubject(activeYearID, selectedSubject).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            } else if (selectedSubject == -1 && selectedOrder != -1) {
                examViewModel.allExamByOrder(activeYearID, selectedOrder).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            } else if (selectedSubject != -1 && selectedOrder != -1) {
                examViewModel.allExamBySubjectOrder(activeYearID, selectedSubject, selectedOrder).observe(viewLifecycleOwner, Observer { exams ->
                    exams.let { adapter.submitList(it) }
                })
            }
        }
        recyclerView.smoothScrollToPosition(0)
    }


    /*---------------------when returning from |ActivityAddEditSubject| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (activeYearID == -1) return

        if (resultCode == Activity.RESULT_OK) {
            //Save extras to vars
            val sid = data!!.getIntExtra(ActivityAddEditExam.EXTRA_E_SID, -1)
            val etid = data.getIntExtra(ActivityAddEditExam.EXTRA_E_ETID, -1)
            val egrade = data.getIntExtra(ActivityAddEditExam.EXTRA_GRADE, -1)
            val edateyear = data.getIntExtra(ActivityAddEditExam.EXTRA_DATEYEAR, -1)
            val edatemonth = data.getIntExtra(ActivityAddEditExam.EXTRA_DATEMONTH, -1)
            val edateday = data.getIntExtra(ActivityAddEditExam.EXTRA_DATEDAY, -1)
            val exam = Exam(sid, etid, activeYearID, egrade, edateyear, edatemonth, edateday)

            /*---------------------If the Request was a ADD subject request--------------------------*/
            if (requestCode == ADD_EXAM_REQUEST) {

                if (sid == -1 || etid == -1) {
                    val snackbar = Snackbar
                            .make(clExam, "Failed to add Exam", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }


                examViewModel.insert(exam)


                /*---------------------If the Request was a EDIT teacher request--------------------------*/
            } else if (requestCode == EDIT_EXAM_REQUEST) {
                val id = data.getIntExtra(ActivityAddEditExam.EXTRA_EID, -1)

                if (sid == -1 || etid == -1 || id == -1) {
                    val snackbar = Snackbar
                            .make(clExam, "Failed to update Exam!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                exam.eid = id
                examViewModel.update(exam)


            }
        }
    }


    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(examSubjectYearExamtype: ExamSubjectYearExamtype) {
        val intent = Intent(context, ActivityAddEditExam::class.java)
        intent.putExtra(ActivityAddEditExam.EXTRA_EID, examSubjectYearExamtype.exam.eid)
        intent.putExtra(ActivityAddEditExam.EXTRA_E_SID, examSubjectYearExamtype.exam.esid)
        intent.putExtra(ActivityAddEditExam.EXTRA_E_ETID, examSubjectYearExamtype.exam.eetid)
        intent.putExtra(ActivityAddEditExam.EXTRA_GRADE, examSubjectYearExamtype.exam.egrade)
        intent.putExtra(ActivityAddEditExam.EXTRA_DATEYEAR, examSubjectYearExamtype.exam.edateyear)
        intent.putExtra(ActivityAddEditExam.EXTRA_DATEMONTH, examSubjectYearExamtype.exam.edatemonth)
        intent.putExtra(ActivityAddEditExam.EXTRA_DATEDAY, examSubjectYearExamtype.exam.edateday)
        startActivityForResult(intent, EDIT_EXAM_REQUEST)

    }

    override fun onItemLongClicked(examSubjectYearExamtype: ExamSubjectYearExamtype) {
        /*---------------------Confirm Delete Dialog--------------------------*/

        val exam = Exam(examSubjectYearExamtype.exam.esid, examSubjectYearExamtype.exam.eetid, examSubjectYearExamtype.exam.eyid, examSubjectYearExamtype.exam.egrade, examSubjectYearExamtype.exam.edateyear, examSubjectYearExamtype.exam.edatemonth, examSubjectYearExamtype.exam.edateday)
        exam.eid = examSubjectYearExamtype.exam.eid

        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Achtung")
                .setMessage("Es wird gelöscht: ${examSubjectYearExamtype.subject.sname} ${examSubjectYearExamtype.examtype.etname} vom ${exam.edateday}.${exam.edatemonth + 1}.${exam.edateyear}")
                .setPositiveButton("Löschen") { _, _ ->
                    examViewModel.delete(exam)

                }
                .setNegativeButton("Abbrechen", null)
                .show()
    }
}