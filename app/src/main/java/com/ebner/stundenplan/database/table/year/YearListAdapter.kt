package com.ebner.stundenplan.database.table.year

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.year
 */
class YearListAdapter(val itemClickListener: onItemClickListener) : ListAdapter<Year, YearListAdapter.YearViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        return YearViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_year_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Year infos to position number--------------------------*/
    fun getYearAt(position: Int): Year? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemClickListener {

        fun onItemClicked(year: Year)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class YearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Year, itemclickListener: onItemClickListener) = with(itemView) {
            //Bind the data with View
            val tv_year_name: TextView = itemView.findViewById(R.id.tv_year_name)

            tv_year_name.text = item.yname

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<Year>() {
    override fun areItemsTheSame(oldItem: Year, newItem: Year): Boolean {
        return oldItem.yid == newItem.yid
    }

    override fun areContentsTheSame(oldItem: Year, newItem: Year): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.yname == newItem.yname
    }


}