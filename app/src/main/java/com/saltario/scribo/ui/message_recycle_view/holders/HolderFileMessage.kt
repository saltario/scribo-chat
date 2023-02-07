package com.saltario.scribo.ui.message_recycle_view.holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.database.getFileFromStorage
import com.saltario.scribo.ui.message_recycle_view.views.MessageView
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.message_file_item.view.*
import java.io.File

class HolderFileMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserFileMessage: ConstraintLayout = view.block_user_file
    private val chatUserFileMessageTime: TextView = view.chat_user_file_time
    private val chatUserFileMessageName: TextView = view.chat_user_file_name
    private val chatUserDownloadFileProgressBar: ProgressBar = view.chat_user_download_progress_bar

    private val blockOtherUserFileMessage: ConstraintLayout = view.block_other_file
    private val chatOtherUserFileMessageTime: TextView = view.chat_other_file_time
    private val chatOtherUserFileMessageName: TextView = view.chat_other_file_name
    private val chatOtherUserDownloadFileProgressBar: ProgressBar = view.chat_other_download_progress_bar

    private val chatUserDownloadFileMessage: ImageView = view.chat_btn_user_download_file
    private val chatOtherUserDownloadFileMessage: ImageView = view.chat_btn_other_download_file

    override fun drawMessage(view: MessageView) {

        if (view.from == CURRENT_UID){

            blockUserFileMessage.visibility = View.VISIBLE
            blockOtherUserFileMessage.visibility = View.GONE

            chatUserFileMessageTime.text = view.time.asTime()
            chatUserFileMessageName.text = view.text

        } else {

            blockUserFileMessage.visibility = View.GONE
            blockOtherUserFileMessage.visibility = View.VISIBLE

            chatOtherUserFileMessageTime.text = view.time.asTime()
            chatOtherUserFileMessageName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {

        if (view.from == CURRENT_UID) {
            chatUserDownloadFileMessage.setOnClickListener { onClickDownloadButton(view) }
        } else {
            chatOtherUserDownloadFileMessage.setOnClickListener { onClickDownloadButton(view) }
        }

    }

    private fun onClickDownloadButton(view: MessageView) {

        if (view.from == CURRENT_UID) {

            chatUserDownloadFileMessage.visibility = View.INVISIBLE
            chatUserDownloadFileProgressBar.visibility = View.VISIBLE


        } else {

            chatOtherUserDownloadFileMessage.visibility = View.INVISIBLE
            chatOtherUserDownloadFileProgressBar.visibility = View.VISIBLE

        }

        val mFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if (checkPermission(WRITE_FILES)) {
                mFile.createNewFile()
                getFileFromStorage(mFile, view.fileUrl) {

                    showToast(APP_ACTIVITY.getString(R.string.app_toast_download_success))

                    if (view.from == CURRENT_UID) {

                        chatUserDownloadFileMessage.visibility = View.VISIBLE
                        chatUserDownloadFileProgressBar.visibility = View.INVISIBLE


                    } else {

                        chatOtherUserDownloadFileMessage.visibility = View.VISIBLE
                        chatOtherUserDownloadFileProgressBar.visibility = View.INVISIBLE

                    }
                }
            }

        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserDownloadFileMessage.setOnClickListener(null)
        chatOtherUserDownloadFileMessage.setOnClickListener(null)
    }
}