package com.ebner.stundenplan.fragments.main.exam

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by raphael on 30.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.fragments.main.exam
 */
class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    var fragments: ArrayList<Fragment> = arrayListOf(
            FragmentExamAll(1),
            FragmentExamAll(0),
            FragmentExamOverview()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}