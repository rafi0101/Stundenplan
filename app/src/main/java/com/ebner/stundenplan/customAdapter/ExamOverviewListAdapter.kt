package com.ebner.stundenplan.customAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom

/**
 * Created by raphael on 31.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.customAdapter
 */
class ExamOverviewListAdapter(val itemClickListener: onItemClickListener) : ListAdapter<SubjectTeacherRoom, ExamOverviewListAdapter.ExamViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        return ExamViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_exam_overview, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemClickListener {

        fun onItemClicked(subjectTeacherRoom: SubjectTeacherRoom)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SubjectTeacherRoom, itemclickListener: onItemClickListener) = with(itemView) {
            //Bind the data with View
            val tv_exam_overview_subject: TextView = itemView.findViewById(R.id.tv_exam_overview_subject)
            val tv_exam_overview_grade: TextView = itemView.findViewById(R.id.tv_exam_overview_grade)
            val cl_exam_overview: ConstraintLayout = itemView.findViewById(R.id.cl_exam_overview)


            tv_exam_overview_subject.text = item.subject.sname
            tv_exam_overview_grade.text = "WIP"
            cl_exam_overview.setBackgroundColor(item.subject.scolor)


            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<SubjectTeacherRoom>() {
    override fun areItemsTheSame(oldItem: SubjectTeacherRoom, newItem: SubjectTeacherRoom): Boolean {
        return oldItem.subject.sid == newItem.subject.sid
    }

    override fun areContentsTheSame(oldItem: SubjectTeacherRoom, newItem: SubjectTeacherRoom): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return false
    }


}