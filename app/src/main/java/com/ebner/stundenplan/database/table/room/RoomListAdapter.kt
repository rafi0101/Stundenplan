package com.ebner.stundenplan.database.table.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.room
 */

class RoomListAdapter(val itemClickListener: onItemClickListener) : ListAdapter<Room, RoomListAdapter.RoomViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_room_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Teacher infos to position number--------------------------*/
    fun getRoomAt(position: Int): Room? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemClickListener {

        fun onItemClicked(room: Room)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Room, itemclickListener: onItemClickListener) = with(itemView) {
            //Bind the data with View
            val tv_room_number: TextView = itemView.findViewById(R.id.tv_room_number)

            tv_room_number.text = item.rname.toString()

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<Room>() {
    override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
        return oldItem.rid == newItem.rid
    }

    override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.rname == newItem.rname
    }

}
