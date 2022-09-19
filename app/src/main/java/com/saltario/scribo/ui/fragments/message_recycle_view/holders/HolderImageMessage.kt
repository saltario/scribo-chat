package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.fragments.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import com.saltario.scribo.utilits.downloadAndSetImage
import kotlinx.android.synthetic.main.message_image_item.view.*

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view) {

    private val blockUserImageMessage: ConstraintLayout = view.block_user_image
    private val chatUserImageMessage: ImageView = view.chat_user_image
    private val chatUserImageMessageTime: TextView = view.chat_user_image_time

    private val blockOtherUserImageMessage: ConstraintLayout = view.block_other_image
    private val chatOtherUserImageMessage: ImageView = view.chat_other_image
    private val chatOtherUserImageMessageTime: TextView = view.chat_other_image_time

    fun drawMessageAsImage(holder: HolderImageMessage, view: MessageView) {

        if (view.from == CURRENT_UID){

            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.blockOtherUserImageMessage.visibility = View.GONE
            holder.chatUserImageMessage.downloadAndSetImage(view.fileUrl)
            holder.chatUserImageMessageTime.text = view.time.asTime()

        } else {

            holder.blockUserImageMessage.visibility = View.GONE
            holder.blockOtherUserImageMessage.visibility = View.VISIBLE
            holder.chatOtherUserImageMessage.downloadAndSetImage(view.fileUrl)
            holder.chatOtherUserImageMessageTime.text = view.time.asTime()
        }
    }

}