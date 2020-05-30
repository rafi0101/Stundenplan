package com.ebner.stundenplan.fragments.main

import android.os.Bundle
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

/**
 * A simple [Fragment] subclass.
 */
class FragmentExam : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam, container, false)

        activity?.title = getString(R.string.fragment_exams)

        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)

        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(6, 0, 6, 12)
        fragmentmain.layoutParams = params


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


}