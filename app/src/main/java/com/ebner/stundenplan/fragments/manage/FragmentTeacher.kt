package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
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
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.teacher.TeacherListAdapter
import com.ebner.stundenplan.database.table.teacher.TeacherViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class FragmentTeacher : Fragment(), TeacherListAdapter.onItemClickListener {

    private lateinit var teacherViewModel: TeacherViewModel
    private lateinit var cl_teacher: CoordinatorLayout

    companion object {
        private const val ADD_TEACHER_REQUEST = 1
        private const val EDIT_TEACHER_REQUEST = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_teacher, container, false)

        activity?.title = getString(R.string.fragment_teachers)

        /*---------------------Link items to Layout--------------------------*/
        cl_teacher = root.findViewById(R.id.cl_teacher)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_teacher)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_teacher_addTeacher)


        val adapter = TeacherListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        teacherViewModel = ViewModelProvider(this).get(TeacherViewModel::class.java)
        //Automatic update the recyclerlayout
        teacherViewModel.allTeacher.observe(viewLifecycleOwner, Observer { teachers ->
            teachers.let { adapter.submitList(it) }
        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditTeacher::class.java)
            startActivityForResult(intent, ADD_TEACHER_REQUEST)
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
                val teacherItem = adapter.getTeacherAt(position)

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context)
                        .setTitle("Achtung")
                        .setMessage("Es wird der Lehrer ${teacherItem?.tname} und alle zugehörigen Fächer, Prüfungen und Aufgaben gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!")
                        .setPositiveButton("Löschen") { dialog, which ->
                            teacherItem?.let { teacherViewModel.delete(it) }
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(cl_teacher, "Lehrer ${teacherItem?.tname} erfolgreich gelöscht!", 8000) //ms --> 8sec
                            snackbar.show()
                        }
                        .setNegativeButton("Abbrechen") { dialog, which ->
                            adapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener { dialog: DialogInterface? ->
                            adapter.notifyItemChanged(position)
                        }
                        .show()
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

    /*---------------------when returning from |ActivityAddEditTeacher| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*---------------------If the Request was successful--------------------------*/
        if (resultCode == Activity.RESULT_OK) {
            val tname = data!!.getStringExtra(ActivityAddEditTeacher.EXTRA_TNAME)
            val teacher = Teacher(tname)

            /*---------------------If the Request was a ADD teacher request--------------------------*/
            if (requestCode == ADD_TEACHER_REQUEST && resultCode == Activity.RESULT_OK) {

                teacherViewModel.insert(teacher)

                /*---------------------If the Request was a EDIT teacher request--------------------------*/
            } else if (requestCode == EDIT_TEACHER_REQUEST && resultCode == Activity.RESULT_OK) {
                val id = data.getIntExtra(ActivityAddEditTeacher.EXTRA_TID, -1)

                if (id == -1) {
                    val snackbar: Snackbar
                    snackbar = Snackbar
                            .make(cl_teacher, "Failed to update Teacher!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                teacher.tid = id
                teacherViewModel.update(teacher)
            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(teacher: Teacher) {
        val intent = Intent(context, ActivityAddEditTeacher::class.java)
        intent.putExtra(ActivityAddEditTeacher.EXTRA_TID, teacher.tid)
        intent.putExtra(ActivityAddEditTeacher.EXTRA_TNAME, teacher.tname)
        startActivityForResult(intent, EDIT_TEACHER_REQUEST)
    }

}