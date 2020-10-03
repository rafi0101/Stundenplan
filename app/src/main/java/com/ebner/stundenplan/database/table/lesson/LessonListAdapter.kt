package com.ebner.stundenplan.database.table.lesson

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear

/**
 * Created by raphael on 10.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.lesson
 */
class LessonListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<LessonSubjectSchoollessonYear, LessonListAdapter.LessonViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        return LessonViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_lesson_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Lesson infos to position number--------------------------*/
    fun getLessonAt(position: Int): LessonSubjectSchoollessonYear? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(lessonSubjectSchoollessonYear: LessonSubjectSchoollessonYear)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: LessonSubjectSchoollessonYear, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val tvLessonSubject: TextView = itemView.findViewById(R.id.tv_lesson_subject)
            val tvLessonNr: TextView = itemView.findViewById(R.id.tv_lesson_nr)
            val tvLessonTime: TextView = itemView.findViewById(R.id.tv_lesson_time)
            val tvLessonDay: TextView = itemView.findViewById(R.id.tv_lesson_day)

            tvLessonSubject.text = item.subject.sname
            tvLessonSubject.setTextColor(item.subject.scolor)
            tvLessonNr.text = "${item.schoolLesson.slnumber}:"

            //IF minute is less then 10, add a 0 in front of it (just for the view)
            val returnStartMinute = if (item.schoolLesson.slstartminute < 10) "0${item.schoolLesson.slstartminute}" else "${item.schoolLesson.slstartminute}"
            val returnEndMinute = if (item.schoolLesson.slendminute < 10) "0${item.schoolLesson.slendminute}" else "${item.schoolLesson.slendminute}"

            tvLessonTime.text = "${item.schoolLesson.slstarthour}:$returnStartMinute - ${item.schoolLesson.slendhour}:$returnEndMinute"

            val day = when (item.lesson.lday) {
                1 -> "Montag"
                2 -> "Dienstag"
                3 -> "Mittwoch"
                4 -> "Donnerstag"
                5 -> "Freitag"
                6 -> "Samstag"
                7 -> "Sonntag"
                else -> ""
            }

            val cycle = when (item.lesson.lcycle) {
                1 -> "(A)"
                2 -> "(B)"
                else -> ""
            }

            tvLessonDay.text = "$day $cycle"

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<LessonSubjectSchoollessonYear>() {
    override fun areItemsTheSame(oldItem: LessonSubjectSchoollessonYear, newItem: LessonSubjectSchoollessonYear): Boolean {
        return oldItem.lesson.lid == newItem.lesson.lid
    }

    override fun areContentsTheSame(oldItem: LessonSubjectSchoollessonYear, newItem: LessonSubjectSchoollessonYear): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.lesson.lday == newItem.lesson.lday &&
                oldItem.lesson.lcycle == newItem.lesson.lcycle &&
                oldItem.lesson.lsid == newItem.lesson.lsid &&
                oldItem.lesson.lslid == newItem.lesson.lslid &&
                oldItem.lesson.lyid == newItem.lesson.lyid
    }


}