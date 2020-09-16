package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
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
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.examtype.ExamtypeListAdapter
import com.ebner.stundenplan.database.table.examtype.ExamtypeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentExamtype : Fragment(), ExamtypeListAdapter.OnItemClickListener {


    private lateinit var examtypeViewModel: ExamtypeViewModel
    private lateinit var clExamtype: CoordinatorLayout

    companion object {
        private const val ADD_EXAMTYPE_REQUEST = 1
        private const val EDIT_EXAMTYPE_REQUEST = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_examtype, container, false)

        activity?.title = getString(R.string.fragment_examtypes)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


        /*---------------------Link items to Layout--------------------------*/
        clExamtype = root.findViewById(R.id.cl_examtype)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_examtype)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_examtype_addExam)


        val adapter = ExamtypeListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        examtypeViewModel = ViewModelProvider(this).get(ExamtypeViewModel::class.java)
        //Automatic update the recyclerlayout
        examtypeViewModel.allExamtype.observe(viewLifecycleOwner, Observer { examtypes ->
            examtypes.let { adapter.submitList(it) }
        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditExamtype::class.java)
            startActivityForResult(intent, ADD_EXAMTYPE_REQUEST)
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
                //Item from database (ExamtypeItem?.rid gives the id)
                val examtypeitem = adapter.getExamtypeAt(position)

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context!!)
                        .setTitle("Achtung")
                        .setMessage("Es wird die Prüfungsart ${examtypeitem?.etname} und alle zugehörigen Fächer, Prüfungen und Aufgaben gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!")
                        .setPositiveButton("Löschen") { _, _ ->
                            examtypeitem?.let { examtypeViewModel.delete(it) }
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clExamtype, "Prüfungsart ${examtypeitem?.etname} erfolgreich gelöscht!", 8000) //ms --> 8sec
                            snackbar.show()
                        }
                        .setNegativeButton("Abbrechen") { _, _ ->
                            adapter.notifyItemChanged(position)
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

    /*---------------------when returning from |ActivityAddEditExamtype| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*---------------------If the Request was successful--------------------------*/
        if (resultCode == Activity.RESULT_OK) {
            val etname = data!!.getStringExtra(ActivityAddEditExamtype.EXTRA_ETNAME)
            val etweight = data.getDoubleExtra(ActivityAddEditExamtype.EXTRA_ETWEIGHT, -1.0)
            val examtype = Examtype(etname, etweight)

            /*---------------------If the Request was a ADD Examtype request--------------------------*/
            if (requestCode == ADD_EXAMTYPE_REQUEST && resultCode == Activity.RESULT_OK) {

                examtypeViewModel.insert(examtype)

                /*---------------------If the Request was a EDIT Examtype request--------------------------*/
            } else if (requestCode == EDIT_EXAMTYPE_REQUEST && resultCode == Activity.RESULT_OK) {
                val id = data.getIntExtra(ActivityAddEditExamtype.EXTRA_ETID, -1)

                if (id == -1) {
                    val snackbar = Snackbar
                            .make(clExamtype, "Failed to update Examtype!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                examtype.etid = id
                examtypeViewModel.update(examtype)
            }
        }
    }


    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(examtype: Examtype) {
        val intent = Intent(context, ActivityAddEditExamtype::class.java)
        intent.putExtra(ActivityAddEditExamtype.EXTRA_ETID, examtype.etid)
        intent.putExtra(ActivityAddEditExamtype.EXTRA_ETNAME, examtype.etname)
        intent.putExtra(ActivityAddEditExamtype.EXTRA_ETWEIGHT, examtype.etweight)
        startActivityForResult(intent, EDIT_EXAMTYPE_REQUEST)

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
