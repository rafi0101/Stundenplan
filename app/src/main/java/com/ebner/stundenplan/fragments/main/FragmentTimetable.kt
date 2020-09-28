package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alamkanak.weekview.OnEventClickListener
import com.alamkanak.weekview.OnEventLongClickListener
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.ebner.stundenplan.R
import com.ebner.stundenplan.SubjectExamsActivity
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.mergedEntities.LessonEvent
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.fragments.manage.ActivityAddEditLesson
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass. Explanation for this Fragment -->
 *
 * The Timetable works as Following:
 * Every lesson with [Subject], day, and [SchoolLesson] is saved in the [Lesson] Table (merged as one Object in [LessonSubjectSchoollessonYear])
 * 1. All [Lesson]s are fetched as [LessonSubjectSchoollessonYear] (depending on the current active year)
 * 2. Every [Lesson] is converted in a [LessonEvent] Object and added to a [MutableList]<[WeekViewDisplayable]>
 *   The [Lesson.lid] is always the same as the [LessonEvent.id] so it is easy the get the Original [Lesson] Object back from it's [LessonEvent]
 * 3. This MutableList is submitted to the [weekView] and shown in the Timetable
 * The Timetable is limited to the current Week. Horizontal scrolling is disabled
 * When the User [onEventClick] or [onEventLongClick] on a [LessonEvent] the selected [Lesson] is fetched from the Database, and you see an
 *   overview [SubjectExamsActivity], you change [ActivityAddEditLesson] or delete it
 */
class FragmentTimetable : Fragment(), OnEventClickListener<LessonEvent>, OnEventLongClickListener<LessonEvent> {

    private val weekView: WeekView<LessonEvent> by lazy { requireActivity().findViewById<WeekView<LessonEvent>>(R.id.weekView) }

