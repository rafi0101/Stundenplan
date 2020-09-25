package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectListAdapter
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FragmentSubject : Fragment(), SubjectListAdapter.OnItemClickListener {


    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var clSubject: CoordinatorLayout

    companion object {
        private const val ADD_SUBJECT_REQUEST = 1
        private const val EDIT_SUBJECT_REQUEST = 2
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_subject, container, false)

        activity?.title = getString(R.string.fragment_subjects)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


        /*---------------------Link items to Layout--------------------------*/
        clSubject = root.findViewById(R.id.cl_subject)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_subject)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_subject_addSubject)


        val adapter = SubjectListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        //Automatic update the recyclerlayout
        subjectViewModel.allSubject.observe(viewLifecycleOwner, Observer { subjects ->
            subjects.let { adapter.submitList(it) }
        })

        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditSubject::class.java)
            startActivityForResult(intent, ADD_SUBJECT_REQUEST)
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
                val subjectItem: SubjectTeacherRoom = adapter.getSubjectAt(position)!!

                val subject = Subject(subjectItem.subject.sname, subjectItem.subject.snameshort, subjectItem.subject.scolor, subjectItem.subject.snote, subjectItem.subject.sinactive, subjectItem.subject.stid, subjectItem.subject.srid)
                subject.sid = subjectItem.subject.sid

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context!!)
                        .setTitle("Achtung")
                        .setMessage("Es wird das Fach ${subject.sname} und alle zugehörigen Prüfungen sowie Aufgaben aller Jahre gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!\n" +
                                "Empfehlung: setze das Fach auf inaktiv")
                        .setPositiveButton("Löschen") { _, _ ->
                            subjectViewModel.delete(subject)
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clSubject, "Fach ${subject.sname} erfolgreich gelöscht!", 5000) //ms --> 8sec
                            snackbar.show()
                        }
                        .setNegativeButton("Abbrechen") { _, _ ->
                            adapter.notifyItemChanged(position)
                        }
                        .setNeutralButton("Inaktiv") { _, _ ->
                            adapter.notifyItemChanged(position)
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(500)
                                subject.sinactive = true
                                subjectViewModel.update(subject)
                            }

                        }
                        .setOnCancelListener {
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

    /*---------------------when returning from |ActivityAddEditSubject| do something--------------------------*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Save extras to vars
            val sname: String = data!!.getStringExtra(ActivityAddEditSubject.EXTRA_SNAME)
            val snameshort = data.getStringExtra(ActivityAddEditSubject.EXTRA_SNAMESHORT)
            val scolor = data.getIntExtra(ActivityAddEditSubject.EXTRA_SCOLOR, 0)
            val snote = data.getStringExtra(ActivityAddEditSubject.EXTRA_SNOTE)
            val sinactive = data.getBooleanExtra(ActivityAddEditSubject.EXTRA_SINACTIVE, false)
            val rid = data.getIntExtra(ActivityAddEditSubject.EXTRA_S_RID, -1)
            val tid = data.getIntExtra(ActivityAddEditSubject.EXTRA_S_TID, -1)
            val subject = Subject(sname, snameshort, scolor, snote, sinactive, tid, rid)

            /*---------------------If the Request was a ADD subject request--------------------------*/
            if (requestCode == ADD_SUBJECT_REQUEST) {

                if (rid == -1 || tid == -1) {
                    val snackbar = Snackbar
                            .make(clSubject, "Failed to add Subject", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }


                subjectViewModel.insert(subject)


                /*---------------------If the Request was a EDIT teacher request--------------------------*/
            } else if (requestCode == EDIT_SUBJECT_REQUEST) {
                val id = data.getIntExtra(ActivityAddEditSubject.EXTRA_SID, -1)

                if (rid == -1 || tid == -1 || id == -1) {
                    val snackbar = Snackbar
                            .make(clSubject, "Failed to update Subject!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                subject.sid = id
                subjectViewModel.update(subject)


            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(subjectTeacherRoom: SubjectTeacherRoom) {
        val intent = Intent(context, ActivityAddEditSubject::class.java)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SID, subjectTeacherRoom.subject.sid)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SNAME, subjectTeacherRoom.subject.sname)
        intent.putExtra(ActivityAddEditSubject.EXTRA_S_TID, subjectTeacherRoom.subject.stid)
        intent.putExtra(ActivityAddEditSubject.EXTRA_S_RID, subjectTeacherRoom.subject.srid)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SNAMESHORT, subjectTeacherRoom.subject.snameshort)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SINACTIVE, subjectTeacherRoom.subject.sinactive)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SNOTE, subjectTeacherRoom.subject.snote)
        intent.putExtra(ActivityAddEditSubject.EXTRA_SCOLOR, subjectTeacherRoom.subject.scolor)
        startActivityForResult(intent, EDIT_SUBJECT_REQUEST)

    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}
