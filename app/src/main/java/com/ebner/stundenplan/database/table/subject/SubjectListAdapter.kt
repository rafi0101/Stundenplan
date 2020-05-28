package com.ebner.stundenplan.database.table.subject

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.subject
 */
class SubjectListAdapter(val itemClickListener: onItemClickListener) : ListAdapter<SubjectTeacherRoom, SubjectListAdapter.SubjectViewHolder>(TaskDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.content_subject_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform Subject infos to position number--------------------------*/
    fun getSubjectAt(position: Int): SubjectTeacherRoom? {
        return getItem(position)
    }


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface onItemClickListener {

        fun onItemClicked(subjectTeacherRoom: SubjectTeacherRoom)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SubjectTeacherRoom, itemclickListener: onItemClickListener) = with(itemView) {
            //Bind the data with View
            val tv_subject_name: TextView = itemView.findViewById(R.id.tv_subject_name)
            val tv_subject_note: TextView = itemView.findViewById(R.id.tv_subject_note)
            val tv_subject_teacher: TextView = itemView.findViewById(R.id.tv_subject_teacher)
            val tv_subject_room: TextView = itemView.findViewById(R.id.tv_subject_room)

            tv_subject_name.text = item.subject.sname
            tv_subject_note.text = item.subject.snote
            tv_subject_room.text = "Raum: " + item.room.rname
            tv_subject_teacher.text = "Lehrer: " + item.teacher.tname

            if (TextUtils.isEmpty(item.subject.snote) || TextUtils.getTrimmedLength(item.subject.snote) == 0) {
                tv_subject_note.setVisibility(View.GONE)
            }
            if (item.subject.sinactive) {
                tv_subject_name.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption)
                tv_subject_teacher.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption)
                tv_subject_room.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption)
                tv_subject_note.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption)

            }

            tv_subject_name.setTextColor(item.subject.scolor)

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
        return oldItem.subject.sname == newItem.subject.sname &&
                oldItem.subject.snameshort == newItem.subject.snameshort &&
                oldItem.subject.scolor == newItem.subject.scolor &&
                oldItem.subject.snote == newItem.subject.snote &&
                oldItem.subject.sinactive == newItem.subject.sinactive &&
                oldItem.subject.stid == newItem.subject.stid &&
                oldItem.subject.srid == newItem.subject.srid &&
                oldItem.room == newItem.room &&
                oldItem.teacher == newItem.teacher

    }


}