package com.ebner.stundenplan.database.table.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.teacher
 */
class TeacherListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<Teacher, TeacherListAdapter.TeacherViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        return TeacherViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_teacher_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Teacher infos to position number--------------------------*/
    fun getTeacherAt(position: Int): Teacher? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(teacher: Teacher)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Teacher, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val tvTeacherName: TextView = itemView.findViewById(R.id.tv_teacher_name)
            val tvTeacherGender: TextView = itemView.findViewById(R.id.tv_teacher_gender)

            tvTeacherName.text = item.tname

            tvTeacherGender.text = if (item.tgender == 0) "Herr" else "Frau"

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<Teacher>() {
    override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        return oldItem.tid == newItem.tid
    }

    override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.tname == newItem.tname
    }



}