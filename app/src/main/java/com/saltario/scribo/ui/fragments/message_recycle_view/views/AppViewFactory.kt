package com.saltario.scribo.ui.fragments.message_recycle_view.views

import com.saltario.scribo.database.TYPE_IMAGE
import com.saltario.scribo.database.TYPE_TEXT
import com.saltario.scribo.models.Common

class AppViewFactory {

     companion object {
         fun getView(message: Common): MessageView{
             return when (message.type) {

                 TYPE_IMAGE -> ViewImageMessage(
                     message.id,
                     message.from,
                     message.time.toString(),
                     message.fileUrl
                 )

                 TYPE_TEXT -> ViewTextMessage(
                     message.id,
                     message.from,
                     message.time.toString(),
                     message.fileUrl,
                     message.text
                 )

                 else -> { ViewTextMessage(
                     message.id,
                     message.from,
                     message.time.toString(),
                     message.fileUrl,
                     message.text
                 )}
             }
         }
     }
}