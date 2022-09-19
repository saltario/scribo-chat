package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_image_item.view.*

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view) {

    val blockUserImageMessage: ConstraintLayout = view.block_user_image
    val chatUserImageMessage: ImageView = view.chat_user_image
    val chatUserImageMessageTime: TextView = view.chat_user_image_time

    val blockOtherUserImageMessage: ConstraintLayout = view.block_other_image
    val chatOtherUserImageMessage: ImageView = view.chat_other_image
    val chatOtherUserImageMessageTime: TextView = view.chat_other_image_time

}