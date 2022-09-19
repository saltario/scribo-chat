package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.fragments.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_text_item.view.*

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view) {

    private val blockUserMessage: ConstraintLayout = view.block_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_time

    private val blockOtherUserMessage: ConstraintLayout = view.block_other_message
    private val chatOtherUserMessage: TextView = view.chat_other_message
    private val chatOtherUserMessageTime: TextView = view.chat_other_time

    fun drawMessageAsText(holder: HolderTextMessage, view: MessageView) {

        if (view.from == CURRENT_UID){

            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockOtherUserMessage.visibility = View.GONE
            holder.chatUserMessage.text = view.text
            holder.chatUserMessageTime.text = view.time.asTime()
        } else {

            holder.blockUserMessage.visibility = View.GONE
            holder.blockOtherUserMessage.visibility = View.VISIBLE
            holder.chatOtherUserMessage.text = view.text
            holder.chatOtherUserMessageTime.text = view.time.asTime()
        }
    }
}