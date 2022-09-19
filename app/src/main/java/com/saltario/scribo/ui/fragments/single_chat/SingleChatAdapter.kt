package com.saltario.scribo.ui.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.AppHolderFactory
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.HolderImageMessage
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.HolderTextMessage
import com.saltario.scribo.ui.fragments.message_recycle_view.holders.HolderVoiceMessage
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
            is HolderTextMessage -> holder.drawMessageAsText(holder, mListMessagesCache[position])
            is HolderImageMessage -> holder.drawMessageAsImage(holder, mListMessagesCache[position])
            is HolderVoiceMessage -> holder.drawMessageAsVoice(holder, mListMessagesCache[position])
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
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
