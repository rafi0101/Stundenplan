package com.ebner.stundenplan.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ebner.stundenplan.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentGrade : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_grade, container, false)

        activity?.title = getString(R.string.fragment_grades)

        return root
    }
}