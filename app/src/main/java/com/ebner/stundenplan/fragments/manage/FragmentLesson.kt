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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.lesson.LessonListAdapter
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentLesson : Fragment(), LessonListAdapter.OnItemClickListener {


    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var clLesson: CoordinatorLayout

    private var activeYearID: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_lesson, container, false)

        activity?.title = getString(R.string.fragment_lessons)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


        /*---------------------Link items to Layout--------------------------*/
        clLesson = root.findViewById(R.id.cl_lesson)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_lesson)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_lesson_addLesson)


        val adapter = LessonListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        //Automatic update the recyclerlayout
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.year.yid


            lessonViewModel.allLesson(activeYearID).observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })

        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditLesson::class.java)
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
                //Item from database (teacherItem?.rid gives the id)
                val lessonItem = adapter.getLessonAt(position)!!


                val lesson = Lesson(lessonItem.lesson.lday, lessonItem.lesson.lslid, lessonItem.lesson.lsid, lessonItem.lesson.lyid)
                lesson.lid = lessonItem.lesson.lid

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context!!)
                        .setTitle("Achtung")
                        .setMessage("Es wird die Stunde ${lessonItem.subject.sname} gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!")
                        .setPositiveButton("Löschen") { _, _ ->
                            lessonViewModel.delete(lesson)
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clLesson, "Stunde ${lessonItem.subject.sname} erfolgreich gelöscht!", 5000) //ms --> 8sec
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

    /*---------------------when returning from |ActivityAddEditLesson| do something--------------------------*/
    private val openAddEditActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            //Save extras to vars
            val data = result.data!!
            val lday = data.getIntExtra(ActivityAddEditLesson.EXTRA_LDAY, -1)
            val lslid = data.getIntExtra(ActivityAddEditLesson.EXTRA_L_SLID, -1)
            val lsid = data.getIntExtra(ActivityAddEditLesson.EXTRA_L_SID, -1)

            val lesson = Lesson(lday, lslid, lsid, activeYearID)

            /*---------------------If the Request was a EDIT lesson request--------------------------*/
            if (data.hasExtra(ActivityAddEditLesson.EXTRA_LID)) {
                val id = data.getIntExtra(ActivityAddEditLesson.EXTRA_LID, -1)

                if (lslid == -1 || lsid == -1 || activeYearID == -1 || id == -1) {
                    val snackbar = Snackbar
                            .make(clLesson, "Failed to update Lesson!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }

                lesson.lid = id
                lessonViewModel.update(lesson)


            } else {
                /*---------------------Else the request was a ADD lesson request--------------------------*/
                if (lslid == -1 || lsid == -1 || activeYearID == -1) {
                    val snackbar = Snackbar
                            .make(clLesson, "Failed to add Lesson", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }
                lessonViewModel.insert(lesson)
            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(lessonSubjectSchoollessonYear: LessonSubjectSchoollessonYear) {
        val intent = Intent(context, ActivityAddEditLesson::class.java)
        intent.putExtra(ActivityAddEditLesson.EXTRA_LID, lessonSubjectSchoollessonYear.lesson.lid)
        intent.putExtra(ActivityAddEditLesson.EXTRA_LDAY, lessonSubjectSchoollessonYear.lesson.lday)
        intent.putExtra(ActivityAddEditLesson.EXTRA_L_SLID, lessonSubjectSchoollessonYear.lesson.lslid)
        intent.putExtra(ActivityAddEditLesson.EXTRA_L_SID, lessonSubjectSchoollessonYear.lesson.lsid)
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