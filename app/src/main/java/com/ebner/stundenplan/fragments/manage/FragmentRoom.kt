package com.ebner.stundenplan.fragments.manage

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.room.Room
import com.ebner.stundenplan.database.table.room.RoomListAdapter
import com.ebner.stundenplan.database.table.room.RoomViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class FragmentRoom : Fragment(), RoomListAdapter.OnItemClickListener {

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var clRoom: CoordinatorLayout


    companion object {
        private const val ADD_ROOM_REQUEST = 1
        private const val EDIT_ROOM_REQUEST = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_room, container, false)

        activity?.title = getString(R.string.fragment_rooms)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params


        /*---------------------Link items to Layout--------------------------*/
        clRoom = root.findViewById(R.id.cl_room)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_room)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_room_addRoom)


        val adapter = RoomListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        //Automatic update the recyclerlayout
        roomViewModel.allRoom.observe(viewLifecycleOwner, Observer { rooms ->
            rooms.let { adapter.submitList(it) }

        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditRoom::class.java)
            startActivityForResult(intent, ADD_ROOM_REQUEST)
        }

        /*---------------------Swiping on a row--------------------------*/
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            /*---------------------do action on swipe--------------------------*/
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Item in recyclerview
                val position = viewHolder.adapterPosition
                //Item from database (roomItem?.rid gives the id)
                val roomItem = adapter.getRoomAt(position)

                /*---------------------Confirm Delete Dialog--------------------------*/
                MaterialAlertDialogBuilder(context)
                        .setTitle("Achtung")
                        .setMessage("Es wird der Raum ${roomItem?.rname} und alle zugehörigen Fächer, Prüfungen und Aufgaben gelöscht.\nDas Wiederherstellen ist nicht mehr möglich!")
                        .setPositiveButton("Löschen") { _, _ ->
                            roomItem?.let { roomViewModel.delete(it) }
                            // showing snack bar with Undo option
                            val snackbar = Snackbar
                                    .make(clRoom, "Raum ${roomItem?.rname} erfolgreich gelöscht!", 8000) //ms --> 8sec
                            snackbar.show()
                        }
                        .setNegativeButton("Abbrechen") { _, _ ->
                            adapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener {
                            adapter.notifyItemChanged(position)
                        }
                        .show()
            }

            /*---------------------ADD trash bin icon to background--------------------------*/
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_sweep) }

                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX > 0) { // Swiping to the right
                    val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
                    val iconRight = itemView.left + iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                } else if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                icon.draw(c)
            }
        }).attachToRecyclerView(recyclerView)

        //Return the inflated layout
        return root
    }

    /*---------------------when returning from |ActivityAddEditRoom| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*---------------------If the Request was successful--------------------------*/
        if (resultCode == RESULT_OK) {
            val rname = data!!.getStringExtra(ActivityAddEditRoom.EXTRA_RNAME)
            val room = Room(rname)

            /*---------------------If the Request was a ADD room request--------------------------*/
            if (requestCode == ADD_ROOM_REQUEST && resultCode == RESULT_OK) {

                roomViewModel.insert(room)

                /*---------------------If the Request was a EDIT room request--------------------------*/
            } else if (requestCode == EDIT_ROOM_REQUEST && resultCode == RESULT_OK) {
                val id = data.getIntExtra(ActivityAddEditRoom.EXTRA_RID, -1)

                if (id == -1) {
                    val snackbar = Snackbar
                            .make(clRoom, "Failed to update Room!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                room.rid = id
                roomViewModel.update(room)

            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(room: Room) {
        val intent = Intent(context, ActivityAddEditRoom::class.java)
        intent.putExtra(ActivityAddEditRoom.EXTRA_RID, room.rid)
        intent.putExtra(ActivityAddEditRoom.EXTRA_RNAME, room.rname)
        startActivityForResult(intent, EDIT_ROOM_REQUEST)

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

