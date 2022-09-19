package com.saltario.scribo.ui.fragments.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DatabaseReference
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.fragments.BaseFragment
import com.saltario.scribo.ui.objects.AppChildEventListener
import com.saltario.scribo.ui.objects.AppTextWatcher
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    // Количество сообщений, которое будет загружаться и подгружаться сразу
    private var mCountMessages = 15

    // Список
    private lateinit var mRecyclerView: RecyclerView
    // Адаптер списка
    private lateinit var mAdapter: SingleChatAdapter

    // Если пользователь скролит список?
    private var mIsScrolling = false

    private var mSmoothScrollToPosition = true
    // true - скролить вниз (новое сообщение)
    // false - скролить вверх (подгрузка сообщений)

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder

    override fun onResume() {
        super.onResume()

        initVoiceRecorder()
        initLayoutFields()
        initToolBar()
        initOtherUser()
        initButtonListeners()
        initRecycleView()
    }

    private fun initVoiceRecorder() {
        mAppVoiceRecorder = AppVoiceRecorder()
    }

    override fun onPause() {
        super.onPause()

        mToolBarInfo.visibility = View.GONE
        mRefOtherUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListeners)
    }

    private fun initLayoutFields() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initButtonListeners() {
        // Отправка сообщений
        chat_btn_sent_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast(getString(R.string.chat_toast_message_is_empty))
            } else {
                sendMessageAsText(message, contact.id){
                    chat_input_message.setText("")
                }
            }
        }
        // Отправка вложения
        chat_btn_attach.setOnClickListener {
            mSmoothScrollToPosition = true
            attachFile()
        }

        CoroutineScope(Dispatchers.IO).launch {
            chat_btn_voice.setOnTouchListener { v, event ->

                if (checkPermission(RECORD_AUDIO)){
                    if (event.action == MotionEvent.ACTION_DOWN){
                        chat_input_message.setText(getString(R.string.chat_record))
                        chat_btn_voice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, R.color.color_black))
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecorder(messageKey)

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        chat_input_message.setText("")
                        chat_btn_voice.colorFilter = null
                        mAppVoiceRecorder.stopRecorder { file, messageKey ->

                            uploadFileToStorage(Uri.fromFile(file), messageKey)
                        }
                    }
                }
                true
            }
        }

        // Проверяем если поле ввода текста пустое то скрываем кнопку отправки
        chat_input_message.addTextChangedListener(AppTextWatcher{
            val string = chat_input_message.text.toString()
            if (string.isEmpty() || string == getString(R.string.chat_record)){
                chat_btn_sent_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_sent_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })
    }

    private fun initRecycleView() {

        mRecyclerView = chat_recycle_view
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false

        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
        mRecyclerView.adapter = mAdapter

        mMessagesListeners = AppChildEventListener { snapshot ->

            val message = snapshot.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }

            } else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)

        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsScrolling &&
                    dy < 0 &&
                    mLayoutManager.findFirstVisibleItemPosition() <= mCountMessages){
                    updateData()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {

        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += mCountMessages
        mRefMessages.removeEventListener(mMessagesListeners)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)
    }

    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(ATTACH_IMAGE_WIDTH, ATTACH_IMAGE_HEIGHT)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {

            val uri = CropImage.getActivityResult(data).uri
            val messageKey = getMessageKey(contact.id)
            val path = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_IMAGE).child(messageKey)

            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    sendMessageAsImage(it, contact.id, messageKey)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAppVoiceRecorder.releaseRecorder()
    }
}