    private lateinit var pbTimetable: ProgressBar
    private lateinit var clTimetable: CoordinatorLayout
    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    private var activeYearID: Int = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_timetable, container, false)

        activity?.title = getString(R.string.fragment_timetable)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(0F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        pbTimetable = root.findViewById(R.id.pb_timetable)
        clTimetable = root.findViewById(R.id.cl_timetable)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_timetable_addLesson)

        lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid


            lessonViewModel.allLesson(activeYearID).observe(viewLifecycleOwner, Observer { lessons ->

                val lessonEvent = mutableListOf<WeekViewDisplayable<LessonEvent>>()
                var lastEventTime = 15

                //runBlocking: wait with weekView.submit until all lessons are added to the list
                runBlocking {

                    //Add Each lesson to the list
                    lessons.forEach {

                        //save current Year and Month
                        val calendar = Calendar.getInstance()
                        val currentYear = calendar.get(Calendar.YEAR)
                        val currentMonth = calendar.get(Calendar.MONTH)

                        val id = it.lesson.lid.toLong()
                        val title = it.subject.sname
                        //Transform lesson starthour, minute and day to a calendar item
                        val startTime = Calendar.getInstance().apply {
                            set(Calendar.YEAR, currentYear)
                            set(Calendar.MONTH, currentMonth)
                            set(Calendar.DAY_OF_WEEK, it.lesson.lday + 1)
                            set(Calendar.HOUR_OF_DAY, it.schoolLesson.slstarthour)
                            set(Calendar.MINUTE, it.schoolLesson.slstartminute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        //Transform lesson endhour, minute and day to a calendar item
                        val endTime = Calendar.getInstance().apply {
                            set(Calendar.YEAR, currentYear)
                            set(Calendar.MONTH, currentMonth)
                            set(Calendar.DAY_OF_WEEK, it.lesson.lday + 1)
                            set(Calendar.HOUR_OF_DAY, it.schoolLesson.slendhour)
                            set(Calendar.MINUTE, it.schoolLesson.slendminute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        //location: room number/name and teacher in the next line
                        val location = "\nOrt: ${it.room.rname}\nLehrer: ${it.teacher.tname}"
                        val color = it.subject.scolor

                        val isCanceled = false

                        //IF last lesson is greater then lastEventTime, expand the view
                        if (it.schoolLesson.slendhour >= lastEventTime) {
                            lastEventTime = it.schoolLesson.slendhour + 2
                        }


                        lessonEvent.add(LessonEvent(id, title, startTime, endTime, location, color, false, isCanceled))

                    }
                }

                weekView.maxHour = lastEventTime
                weekView.submit(lessonEvent)
                pbTimetable.visibility = View.INVISIBLE


            })
            weekView.onEventClickListener = this
            weekView.onEventLongClickListener = this

        })

        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditLesson::class.java)
            openAddEditActivity.launch(intent)
        }


        return root
    }

    /*---------------------Normal Click on Item, to get overview about Subject--------------------------*/
    override fun onEventClick(data: LessonEvent, eventRect: RectF) {
        pbTimetable.visibility = View.VISIBLE

        CoroutineScope(IO).launch {
            //Fetch lesson in IO thread
            val lesson = lessonViewModel.singleLesson(data.id.toInt())

            //Start Activity in Main thread to see all information about this subject
            withContext(Main) {

                val intent = Intent(context, SubjectExamsActivity::class.java)
                intent.putExtra(SubjectExamsActivity.EXTRA_SID, lesson.subject.sid)
                intent.putExtra(SubjectExamsActivity.EXTRA_SNAME, lesson.subject.sname)
                intent.putExtra(SubjectExamsActivity.EXTRA_SCOLOR, lesson.subject.scolor)
                startActivity(intent)

                //Wait 1s in IO thread
                withContext(IO) {
                    delay(1000)
                }
                //Make the Progressbar after this second invisible
                withContext(Main) {
                    pbTimetable.visibility = View.INVISIBLE
                }
            }
        }
    }

    /*---------------------Long Click on Item, to change or delete it--------------------------*/
    override fun onEventLongClick(data: LessonEvent, eventRect: RectF) {
        pbTimetable.visibility = View.VISIBLE

        //Vibrate
        view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

        //Fetch lesson in IO thread
        CoroutineScope(IO).launch {

            val lessonSubjectSchoollessonYear = lessonViewModel.singleLesson(data.id.toInt())
            //Replace Int of Day to String
            val lessonDay = when (lessonSubjectSchoollessonYear.lesson.lday) {
                1 -> "Montag"
                2 -> "Dienstag"
                3 -> "Mittwoch"
                4 -> "Donnerstag"
                5 -> "Freitag"
                6 -> "Samstag"
                7 -> "Sonntag"
                else -> ""
            }

            //Create AlertDialog with 3 options what to do
            withContext(Main) {
                /*---------------------Change or Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle("${lessonSubjectSchoollessonYear.subject.sname} am $lessonDay in der ${lessonSubjectSchoollessonYear.schoolLesson.slnumber} Stunde")
                        .setMessage("Was möchtes du unternehmen?")
                        .setPositiveButton("Löschen") { dialog, _ ->
                            lessonViewModel.delete(lessonSubjectSchoollessonYear.lesson)
                            pbTimetable.visibility = View.INVISIBLE
                            dialog.dismiss()
                            // show snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clTimetable, "Stunde gelöscht", 8000)
                            snackbar.setAction("UNDO") {
                                // undo is selected, restore the deleted item
                                lessonViewModel.insert(lessonSubjectSchoollessonYear.lesson)
                            }
                            snackbar.setActionTextColor(Color.YELLOW)
                            snackbar.show()

                        }
                        .setNeutralButton("Abbrechen") { _, _ ->
                            pbTimetable.visibility = View.INVISIBLE
                        }
                        /*---------------------Start AddEdit Activity, to edit (this) entry--------------------------*/
                        .setNegativeButton("Ändern") { dialog, _ ->
                            val intent = Intent(context, ActivityAddEditLesson::class.java)
                            intent.putExtra(ActivityAddEditLesson.EXTRA_LID, lessonSubjectSchoollessonYear.lesson.lid)
                            intent.putExtra(ActivityAddEditLesson.EXTRA_LDAY, lessonSubjectSchoollessonYear.lesson.lday)
                            intent.putExtra(ActivityAddEditLesson.EXTRA_L_SLID, lessonSubjectSchoollessonYear.lesson.lslid)
                            intent.putExtra(ActivityAddEditLesson.EXTRA_L_SID, lessonSubjectSchoollessonYear.lesson.lsid)
                            openAddEditActivity.launch(intent)

                            pbTimetable.visibility = View.INVISIBLE
                            dialog.dismiss()
                        }
                        .setOnCancelListener {
                            pbTimetable.visibility = View.INVISIBLE
                        }
                        .show()
            }
        }
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
                            .make(clTimetable, "Failed to update Lesson!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }

                lesson.lid = id
                lessonViewModel.update(lesson)


            } else {
                /*---------------------Else the request was a ADD lesson request--------------------------*/
                if (lslid == -1 || lsid == -1 || activeYearID == -1) {
                    val snackbar = Snackbar
                            .make(clTimetable, "Failed to add Lesson", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }
                lessonViewModel.insert(lesson)
            }
        }
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