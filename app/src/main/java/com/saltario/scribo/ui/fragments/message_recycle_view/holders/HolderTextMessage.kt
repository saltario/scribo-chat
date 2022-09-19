package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_text_item.view.*

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view) {

    val blockUserMessage: ConstraintLayout = view.block_user_message
    val chatUserMessage: TextView = view.chat_user_message
    val chatUserMessageTime: TextView = view.chat_user_time

    val blockOtherUserMessage: ConstraintLayout = view.block_other_message
    val chatOtherUserMessage: TextView = view.chat_other_message
    val chatOtherUserMessageTime: TextView = view.chat_other_time
}