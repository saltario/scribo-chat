package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.fragments.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_voice_item.view.*

class HolderVoiceMessage(view: View): RecyclerView.ViewHolder(view) {

    private val blockUserVoiceMessage: ConstraintLayout = view.block_user_voice
    private val chatUserVoiceMessageTime: TextView = view.chat_user_voice_time

    private  val blockOtherUserVoiceMessage: ConstraintLayout = view.block_other_voice
    private val chatOtherUserVoiceMessageTime: TextView = view.chat_other_voice_time

    val chatUserPlayVoiceMessage: ImageView = view.chat_btn_user_play_voice
    val chatOtherUserPlayVoiceMessage: ImageView = view.chat_btn_other_play_voice

    val chatUserStopVoiceMessage: ImageView = view.chat_btn_user_stop_voice
    val chatOtherUserStopVoiceMessage: ImageView = view.chat_btn_other_stop_voice

    fun drawMessageAsVoice(holder: HolderVoiceMessage, view: MessageView) {

        if (view.from == CURRENT_UID){

            holder.blockUserVoiceMessage.visibility = View.VISIBLE
            holder.blockOtherUserVoiceMessage.visibility = View.GONE
            holder.chatUserVoiceMessageTime.text = view.time.asTime()

        } else {

            holder.blockUserVoiceMessage.visibility = View.GONE
            holder.blockOtherUserVoiceMessage.visibility = View.VISIBLE
            holder.chatOtherUserVoiceMessageTime.text = view.time.asTime()
        }
    }

}