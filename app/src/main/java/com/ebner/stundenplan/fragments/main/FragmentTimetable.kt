package com.ebner.stundenplan.fragments.main


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alamkanak.weekview.OnEventClickListener
import com.alamkanak.weekview.OnEventLongClickListener
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.ebner.stundenplan.R
import com.ebner.stundenplan.SubjectExamsActivity
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.mergedEntities.LessonEvent
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.time.DayOfWeek
import java.util.*
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentTimetable : Fragment(), OnEventClickListener<LessonEvent>, OnEventLongClickListener<LessonEvent> {

    private val TAG = "debug_FragmentTimetable"

    private val weekView: WeekView<LessonEvent> by lazy {
        requireActivity().findViewById<WeekView<LessonEvent>>(R.id.weekView)
    }
    private lateinit var pbTimetable: ProgressBar

    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private var activeYearID: Int = -1


    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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




        return root
    }


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

    override fun onEventLongClick(data: LessonEvent, eventRect: RectF) {
        pbTimetable.visibility = View.VISIBLE

        //Vibrate
        view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

        //Fetch lesson in IO thread
        CoroutineScope(IO).launch {

            val lesson = lessonViewModel.singleLesson(data.id.toInt())

            //Create AlertDialog with 3 options what to do
            withContext(Main) {
                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context)
                        .setTitle("${lesson.subject.sname} am ${DayOfWeek.of(lesson.lesson.lday)}")
                        .setMessage("Was möchtes du unternehmen?")
                        .setPositiveButton("Löschen") { dialog, _ ->
                            lessonViewModel.delete(lesson.lesson)
                            pbTimetable.visibility = View.INVISIBLE
                            dialog.dismiss()
                        }
                        .setNeutralButton("Abbrechen") { _, _ ->
                            pbTimetable.visibility = View.INVISIBLE
                        }
                        .setNegativeButton("Ändern") { dialog, _ ->
                            //Will be added soon
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