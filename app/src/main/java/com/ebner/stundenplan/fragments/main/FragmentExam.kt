package com.ebner.stundenplan.fragments.main

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.exam.Exam
import com.ebner.stundenplan.database.table.exam.ExamListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class FragmentExam : Fragment(), ExamListAdapter.onItemClickListener {

    private val TAG = "debug_FragmentExam"

    private lateinit var examViewModel: ExamViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var cl_exam: CoordinatorLayout

    private var activeYearID: Int = -1

    companion object {
        private const val ADD_EXAM_REQUEST = 1
        private const val EDIT_EXAM_REQUEST = 2
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam, container, false)

        activity?.title = getString(R.string.fragment_exams)

        /*---------------------Link items to Layout--------------------------*/
        cl_exam = root.findViewById(R.id.cl_exam)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_exam)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_exam_addExam)


        val adapter = ExamListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        //Automatic update the recyclerlayout

        //Get current activeYearID
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid

            examViewModel.allExam(activeYearID).observe(viewLifecycleOwner, Observer { exams ->
                exams.let { adapter.submitList(it) }
            })
        })

        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditExam::class.java)
            startActivityForResult(intent, ADD_EXAM_REQUEST)
        }


        /*---------------------Swiping on a row--------------------------*/
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            /*---------------------do action on swipe--------------------------*/
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Item in recyclerview
                val position = viewHolder.adapterPosition
                //Item from database (teacherItem?.rid gives the id)
                val examItem: ExamSubjectYearExamtype = adapter.getExamAt(position)!!

                val exam = Exam(examItem.exam.esid, examItem.exam.eetid, examItem.exam.eyid, examItem.exam.egrade, examItem.exam.edateyear, examItem.exam.edatemonth, examItem.exam.edateday)
                exam.eid = examItem.exam.eid

                examViewModel.delete(exam)

                // showing snack bar with Undo option
                val snackbar = Snackbar
                        .make(cl_exam, "Prüfung erfolgreich gelöscht", 8000) //ms --> 8sec

                // undo is selected, restore the deleted item
                snackbar.setAction("UNDO") {

                    snackbar.dismiss()
                    val snackbar2 = Snackbar
                            .make(cl_exam, "Prüfung erfolgreich wiederhergestellt", Snackbar.LENGTH_SHORT)
                    snackbar2.show()
                    examViewModel.insert(exam)

                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()

            }

            /*---------------------ADD trash bin icon to background--------------------------*/
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_sweep) }

                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX > 0) { // Swiping to the right
                    val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
                    val iconRight = itemView.left + iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                } else if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                icon.draw(c)
            }
        }).attachToRecyclerView(recyclerView)


        //Return the inflated layout
        return root

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
            Log.d(TAG, "fetched grade: $egrade");
            val exam = Exam(sid, etid, activeYearID, egrade, edateyear, edatemonth, edateday)
            Log.d(TAG, "fetched exam: $exam");

            /*---------------------If the Request was a ADD subject request--------------------------*/
            if (requestCode == ADD_EXAM_REQUEST) {

                if (sid.equals(-1) || etid.equals(-1)) {
                    val snackbar: Snackbar
                    snackbar = Snackbar
                            .make(cl_exam, "Failed to add Exam", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }


                examViewModel.insert(exam)


                /*---------------------If the Request was a EDIT teacher request--------------------------*/
            } else if (requestCode == EDIT_EXAM_REQUEST) {
                val id = data.getIntExtra(ActivityAddEditExam.EXTRA_EID, -1)

                if (sid.equals(-1) || etid.equals(-1) || id.equals(-1)) {
                    val snackbar: Snackbar
                    snackbar = Snackbar
                            .make(cl_exam, "Failed to update Exam!", Snackbar.LENGTH_LONG)
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
}