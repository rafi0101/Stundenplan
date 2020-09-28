package com.ebner.stundenplan.fragments.main

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by raphael on 24.09.2020.
 * Stundenplan Created in com.ebner.stundenplan.fragments.main
 */
/**
 * Custom item decoration for a vertical [RecyclerView] with [GridLayoutManager]. Adds a
 * small amount of padding to the left of grid items, and a large amount of padding to the right.
 */
class HomeFieldItemDecoration(private val largePadding: Int, private val smallPadding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = smallPadding
        outRect.right = smallPadding
        outRect.top = largePadding
        outRect.bottom = largePadding
    }
}
