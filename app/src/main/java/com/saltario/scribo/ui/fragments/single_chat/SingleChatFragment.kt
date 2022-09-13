package com.saltario.scribo.ui.fragments.single_chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.fragments.BaseFragment
import com.saltario.scribo.ui.objects.AppChildEventListener
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*

class SingleChatFragment(private val contact: Common) : BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mOtherUser: User
    private lateinit var mToolBarInfo: View
    private lateinit var mRefOtherUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListeners: AppChildEventListener

    override fun onResume() {
        super.onResume()
        initFields()
        initButtonListeners()
        initRecycleView()
        mToolBarInfo.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
        mRefOtherUser.removeEventListener(mListenerInfoToolbar)
        hideKeyboard()
        mRefMessages.removeEventListener(mMessagesListeners)
    }

    private fun initRecycleView() {
        mRecyclerView = chat_recycle_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListeners = AppChildEventListener { snapshot ->

            mAdapter.addItem(snapshot.getCommonModel())
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)

        }
        mRefMessages.addChildEventListener(mMessagesListeners)
    }

    private fun initFields() {

        mToolBarInfo = APP_ACTIVITY.mToolBar.info_toolbar
        mListenerInfoToolbar = AppValueEventListener {
            mOtherUser = it.getUserModel()
            updateInfoToolBar()
        }
        mRefOtherUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefOtherUser.addValueEventListener(mListenerInfoToolbar)
    }

    private fun updateInfoToolBar() {

        if (mOtherUser.fullname.isEmpty()) {
            mToolBarInfo.info_toolbar_fullname.text = contact.fullname
        } else {
            mToolBarInfo.info_toolbar_fullname.text = mOtherUser.fullname
        }

        mToolBarInfo.info_toolbar_state.text = mOtherUser.state
        mToolBarInfo.info_toolbar_photo.downloadAndSetImage(mOtherUser.photoUrl)
    }

    private fun initButtonListeners() {
        chat_btn_sent_message.setOnClickListener {
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast(getString(R.string.chat_toast_message_is_empty))
            } else {
                sendMessage(message, contact.id, TYPE_TEXT){
                    chat_input_message.setText("")
                }
            }
        }
    }
}