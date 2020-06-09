package com.ebner.stundenplan.database.table.examtype

import android.annotation.SuppressLint
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
 * Stundenplan Created in com.ebner.stundenplan.database.table.examtype
 */
class ExamtypeListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<Examtype, ExamtypeListAdapter.ExamtypeViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamtypeViewHolder {
        return ExamtypeViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_examtype_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: ExamtypeViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Examtype infos to position number--------------------------*/
    fun getExamtypeAt(position: Int): Examtype? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(examtype: Examtype)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class ExamtypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Examtype, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val tvExamtypeName: TextView = itemView.findViewById(R.id.tv_examtype_name)
            val tvExamtypeWeight: TextView = itemView.findViewById(R.id.tv_examtype_weight)

            tvExamtypeName.text = item.etname
            tvExamtypeWeight.text = "x " + item.etweight.toString()

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<Examtype>() {
    override fun areItemsTheSame(oldItem: Examtype, newItem: Examtype): Boolean {
        return oldItem.etid == newItem.etid
    }

    override fun areContentsTheSame(oldItem: Examtype, newItem: Examtype): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.etname == newItem.etname &&
                oldItem.etweight == newItem.etweight
    }


}