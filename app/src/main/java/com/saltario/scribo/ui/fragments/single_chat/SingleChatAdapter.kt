package com.saltario.scribo.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var mListMessagesCache = mutableListOf<Common>()
    private lateinit var mDiffResult: DiffUtil.DiffResult

    class SingleChatHolder(view: View): RecyclerView.ViewHolder(view){

        val blockUserMessage: ConstraintLayout = view.block_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_time

        val blockOtherUserMessage: ConstraintLayout = view.block_other_message
        val chatOtherUserMessage: TextView = view.chat_other_message
        val chatOtherUserMessageTime: TextView = view.chat_other_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        if (mListMessagesCache[position].from == CURRENT_UID){
            // Сообщение текущего пользователя
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockOtherUserMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCache[position].text
            holder.chatUserMessageTime.text = mListMessagesCache[position].time.toString().asTime()
        } else {
            // Сообщение собеседника
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
