package com.ebner.stundenplan.fragments.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.ebner.stundenplan.database.table.mergedEntities.TaskLesson
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.ebner.stundenplan.database.table.task.Task
import com.ebner.stundenplan.database.table.task.TaskListAdapter
import com.ebner.stundenplan.database.table.task.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentTask : Fragment(), TaskListAdapter.OnItemClickListener {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var clTask: CoordinatorLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter


    private var activeYearID: Int = -1
    private var selectedSubject: Int = -1
    private var selectedFinished: Int = -1

    companion object {
        private const val ADD_TASK_REQUEST = 1
        private const val EDIT_TASK_REQUEST = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_task, container, false)

        activity?.title = getString(R.string.fragment_tasks)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


        /*---------------------Link items to Layout--------------------------*/
        clTask = root.findViewById(R.id.cl_task)
        recyclerView = root.findViewById<RecyclerView>(R.id.rv_task)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_task_addTask)
        val dropdownSubject: AutoCompleteTextView = root.findViewById(R.id.actv_dropdown_subject)
        val dropdownFinished: AutoCompleteTextView = root.findViewById(R.id.actv_dropdown_finished)


        adapter = TaskListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        //Automatic update the recyclerlayout

        //Get current activeYearID
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid

            updateRecyclerView()
        })


        val subjectsList: MutableList<Subject> = mutableListOf(Subject("Auswählen", "", -1, "", true, -1, -1))
        val selectFinishedList = listOf("Alle", "Ja", "Nein")

        runBlocking {
            //Get a List of all Subjects
            subjectsList.addAll(subjectViewModel.allSubjectList() as MutableList<Subject>)
        }

        val dropDownAdapterSubject = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, subjectsList)
        dropdownSubject.setAdapter(dropDownAdapterSubject)
        val dropDownAdapterFinished = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, selectFinishedList)
        dropdownFinished.setAdapter(dropDownAdapterFinished)


        dropdownSubject.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedSubjectSubject = parent.adapter.getItem(position) as Subject
            selectedSubject = selectedSubjectSubject.sid

            if (selectedSubjectSubject.sname == "Auswählen" && selectedSubjectSubject.scolor == -1 && selectedSubjectSubject.sinactive) {
                selectedSubject = -1
            }

            updateRecyclerView()
        }
        dropdownFinished.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedFinished = position
            if (parent.adapter.getItem(position).toString() == "Alle") {
                selectedFinished = -1
            }
            updateRecyclerView()
        }


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditTask::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST)
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
                val taskItem: TaskLesson = adapter.getTaskAt(position)!!

                val task = Task(taskItem.task.tkname, taskItem.task.tknote, taskItem.task.tkdateday, taskItem.task.tkdatemonth, taskItem.task.tkdateyear, taskItem.task.tkfinished, taskItem.task.tklid, activeYearID)
                task.tkid = taskItem.task.tkid

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context!!)
                        .setTitle("Achtung")
                        .setMessage("Es wird die Aufgabe ${task.tkname} gelöscht")
                        .setPositiveButton("Löschen") { _, _ ->
                            taskViewModel.delete(task)
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clTask, "Aufgabe ${task.tkname} erfolgreich gelöscht!", 5000) //ms --> 8sec
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

    /*---------------------Update Recycler View--------------------------*/
    private fun updateRecyclerView() {

        if (selectedSubject == -1 && selectedFinished == -1) {
            taskViewModel.allTask(activeYearID).observe(viewLifecycleOwner, Observer { tasks ->
                adapter.submitList(tasks)
            })
        } else if (selectedSubject != -1 && selectedFinished == -1) {
            taskViewModel.allTaskBySubject(activeYearID, selectedSubject).observe(viewLifecycleOwner, Observer { tasks ->
                adapter.submitList(tasks)
            })
        } else if (selectedSubject == -1 && selectedFinished != -1) {
            //If selectedFinished == 1, then true, else (if == 2) = false
            val calculatedFinished = selectedFinished == 1
            taskViewModel.allTaskByFinished(activeYearID, calculatedFinished).observe(viewLifecycleOwner, Observer { tasks ->
                adapter.submitList(tasks)
            })

        } else if (selectedSubject != -1 && selectedFinished != -1) {
            //If selectedFinished == 1, then true, else (if == 2) = false
            val calculatedFinished = selectedFinished == 1
            taskViewModel.allTaskBySubjectFinished(activeYearID, selectedSubject, calculatedFinished).observe(viewLifecycleOwner, Observer { tasks ->
                adapter.submitList(tasks)
            })

        }

        recyclerView.smoothScrollToPosition(0)
    }


    /*---------------------when returning from |ActivityAddEditSubject| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Save extras to vars
            val tkname: String = data!!.getStringExtra(ActivityAddEditTask.EXTRA_TKNAME)
            val tknote = data.getStringExtra(ActivityAddEditTask.EXTRA_TKNOTE)
            val tkdateday = data.getIntExtra(ActivityAddEditTask.EXTRA_TKDATEDAY, -1)
            val tkdatemonth = data.getIntExtra(ActivityAddEditTask.EXTRA_TKDATEMONTH, -1)
            val tkdateyear = data.getIntExtra(ActivityAddEditTask.EXTRA_TKDATEYEAR, -1)
            val tkfinished = data.getBooleanExtra(ActivityAddEditTask.EXTRA_TKFINISHED, false)
            val tklid = data.getIntExtra(ActivityAddEditTask.EXTRA_TKLID, -1)

            val task = Task(tkname, tknote, tkdateday, tkdatemonth, tkdateyear, tkfinished, tklid, activeYearID)

            /*---------------------If the Request was a ADD subject request--------------------------*/
            if (requestCode == ADD_TASK_REQUEST) {

                if (tklid == -1) {
                    val snackbar = Snackbar
                            .make(clTask, "Failed to add Task", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }


                taskViewModel.insert(task)


                /*---------------------If the Request was a EDIT teacher request--------------------------*/
            } else if (requestCode == EDIT_TASK_REQUEST) {
                val id = data.getIntExtra(ActivityAddEditTask.EXTRA_TKID, -1)

                if (tklid == -1 || id == -1) {
                    val snackbar = Snackbar
                            .make(clTask, "Failed to update Task!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                task.tkid = id
                taskViewModel.update(task)


            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(taskLesson: TaskLesson) {
        val intent = Intent(context, ActivityAddEditTask::class.java)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKID, taskLesson.task.tkid)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKNAME, taskLesson.task.tkname)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKNOTE, taskLesson.task.tknote)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKDATEDAY, taskLesson.task.tkdateday)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKDATEMONTH, taskLesson.task.tkdatemonth)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKDATEYEAR, taskLesson.task.tkdateyear)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKFINISHED, taskLesson.task.tkfinished)
        intent.putExtra(ActivityAddEditTask.EXTRA_TKLID, taskLesson.task.tklid)
        startActivityForResult(intent, EDIT_TASK_REQUEST)

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