package com.saltario.scribo.ui.message_recycle_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.database.CURRENT_UID
import com.saltario.scribo.ui.message_recycle_view.views.MessageView
import com.saltario.scribo.ui.objects.AppVoicePlayer
import com.saltario.scribo.utilits.asTime
import kotlinx.android.synthetic.main.message_voice_item.view.*

class HolderVoiceMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserVoiceMessage: ConstraintLayout = view.block_user_voice
    private val chatUserVoiceMessageTime: TextView = view.chat_user_voice_time

    private val blockOtherUserVoiceMessage: ConstraintLayout = view.block_other_voice
    private val chatOtherUserVoiceMessageTime: TextView = view.chat_other_voice_time

    private val chatUserPlayVoiceMessage: ImageView = view.chat_btn_user_play_voice
    private val chatOtherUserPlayVoiceMessage: ImageView = view.chat_btn_other_play_voice

    private val chatUserStopVoiceMessage: ImageView = view.chat_btn_user_stop_voice
    private val chatOtherUserStopVoiceMessage: ImageView = view.chat_btn_other_stop_voice

    private var mAppVoicePlayer = AppVoicePlayer()

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

    // Когда голосовое сообщение в поле видимости, тогда подключаем слушателя, чтобы утечки памяти не было
    override fun onAttach(view: MessageView) {

        mAppVoicePlayer.init()

        if (view.from == CURRENT_UID) {
            // Слушатель кнопки старта записи
            chatUserPlayVoiceMessage.setOnClickListener {

                chatUserPlayVoiceMessage.visibility = View.GONE
                chatUserStopVoiceMessage.visibility = View.VISIBLE

                // Подключаем только тогда, когда кнопка видна, т.е. после старта прослушки
                // Слушатель кнопки остановки записи
                chatUserStopVoiceMessage.setOnClickListener {
                    stop {
                        chatUserStopVoiceMessage.setOnClickListener(null)
                        chatUserPlayVoiceMessage.visibility = View.VISIBLE
                        chatUserStopVoiceMessage.visibility = View.GONE
                    }
                }

                // Играем запись, в колбэке управляем видимостью кнопок
                play(view) {
                    chatUserPlayVoiceMessage.visibility = View.VISIBLE
                    chatUserStopVoiceMessage.visibility = View.GONE
                }
            }

        } else {
            // Слушатель кнопки старта записи
            chatOtherUserPlayVoiceMessage.setOnClickListener {

                chatOtherUserPlayVoiceMessage.visibility = View.GONE
                chatOtherUserStopVoiceMessage.visibility = View.VISIBLE

                // Подключаем только тогда, когда кнопка видна, т.е. после старта прослушки
                // Слушатель кнопки остановки записи
                chatOtherUserStopVoiceMessage.setOnClickListener {
                    stop {
                        chatOtherUserStopVoiceMessage.setOnClickListener(null)
                        chatOtherUserPlayVoiceMessage.visibility = View.VISIBLE
                        chatOtherUserStopVoiceMessage.visibility = View.GONE
                    }
                }

                // Играем запись, в колбэке управляем видимостью кнопок
                play(view) {
                    chatOtherUserPlayVoiceMessage.visibility = View.VISIBLE
                    chatOtherUserStopVoiceMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl){
            function()
        }
    }

    private fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop {
            function()
        }
    }

    override fun onDetach() {
        chatUserPlayVoiceMessage.setOnClickListener(null)
        chatOtherUserPlayVoiceMessage.setOnClickListener(null)
        mAppVoicePlayer.release()
    }

}