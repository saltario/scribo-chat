package com.saltario.scribo.ui.screens.main_list

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.utilits.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_list_item.view.*

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private val mListItems = mutableListOf<Common>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        return MainListHolder(view)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemFullname.text = mListItems[position].fullname
        holder.itemPhoto.downloadAndSetImage(mListItems[position].photoUrl)
        holder.itemLastMessage.text = mListItems[position].lastMessage
    }

    fun updateMainListItem(item: Common) {
        mListItems.add(item)
        notifyItemInserted(mListItems.size)
    }

    override fun getItemCount(): Int = mListItems.size

    class MainListHolder(view: View): RecyclerView.ViewHolder(view){

        val itemFullname: TextView = view.main_list_fullname
        val itemPhoto: CircleImageView = view.main_list_photo
        val itemLastMessage: TextView = view.main_list_last_message
    }

}