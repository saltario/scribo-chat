package com.saltario.scribo.ui.fragments.message_recycle_view.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.TYPE_IMAGE
import com.saltario.scribo.ui.fragments.message_recycle_view.views.MessageView

class AppHolderFactory {

    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType){

                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_image_item, parent, false)
                    HolderImageMessage(view)
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