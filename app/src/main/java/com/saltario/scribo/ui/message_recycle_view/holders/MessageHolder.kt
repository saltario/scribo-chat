package com.saltario.scribo.ui.message_recycle_view.holders

import com.saltario.scribo.ui.message_recycle_view.views.MessageView

interface MessageHolder {

    fun drawMessage(view: MessageView)

    fun onAttach(view: MessageView)

    fun onDetach()
}