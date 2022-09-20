package com.saltario.scribo.ui.message_recycle_view.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_text_item.view.*

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserMessage: ConstraintLayout = view.block_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_time

    private val blockOtherUserMessage: ConstraintLayout = view.block_other_message
    private val chatOtherUserMessage: TextView = view.chat_other_message
    private val chatOtherUserMessageTime: TextView = view.chat_other_time

    override fun drawMessage(view: MessageView) {

        if (view.from == CURRENT_UID){

            blockUserMessage.visibility = View.VISIBLE
            blockOtherUserMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.time.asTime()
        } else {

            blockUserMessage.visibility = View.GONE
            blockOtherUserMessage.visibility = View.VISIBLE
            chatOtherUserMessage.text = view.text
            chatOtherUserMessageTime.text = view.time.asTime()
        }
    }
}