package com.saltario.scribo.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.hideKeyboard
import com.saltario.scribo.utilits.replaceFragment
import com.saltario.scribo.utilits.showToast
import kotlinx.android.synthetic.main.fragment_add_contacts.*

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

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
        updateFragmentFields()
        initRecyclerView()
        initFloatingActionButton()
    }

    private fun resetListContacts() {
        mListContacts.clear()
    }

    private fun initFloatingActionButton() {
        add_contacts_btn_next.setOnClickListener {
            if (mListContacts.isEmpty()) {
                showToast("Добавьте участников")
            } else {
                replaceFragment(CreateGroupFragment(mListContacts))
            }
        }
    }

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

                            if (tempList.isEmpty()) {
                                newModel.lastMessage = "Чат очищен"
                            } else {
                                newModel.lastMessage = tempList[0].text
                            }

                            if (newModel.fullname.isEmpty()){
                                newModel.fullname = newModel.phone
                            }

                            mAdapter.updateListItem(newModel)
                        })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    private fun updateFragmentFields() {
        APP_ACTIVITY.title = "Добавить в группу"
        hideKeyboard()
    }

    companion object {
        val mListContacts = mutableListOf<Common>()
    }
}