package com.ebner.stundenplan.fragments.main

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.ebner.stundenplan.R
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentTask : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_task, container, false)

        activity?.title = getString(R.string.fragment_tasks)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


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
        return dp * (context.getResources().getDisplayMetrics().densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}