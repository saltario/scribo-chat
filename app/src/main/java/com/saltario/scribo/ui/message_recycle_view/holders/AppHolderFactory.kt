package com.saltario.scribo.ui.message_recycle_view.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.ui.message_recycle_view.views.MessageView

class AppHolderFactory {

    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType){

                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_image_item, parent, false)
                    HolderImageMessage(view)
                }

                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_voice_item, parent, false)
                    HolderVoiceMessage(view)
                }

                MessageView.MESSAGE_FILE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_file_item, parent, false)
                    HolderFileMessage(view)
                }

                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_text_item, parent, false)
                    HolderTextMessage(view)
                }

            }
        }
    }
}