package com.saltario.scribo.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.ui.message_recycle_view.holders.*
import com.saltario.scribo.ui.message_recycle_view.views.MessageView

class SingleChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MessageHolder).drawMessage(mListMessagesCache[position])
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
