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
import androidx.fragment.app.FragmentTransaction
import com.ebner.stundenplan.BuildConfig
import com.ebner.stundenplan.R
import com.ebner.stundenplan.fragments.manage.FragmentSubject
import com.ebner.stundenplan.fragments.manage.FragmentYear
import com.google.android.material.navigation.NavigationView
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentHome : Fragment() {

    private val TAG = "debug_FragmentHome"


    private lateinit var ibtn_timetable: ImageButton
    private lateinit var ibtn_subject: ImageButton
    private lateinit var ibtn_task: ImageButton
    private lateinit var ibtn_year: ImageButton
    private lateinit var ibtn_exam: ImageButton


    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val versionName = BuildConfig.VERSION_NAME
        val tv_version_number = root.findViewById<TextView>(R.id.tv_version_number)
        tv_version_number.text = versionName


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        ibtn_timetable = root.findViewById(R.id.ibtn_timetable)
        ibtn_subject = root.findViewById(R.id.ibtn_subject)
        ibtn_task = root.findViewById(R.id.ibtn_task)
        ibtn_year = root.findViewById(R.id.ibtn_year)
        ibtn_exam = root.findViewById(R.id.ibtn_exam)

        ibtn_timetable.setOnClickListener {
            changeFragment(FragmentTimetable())
        }

        ibtn_subject.setOnClickListener {
            changeFragment(FragmentSubject())
        }

        ibtn_task.setOnClickListener {
            changeFragment(FragmentTask())
        }

        ibtn_year.setOnClickListener {
            changeFragment(FragmentYear())
        }

        ibtn_exam.setOnClickListener {
            changeFragment(FragmentExam())
        }






        return root
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction: FragmentTransaction
        transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        //transaction.addToBackStack(null); //need when you can press back and something should happen (go to last fragment)
        transaction.commit()

        val navigationView = activity!!.findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setCheckedItem(R.id.nav_home)
        when (fragment) {
            is FragmentTimetable -> navigationView.setCheckedItem(R.id.nav_timetable)
            is FragmentSubject -> navigationView.setCheckedItem(R.id.nav_subject)
            is FragmentTask -> navigationView.setCheckedItem(R.id.nav_task)
            is FragmentYear -> navigationView.setCheckedItem(R.id.nav_year)
            is FragmentExam -> navigationView.setCheckedItem(R.id.nav_exam)
            else -> {
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
        return dp * (context.getResources().getDisplayMetrics().densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}