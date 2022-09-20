package com.saltario.scribo.ui.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_voice_item.view.*

class HolderVoiceMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserVoiceMessage: ConstraintLayout = view.block_user_voice
    private val chatUserVoiceMessageTime: TextView = view.chat_user_voice_time

    private  val blockOtherUserVoiceMessage: ConstraintLayout = view.block_other_voice
    private val chatOtherUserVoiceMessageTime: TextView = view.chat_other_voice_time

    val chatUserPlayVoiceMessage: ImageView = view.chat_btn_user_play_voice
    val chatOtherUserPlayVoiceMessage: ImageView = view.chat_btn_other_play_voice

    val chatUserStopVoiceMessage: ImageView = view.chat_btn_user_stop_voice
    val chatOtherUserStopVoiceMessage: ImageView = view.chat_btn_other_stop_voice

    override fun drawMessage(view: MessageView) {

        if (view.from == CURRENT_UID){

            blockUserVoiceMessage.visibility = View.VISIBLE
            blockOtherUserVoiceMessage.visibility = View.GONE
            chatUserVoiceMessageTime.text = view.time.asTime()

        } else {

            blockUserVoiceMessage.visibility = View.GONE
            blockOtherUserVoiceMessage.visibility = View.VISIBLE
            chatOtherUserVoiceMessageTime.text = view.time.asTime()
        }
    }

}