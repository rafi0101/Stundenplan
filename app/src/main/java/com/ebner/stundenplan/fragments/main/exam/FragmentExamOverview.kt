package com.ebner.stundenplan.fragments.main.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ebner.stundenplan.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentExamOverview : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam_overview, container, false)

        return root
    }
}