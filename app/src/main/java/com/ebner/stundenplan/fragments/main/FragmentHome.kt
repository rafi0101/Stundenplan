package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.BuildConfig
import com.ebner.stundenplan.MainActivity
import com.ebner.stundenplan.R
import com.ebner.stundenplan.customAdapter.HomeListAdapter
import com.ebner.stundenplan.customObjects.HomeField
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentHome : Fragment(), HomeListAdapter.OnItemClickListener {

    private lateinit var rvHomeItems: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val versionName = BuildConfig.VERSION_NAME
        val tvVersionNumber = root.findViewById<TextView>(R.id.tv_version_number)
        tvVersionNumber.text = versionName

        activity?.title = getString(R.string.app_name)

        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        rvHomeItems = root.findViewById(R.id.rv_home_items)

        val homeItemList = listOf(
                HomeField(1, "Stundenplan", R.drawable.ic_schedule, R.color.green_500),
                HomeField(2, "Fächer", R.drawable.ic_widgets),
                HomeField(3, "Aufgaben", R.drawable.ic_work),
                HomeField(4, "Klassen", R.drawable.ic_year),
                HomeField(5, "Prüfungen", R.drawable.ic_grade))

        rvHomeItems.setHasFixedSize(true)

        val layoutManager: GridLayoutManager
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            layoutManager = GridLayoutManager(context, homeItemList.size, RecyclerView.VERTICAL, false)
        } else {
            // In portrait
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0 -> 2
                        else -> 1
                    }
                }
            }
        }

        rvHomeItems.layoutManager = layoutManager

        val adapter = HomeListAdapter(this)
        rvHomeItems.adapter = adapter
        adapter.submitList(homeItemList)
        rvHomeItems.addItemDecoration(HomeFieldItemDecoration(16, 16))

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

    override fun onItemClicked(home: HomeField) {
        val mainactivity = (activity as MainActivity)
        when (home.id) {
            1 -> mainactivity.changeFragment(R.id.nav_timetable)
            2 -> mainactivity.changeFragment(R.id.nav_subject)
            3 -> mainactivity.changeFragment(R.id.nav_task)
            4 -> mainactivity.changeFragment(R.id.nav_year)
            5 -> mainactivity.changeFragment(R.id.nav_exam)
            else -> mainactivity.changeFragment(R.id.nav_home)
        }
    }
}