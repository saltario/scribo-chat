package com.saltario.scribo.ui.fragments.message_recycle_view.views

data class ViewImageMessage(
    override val id: String,
    override val from: String,
    override val time: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView{

    override fun getTypeView(): Int {
        return MessageView.MESSAGE_IMAGE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}