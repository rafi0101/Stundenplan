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
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype

/**
 * Created by raphael on 31.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.customAdapter
 */
class SubjectExamsExamsListAdapter : ListAdapter<ExamSubjectYearExamtype, SubjectExamsExamsListAdapter.ExamViewHolder>(ExamsTaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        return ExamViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_subjectexams_exams, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ExamSubjectYearExamtype) = with(itemView) {
            //Bind the data with View
            val tv_subjectexams_examtype: TextView = itemView.findViewById(R.id.tv_subjectexams_examtype)
            val tv_subjectexams_grade: TextView = itemView.findViewById(R.id.tv_subjectexams_grade)
            val tv_subjectexams_weight: TextView = itemView.findViewById(R.id.tv_subjectexams_weight)
            val tv_subjectexams_date: TextView = itemView.findViewById(R.id.tv_subjectexams_date)

            tv_subjectexams_examtype.text = item.examtype.etname
            if (item.exam.egrade == -1) tv_subjectexams_grade.text = "-" else tv_subjectexams_grade.text = item.exam.egrade.toString()
            tv_subjectexams_weight.text = "x${item.examtype.etweight}"
            tv_subjectexams_date.text = "${item.exam.edateday}.${item.exam.edatemonth}.${item.exam.edateyear}"


        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class ExamsTaskDiffCallback : DiffUtil.ItemCallback<ExamSubjectYearExamtype>() {
    override fun areItemsTheSame(oldItem: ExamSubjectYearExamtype, newItem: ExamSubjectYearExamtype): Boolean {
        return oldItem.exam.eid == newItem.exam.eid
    }

    override fun areContentsTheSame(oldItem: ExamSubjectYearExamtype, newItem: ExamSubjectYearExamtype): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return false
    }


}