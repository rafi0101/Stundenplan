package com.ebner.stundenplan.customAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.SubjectExamsActivity
import com.ebner.stundenplan.database.table.task.Task

/**
 * Created by raphael on 17.09.2020.
 * Stundenplan Created in com.ebner.stundenplan.customAdapter
 */


/**
 * ---------------------This is the adapter class to show each item in Tasks in [SubjectExamsActivity] --------------------------
 */
class SubjectExamsTasksListAdapter : ListAdapter<Task, SubjectExamsTasksListAdapter.TaskViewHolder>(SubjectExamsTasksDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_subjectexams_tasks, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Task) = with(itemView) {
            //Bind the data with View
            val tvSubjecttasksName: TextView = itemView.findViewById(R.id.tv_subjecttasks_name)
            val tvSubjecttasksDate: TextView = itemView.findViewById(R.id.tv_subjecttasks_date)
            val tvSubjecttasksDone: TextView = itemView.findViewById(R.id.tv_subjecttasks_done)

            tvSubjecttasksName.text = item.tkname
            tvSubjecttasksDate.text = "${item.tkdateday}.${item.tkdatemonth + 1}.${item.tkdateyear}"

            val SubjectExamsActivity: SubjectExamsActivity

        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class SubjectExamsTasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.tkid == newItem.tkid
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return false
    }


}