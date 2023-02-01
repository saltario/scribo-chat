package com.saltario.scribo.ui.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.asTime
import com.saltario.scribo.utilits.downloadAndSetImage
import kotlinx.android.synthetic.main.message_image_item.view.*

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserImageMessage: LinearLayout = view.block_user_image
    private val chatUserImageMessage: ImageView = view.chat_user_image
    private val chatUserImageMessageTime: TextView = view.chat_user_image_time

    private val blockOtherUserImageMessage: LinearLayout = view.block_other_image
    private val chatOtherUserImageMessage: ImageView = view.chat_other_image
    private val chatOtherUserImageMessageTime: TextView = view.chat_other_image_time

    override fun drawMessage(view: MessageView) {

        if (view.from == CURRENT_UID){

            blockUserImageMessage.visibility = View.VISIBLE
            blockOtherUserImageMessage.visibility = View.GONE
            chatUserImageMessage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view.time.asTime()

        } else {

            blockUserImageMessage.visibility = View.GONE
            blockOtherUserImageMessage.visibility = View.VISIBLE
            chatOtherUserImageMessage.downloadAndSetImage(view.fileUrl)
            chatOtherUserImageMessageTime.text = view.time.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }

}