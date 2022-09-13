package com.saltario.scribo.ui.fragments.single_chat

import android.view.View
import android.widget.AbsListView
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

    // Тулбар
    private lateinit var mToolBarInfo: View
    // Слушает изменения в тулбаре
    private lateinit var mListenerInfoToolbar: AppValueEventListener

    // Собеседник
    private lateinit var mOtherUser: User
    // Ссылка на собеседника в БД
    private lateinit var mRefOtherUser: DatabaseReference

    // Ссылка на сообщение (на одно сообщение)
    private lateinit var mRefMessages: DatabaseReference
    // Подключает слушателя на изменение в сообщениях
    private lateinit var mMessagesListeners: AppChildEventListener
    // Список слушателей (чтобы утечки памяти не было)
    private  var mListListeners = mutableListOf<AppChildEventListener>()
    // Количество сообщений, которое будет загружаться сразу
    private var mCountMessages = 10

    // Список
    private lateinit var mRecyclerView: RecyclerView
    // Адаптер списка
    private lateinit var mAdapter: SingleChatAdapter

    // Если пользователь скролит список?
    private var mIsScrolling = false
    // Нужно ли крутить список вниз?
    private var mSmoothScrollToPosition = true

    override fun onResume() {
        super.onResume()

        initToolBar()
        initOtherUser()
        initButtonListeners()
        initRecycleView()
    }

    override fun onPause() {
        super.onPause()

        mToolBarInfo.visibility = View.GONE
        mRefOtherUser.removeEventListener(mListenerInfoToolbar)
        mListListeners.forEach{
            mRefMessages.removeEventListener(it)
        }
        println()
    }


    private fun initToolBar() {
        mToolBarInfo = APP_ACTIVITY.mToolBar.info_toolbar
        mToolBarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mOtherUser = it.getUserModel()
            updateInfoToolBar()
        }
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

    private fun initOtherUser() {
        mRefOtherUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefOtherUser.addValueEventListener(mListenerInfoToolbar)
    }

    private fun initButtonListeners() {
        chat_btn_sent_message.setOnClickListener {
            mSmoothScrollToPosition = true
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

    private fun initRecycleView() {

        mRecyclerView = chat_recycle_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
        mRecyclerView.adapter = mAdapter

        mMessagesListeners = AppChildEventListener { snapshot ->
            mAdapter.addItem(snapshot.getCommonModel())
            if (mSmoothScrollToPosition) {
                mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }
        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)
        mListListeners.add(mMessagesListeners)

        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0){
                    updateData()
                }
            }
        })
    }

    private fun updateData() {

        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)
        mListListeners.add(mMessagesListeners)
    }
}