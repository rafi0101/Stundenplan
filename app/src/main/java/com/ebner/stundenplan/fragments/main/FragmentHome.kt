package com.ebner.stundenplan.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ebner.stundenplan.BuildConfig
import com.ebner.stundenplan.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentHome : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val tv_version_number = root.findViewById<TextView>(R.id.tv_version_number)
        tv_version_number.text = versionName
        return root
    }
}