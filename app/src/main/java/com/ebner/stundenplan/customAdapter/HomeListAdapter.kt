package com.ebner.stundenplan.customAdapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.customObjects.HomeField

/**
 * Created by raphael on 25.09.2020.
 * Stundenplan Created in com.ebner.stundenplan.customAdapter
 */
class HomeListAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<HomeField, HomeListAdapter.HomeFieldViewHolder>(HomeListDiffCallback()) {


    /*---------------------creates the ViewHolder (returns the view with all items in it)--------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFieldViewHolder {
        return HomeFieldViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.content_home_list, parent, false)
        )
    }

    /*---------------------Bind the data with the View--------------------------*/
    override fun onBindViewHolder(holder: HomeFieldViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    /*---------------------Transform HomeField infos to position number--------------------------*/
/*    fun getHomeFieldAt(position: Int): HomeField? {
        return getItem(position)
    }*/


    /*---------------------Creates an onClickListener (when you press on a item, you get the ID, and can do what ever you want--------------------------*/
    interface OnItemClickListener {

        fun onItemClicked(home: HomeField)
    }

    /*---------------------get the item from the onBindViewHolder, and apply it to the current view row--------------------------*/
    inner class HomeFieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: HomeField, itemclickListener: OnItemClickListener) = with(itemView) {
            //Bind the data with View
            val homeImage: ImageView = itemView.findViewById(R.id.iv_cardview_image)
            val homeTitle: TextView = itemView.findViewById(R.id.tv_cardview_title)

            homeTitle.text = item.title
            homeImage.setImageResource(item.image)
            if (item.color != 0) {
                val color = ContextCompat.getColor(itemView.context, item.color)
                homeImage.imageTintList = ColorStateList.valueOf(color)
            }

            itemView.setOnClickListener {
                itemclickListener.onItemClicked(item)
            }
        }
    }
}

/*---------------------Makes the Animation to the recyclerview, when item is changed, added or deleted--------------------------*/
class HomeListDiffCallback : DiffUtil.ItemCallback<HomeField>() {
    override fun areItemsTheSame(oldItem: HomeField, newItem: HomeField): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HomeField, newItem: HomeField): Boolean {
        //Compare all items, so if there is a new field, add it with &&
        return oldItem.title == newItem.title &&
                oldItem.image == newItem.image &&
                oldItem.color == newItem.color
    }
}