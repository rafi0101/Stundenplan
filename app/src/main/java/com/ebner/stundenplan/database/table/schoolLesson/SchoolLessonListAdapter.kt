package com.ebner.stundenplan.database.table.schoolLesson

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
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.schoolLesson
 */
class SchoolLessonListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<SchoolLesson, SchoolLessonListAdapter.SchoolLessonViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolLessonViewHolder {
        return SchoolLessonViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_schoollesson_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: SchoolLessonViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform SchoolLesson infos to position number--------------------------*/
    fun getSchoolLessonAt(position: Int): SchoolLesson? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(schoolLesson: SchoolLesson)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class SchoolLessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(item: SchoolLesson, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val tvSchoollessonNumber: TextView = itemView.findViewById(R.id.tv_schoollesson_number)
            val tvSchoollessonTimeStart: TextView = itemView.findViewById(R.id.tv_schoollesson_time_start)
            val tvSchoollessonTimeEnd: TextView = itemView.findViewById(R.id.tv_schoollesson_time_end)

            tvSchoollessonNumber.text = item.slnumber.toString()

            //IF minute is less then 10, add a 0 in front of it (just for the view)
            val returnStartMinute = if (item.slstartminute < 10) "0${item.slstartminute}" else "${item.slstartminute}"
            val returnEndMinute = if (item.slendminute < 10) "0${item.slendminute}" else "${item.slendminute}"

            tvSchoollessonTimeStart.text = "${item.slstarthour}:$returnStartMinute"
            tvSchoollessonTimeEnd.text = "${item.slendhour}:$returnEndMinute"


            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }

}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class TaskDiffCallback : DiffUtil.ItemCallback<SchoolLesson>() {
    override fun areItemsTheSame(oldItem: SchoolLesson, newItem: SchoolLesson): Boolean {
        return oldItem.slid == newItem.slid
    }

    override fun areContentsTheSame(oldItem: SchoolLesson, newItem: SchoolLesson): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.slnumber == newItem.slnumber &&
                oldItem.slstarthour == newItem.slstarthour &&
                oldItem.slstartminute == newItem.slstartminute &&
                oldItem.slendhour == newItem.slendhour &&
                oldItem.slendminute == newItem.slendminute
    }


}