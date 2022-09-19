package com.saltario.scribo.ui.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.AppHolderFactory
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.HolderImageMessage
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.HolderTextMessage
import com.saltario.scribo.ui.fragments.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import com.saltario.scribo.utilits.downloadAndSetImage

class SingleChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder){
            is HolderTextMessage -> drawMessageAsText(holder, position)
            is HolderImageMessage -> drawMessageAsImage(holder, position)
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    private fun drawMessageAsImage(holder: HolderImageMessage, position: Int) {

        if (mListMessagesCache[position].from == CURRENT_UID){

            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.blockOtherUserImageMessage.visibility = View.GONE
            holder.chatUserImageMessage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text = mListMessagesCache[position].time.asTime()

        } else {

            holder.blockUserImageMessage.visibility = View.GONE
            holder.blockOtherUserImageMessage.visibility = View.VISIBLE
            holder.chatOtherUserImageMessage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatOtherUserImageMessageTime.text = mListMessagesCache[position].time.asTime()
        }
    }

    private fun drawMessageAsText(holder: HolderTextMessage, position: Int) {

        if (mListMessagesCache[position].from == CURRENT_UID){

            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockOtherUserMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCache[position].text
            holder.chatUserMessageTime.text = mListMessagesCache[position].time.asTime()
        } else {

            holder.blockUserMessage.visibility = View.GONE
            holder.blockOtherUserMessage.visibility = View.VISIBLE
            holder.chatOtherUserMessage.text = mListMessagesCache[position].text
            holder.chatOtherUserMessageTime.text = mListMessagesCache[position].time.asTime()
        }
    }

    override fun getItemCount(): Int {
        return mListMessagesCache.size
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            // Обновить последний элемент списка
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.time.toString() }
            // Обновить первый элемент списка
            notifyItemInserted(0)
        }
        onSuccess()
    }

}
