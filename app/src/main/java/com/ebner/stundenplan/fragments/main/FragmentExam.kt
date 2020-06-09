package com.ebner.stundenplan.fragments.main

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ebner.stundenplan.R
import com.ebner.stundenplan.fragments.main.exam.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentExam : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam, container, false)

        activity?.title = getString(R.string.fragment_exams)

        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val sites: Int = convertDpToPixel(6F, root.context).roundToInt()
        val bottom: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(sites, 0, sites, bottom)
        fragmentmain.layoutParams = params

        /*---------------------Initialize the TabLayout--------------------------*/
        val viewPager: ViewPager2 = root.findViewById(R.id.vp_exam)
        val tabLayout: TabLayout = root.findViewById(R.id.tl_exam)

        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Ausstehend"
                1 -> tab.text = "Alle"
                2 -> tab.text = "Übersicht"
            }
        }.attach()


        //Return the inflated layout
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