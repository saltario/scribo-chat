package com.saltario.scribo.ui.screens.groups

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.screens.single_chat.SingleChatFragment
import com.saltario.scribo.utilits.downloadAndSetImage
import com.saltario.scribo.utilits.replaceFragment
import com.saltario.scribo.utilits.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts_item.view.*
import kotlinx.android.synthetic.main.main_list_item.view.*

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private val mListItems = mutableListOf<Common>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        val holder = AddContactsHolder(view)

        holder.itemView.setOnClickListener {
            if (mListItems[holder.absoluteAdapterPosition].check) {

                holder.itemCheck.visibility = View.INVISIBLE
                mListItems[holder.absoluteAdapterPosition].check = false
                AddContactsFragment.mListContacts.remove(mListItems[holder.absoluteAdapterPosition])

            } else {
                holder.itemCheck.visibility = View.VISIBLE
                mListItems[holder.absoluteAdapterPosition].check = true
                AddContactsFragment.mListContacts.add(mListItems[holder.absoluteAdapterPosition])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemFullname.text = mListItems[position].fullname
        holder.itemPhoto.downloadAndSetImage(mListItems[position].photoUrl)
        holder.itemState.text = mListItems[position].state
    }

    fun updateMainListItem(item: Common) {
        mListItems.add(item)
        notifyItemInserted(mListItems.size)
    }

    override fun getItemCount(): Int = mListItems.size

    class AddContactsHolder(view: View): RecyclerView.ViewHolder(view){

        val itemFullname: TextView = view.add_contacts_fullname
        val itemPhoto: CircleImageView = view.add_contacts_photo
        val itemState: TextView = view.add_contacts_state
        val itemCheck: CircleImageView = view.add_contacts_item_check
    }

}