package com.ebner.stundenplan.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ebner.stundenplan.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentSettings : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        activity?.title = getString(R.string.fragment_settings)

        return root
    }

}
