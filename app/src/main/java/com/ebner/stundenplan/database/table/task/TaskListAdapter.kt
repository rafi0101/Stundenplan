package com.ebner.stundenplan.database.table.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.TaskLesson

/**
 * Created by raphael on 02.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
class TaskListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<TaskLesson, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_task_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Task infos to position number--------------------------*/
    fun getTaskAt(position: Int): TaskLesson? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(taskLesson: TaskLesson)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TaskLesson, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val tvTaskName: TextView = itemView.findViewById(R.id.tv_task_name)
            val tvTaskSubject: TextView = itemView.findViewById(R.id.tv_task_subject)
            val tvTaskTeacher: TextView = itemView.findViewById(R.id.tv_task_teacher)
            val tvTaskNote: TextView = itemView.findViewById(R.id.tv_task_note)

            tvTaskName.text = item.task.tkname
            tvTaskName.setTextColor(item.lessonSubjectSchoollessonYear.subject.scolor)
            tvTaskSubject.text = item.lessonSubjectSchoollessonYear.subject.sname
            tvTaskTeacher.text = item.lessonSubjectSchoollessonYear.teacher.tname
            tvTaskNote.text = item.task.tknote

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<TaskLesson>() {
    override fun areItemsTheSame(oldItem: TaskLesson, newItem: TaskLesson): Boolean {
        return oldItem.task.tkid == newItem.task.tkid
    }

    override fun areContentsTheSame(oldItem: TaskLesson, newItem: TaskLesson): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.task.tkname == newItem.task.tkname &&
                oldItem.task.tknote == newItem.task.tknote
    }


}