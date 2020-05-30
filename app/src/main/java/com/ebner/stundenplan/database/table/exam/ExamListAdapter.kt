package com.ebner.stundenplan.database.table.exam

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.exam
 */
class ExamListAdapter(val itemClickListener: onItemClickListener, val itemLongClickListener: onItemLongClickListener) : ListAdapter<ExamSubjectYearExamtype, ExamListAdapter.ExamViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        return ExamViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_exam_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener, itemLongClickListener)
    }

    /*---------------------Transform Exam infos to position number--------------------------*/
    fun getExamAt(position: Int): ExamSubjectYearExamtype? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemClickListener {

        fun onItemClicked(examSubjectYearExamtype: ExamSubjectYearExamtype)
    }

    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemLongClickListener {

        fun onItemLongClicked(examSubjectYearExamtype: ExamSubjectYearExamtype)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ExamSubjectYearExamtype, itemclickListener: onItemClickListener, itemLongClickListener: onItemLongClickListener) = with(itemView) {
            //Bind the data with View
            val tv_exam_subject_name: TextView = itemView.findViewById(R.id.tv_exam_subject_name)
            val tv_exam_examtype: TextView = itemView.findViewById(R.id.tv_exam_examtype)
            val tv_exam_date: TextView = itemView.findViewById(R.id.tv_exam_date)

            tv_exam_subject_name.text = item.subject.sname
            tv_exam_examtype.text = item.examtype.etname
            tv_exam_date.text = "${item.exam.edateday}.${item.exam.edatemonth}.${item.exam.edateyear}"

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }

            itemView.setOnLongClickListener {
                itemLongClickListener.onItemLongClicked(item)
                return@setOnLongClickListener true
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<ExamSubjectYearExamtype>() {
    override fun areItemsTheSame(oldItem: ExamSubjectYearExamtype, newItem: ExamSubjectYearExamtype): Boolean {
        return oldItem.exam.eid == newItem.exam.eid
    }

    override fun areContentsTheSame(oldItem: ExamSubjectYearExamtype, newItem: ExamSubjectYearExamtype): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.exam.esid == newItem.exam.esid &&
                oldItem.exam.eetid == newItem.exam.eetid &&
                oldItem.exam.eyid == newItem.exam.eyid &&
                oldItem.exam.egrade == newItem.exam.egrade &&
                oldItem.exam.edateyear == newItem.exam.edateyear &&
                oldItem.exam.edatemonth == newItem.exam.edatemonth &&
                oldItem.exam.edateday == newItem.exam.edateday &&
                oldItem.subject == newItem.subject &&
                oldItem.year == newItem.year &&
                oldItem.examtype == newItem.examtype
    }


}