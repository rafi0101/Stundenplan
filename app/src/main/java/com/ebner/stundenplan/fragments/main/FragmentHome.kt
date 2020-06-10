package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ebner.stundenplan.BuildConfig
import com.ebner.stundenplan.MainActivity
import com.ebner.stundenplan.R
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentHome : Fragment() {


    private lateinit var ibtnTimetable: ImageButton
    private lateinit var ibtnSubject: ImageButton
    private lateinit var ibtnTask: ImageButton
    private lateinit var ibtnYear: ImageButton
    private lateinit var ibtnExam: ImageButton


    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val versionName = BuildConfig.VERSION_NAME
        val tvVersionNumber = root.findViewById<TextView>(R.id.tv_version_number)
        tvVersionNumber.text = versionName

        val mainactivity = (activity as MainActivity)

        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        ibtnTimetable = root.findViewById(R.id.ibtn_timetable)
        ibtnSubject = root.findViewById(R.id.ibtn_subject)
        ibtnTask = root.findViewById(R.id.ibtn_task)
        ibtnYear = root.findViewById(R.id.ibtn_year)
        ibtnExam = root.findViewById(R.id.ibtn_exam)

        ibtnTimetable.setOnClickListener {
            mainactivity.changeFragment(R.id.nav_timetable)
        }

        ibtnSubject.setOnClickListener {
            mainactivity.changeFragment(R.id.nav_subject)
        }

        ibtnTask.setOnClickListener {
            mainactivity.changeFragment(R.id.nav_task)
        }

        ibtnYear.setOnClickListener {
            mainactivity.changeFragment(R.id.nav_year)
        }

        ibtnExam.setOnClickListener {
            mainactivity.changeFragment(R.id.nav_exam)
        }


        return root
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