package com.saltario.scribo.ui.screens.groups

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.replaceFragment
import com.saltario.scribo.utilits.showToast
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_add_contacts.*

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private lateinit var mToolBarInfo: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mListItems = listOf<Common>()

    // Ссылка на список диалогов для текущего пользователя
    private val mRefContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    // Ссылка на сообщения для текущего пользователя
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    // Ссылка на список пользователей
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    override fun onResume() {
        super.onResume()

        resetListContacts()
        initToolBar()
        initRecyclerView()
        initListeners()
    }

    private fun resetListContacts() {
        mListContacts.clear()
    }

    private fun initToolBar() {
        mToolBarInfo = APP_ACTIVITY.mToolBar.toolbar_add_contacts
        mToolBarInfo.visibility = View.VISIBLE
    }

    private fun initListeners() {
        add_contacts_btn_next.setOnClickListener {
            if (mListContacts.isEmpty()) {
                showToast(getString(R.string.app_toast_phone_contacts_is_empty))
            } else {
                replaceFragment(CreateGroupFragment(mListContacts))
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun initRecyclerView() {

        mRecyclerView = add_contacts_recycle_view
        mAdapter = AddContactsAdapter()

        // Первым запросом получаем список всех диалогов и кастим в Common модель
        mRefContactsList.addListenerForSingleValueEvent(AppValueEventListener {
            mListItems = it.children.map { it.getCommonModel() }

            // Для каждого диалога, получаем пользователя
            mListItems.forEach { model ->

                if (model.type != TYPE_GROUP)

                // Записываем пользователя в новую модель
                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener{
                    val newModel = it.getCommonModel()

                    // Получаем последнее сообщение
                    mRefMessages.child(model.id).limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener {

                            val tempList = it.children.map { it.getCommonModel() }

                            if (tempList.isEmpty()) { newModel.lastMessage = getString(R.string.app_last_group_message_default) }
                            else {
                                when (newModel.type) {
                                    TYPE_IMAGE -> { newModel.lastMessage = getString(R.string.app_last_message_image_default) }
                                    TYPE_VOICE -> { newModel.lastMessage = getString(R.string.app_last_message_voice_default) }
                                    TYPE_FILE -> { newModel.lastMessage = getString(R.string.app_last_message_file_default) }
                                    else -> { newModel.lastMessage = tempList[0].text }
                                }
                            }

                            if (newModel.fullname.isEmpty()){ newModel.fullname = newModel.phone }

                            mAdapter.updateListItem(newModel)
                        })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    companion object {
        val mListContacts = mutableListOf<Common>()
    }
}