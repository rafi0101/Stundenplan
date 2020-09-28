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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonListAdapter
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentSchoolLesson : Fragment(), SchoolLessonListAdapter.OnItemClickListener {

    private lateinit var schoolLessonViewModel: SchoolLessonViewModel
    private lateinit var clSchoollesson: CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_school_lesson, container, false)

        activity?.title = getString(R.string.fragment_schoollessons)

        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        clSchoollesson = root.findViewById(R.id.cl_schoollesson)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_schoollesson)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_schoollesson_addSchoollesson)


        val adapter = SchoolLessonListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        schoolLessonViewModel = ViewModelProvider(this).get(SchoolLessonViewModel::class.java)
        //Automatic update the recyclerlayout
        schoolLessonViewModel.allSchoolLesson.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditSchoolLesson::class.java)
            openAddEditActivity.launch(intent)
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
                val schoolLessonItem = adapter.getSchoolLessonAt(position)!!

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context!!)
                        .setTitle("Achtung")
                        .setMessage("Es wird die Schulstunde ${schoolLessonItem.slnumber} und alle zugehörigen Stunden im Stundenplan gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!")
                        .setPositiveButton("Löschen") { _, _ ->
                            schoolLessonViewModel.delete(schoolLessonItem)
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clSchoollesson, "Schulstunde  ${schoolLessonItem.slnumber} erfolgreich gelöscht!", 8000) //ms --> 8sec
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

    /*---------------------when returning from |ActivityAddEditSchoolLesson| do something--------------------------*/
    private val openAddEditActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        /*---------------------If the request was successful--------------------------*/
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            //Save extras to vars
            val data = result.data!!
            val slnumber = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLNUMBER, -1)
            val slstarthour = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLSTARTHOUR, -1)
            val slstartminute = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLSTARTMINUTE, -1)
            val slendhour = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLENDHOUR, -1)
            val slendminute = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLENDMINUTE, -1)

            val schoolLesson = SchoolLesson(slnumber, slstarthour, slstartminute, slendhour, slendminute)

            /*---------------------if the request was a edit schoollesson request--------------------------*/
            if (data.hasExtra(ActivityAddEditSchoolLesson.EXTRA_SLID)) {
                val id = data.getIntExtra(ActivityAddEditSchoolLesson.EXTRA_SLID, -1)

                if (id == -1) {
                    val snackbar = Snackbar
                            .make(clSchoollesson, "Failed to update SchoolLesson!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }

                schoolLesson.slid = id
                schoolLessonViewModel.update(schoolLesson)

                /*---------------------else the request was a add schoollesson request--------------------------*/
            } else {
                schoolLessonViewModel.insert(schoolLesson)
            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(schoolLesson: SchoolLesson) {
        val intent = Intent(context, ActivityAddEditSchoolLesson::class.java)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLID, schoolLesson.slid)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLNUMBER, schoolLesson.slnumber)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLSTARTHOUR, schoolLesson.slstarthour)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLSTARTMINUTE, schoolLesson.slstartminute)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLENDHOUR, schoolLesson.slendhour)
        intent.putExtra(ActivityAddEditSchoolLesson.EXTRA_SLENDMINUTE, schoolLesson.slendminute)
        openAddEditActivity.launch(intent)
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