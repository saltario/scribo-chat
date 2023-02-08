package com.saltario.scribo.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.utilits.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_add_contacts.view.*

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private val mListItems = mutableListOf<Common>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_contacts, parent, false)
        val holder = AddContactsHolder(view)

        holder.itemView.setOnClickListener {
            if (mListItems[holder.absoluteAdapterPosition].check) {

                holder.itemCheck.isChecked = false
                mListItems[holder.absoluteAdapterPosition].check = false
                AddContactsFragment.mListContacts.remove(mListItems[holder.absoluteAdapterPosition])

            } else {
                holder.itemCheck.isChecked = true
                mListItems[holder.absoluteAdapterPosition].check = true
                AddContactsFragment.mListContacts.add(mListItems[holder.absoluteAdapterPosition])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemFullname.text = mListItems[position].fullname
        holder.itemPhoto.downloadAndSetImage(mListItems[position].photoUrl)
    }

    fun updateListItem(item: Common) {
        mListItems.add(item)
        notifyItemInserted(mListItems.size)
    }

    override fun getItemCount(): Int = mListItems.size

    class AddContactsHolder(view: View): RecyclerView.ViewHolder(view){
        val itemFullname: TextView = view.add_contacts_fullname
        val itemPhoto: CircleImageView = view.add_contacts_photo
        val itemCheck: CheckBox = view.add_contacts_checkbox
    }
}