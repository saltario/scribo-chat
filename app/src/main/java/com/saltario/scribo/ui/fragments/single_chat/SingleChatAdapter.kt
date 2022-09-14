package com.saltario.scribo.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.database.TYPE_IMAGE
import com.saltario.scribo.database.TYPE_TEXT
import com.saltario.scribo.utilits.asTime
import com.saltario.scribo.utilits.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var mListMessagesCache = mutableListOf<Common>()

    class SingleChatHolder(view: View): RecyclerView.ViewHolder(view){

        // Text
        val blockUserMessage: ConstraintLayout = view.block_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_time

        val blockOtherUserMessage: ConstraintLayout = view.block_other_message
        val chatOtherUserMessage: TextView = view.chat_other_message
        val chatOtherUserMessageTime: TextView = view.chat_other_time

        // Image
        val blockUserImageMessage: ConstraintLayout = view.block_user_image
        val chatUserImageMessage: ImageView = view.chat_user_image
        val chatUserImageMessageTime: TextView = view.chat_user_image_time

        val blockOtherUserImageMessage: ConstraintLayout = view.block_other_image
        val chatOtherUserImageMessage: ImageView = view.chat_other_image
        val chatOtherUserImageMessageTime: TextView = view.chat_other_image_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {

        when (mListMessagesCache[position].type){
            TYPE_TEXT -> drawMessageAsText(holder, position)
            TYPE_IMAGE -> drawMessageAsImage(holder, position)
        }
    }

    private fun drawMessageAsImage(holder: SingleChatHolder, position: Int) {

        holder.blockUserMessage.visibility = View.GONE
        holder.blockOtherUserMessage.visibility = View.GONE

        if (mListMessagesCache[position].from == CURRENT_UID){

            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.blockOtherUserImageMessage.visibility = View.GONE
            holder.chatUserImageMessage.downloadAndSetImage(mListMessagesCache[position].imageUrl)
            holder.chatUserImageMessageTime.text = mListMessagesCache[position].time.toString().asTime()

        } else {

            holder.blockUserImageMessage.visibility = View.GONE
            holder.blockOtherUserImageMessage.visibility = View.VISIBLE
            holder.chatOtherUserImageMessage.downloadAndSetImage(mListMessagesCache[position].imageUrl)
            holder.chatOtherUserImageMessageTime.text = mListMessagesCache[position].time.toString().asTime()
        }
    }

    private fun drawMessageAsText(holder: SingleChatHolder, position: Int) {

        holder.blockUserImageMessage.visibility = View.GONE
        holder.blockOtherUserImageMessage.visibility = View.GONE

        if (mListMessagesCache[position].from == CURRENT_UID){

            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockOtherUserMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCache[position].text
            holder.chatUserMessageTime.text = mListMessagesCache[position].time.toString().asTime()
        } else {

            holder.blockUserMessage.visibility = View.GONE
            holder.blockOtherUserMessage.visibility = View.VISIBLE
            holder.chatOtherUserMessage.text = mListMessagesCache[position].text
            holder.chatOtherUserMessageTime.text = mListMessagesCache[position].time.toString().asTime()
        }
    }

    override fun getItemCount(): Int {
        return mListMessagesCache.size
    }

    fun addItemToBottom(item: Common, onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            // Обновить последний элемент списка
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: Common, onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.time.toString() }
            // Обновить первый элемент списка
            notifyItemInserted(0)
        }
        onSuccess()
    }

}
