package com.saltario.scribo.ui.screens.main_list

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.ui.screens.groups.AddContactsFragment
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.hideKeyboard
import com.saltario.scribo.utilits.replaceFragment
import com.saltario.scribo.utilits.showToolBar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_main_list.*
import kotlinx.android.synthetic.main.toolbar_main_list.view.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private var mListItems = listOf<Common>()

    private lateinit var mToolBarInfo: View

    // Ссылка на список диалогов для текущего пользователя
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    // Ссылка на сообщения для текущего пользователя
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    // Ссылка на список пользователей
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    override fun onResume() {
        super.onResume()

        initFields()
        initToolBar()
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()

        mToolBarInfo.visibility = View.GONE
    }

    private fun initFields() {

        showToolBar()
        setHasOptionsMenu(false)
        hideKeyboard()
    }

    private fun initToolBar() {

        mToolBarInfo = APP_ACTIVITY.mToolBar.toolbar_main_list
        mToolBarInfo.visibility = View.VISIBLE

        mToolBarInfo.toolbar_main_create_group_chat.setOnClickListener {
            replaceFragment(AddContactsFragment())
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycle_view
        mAdapter = MainListAdapter()

        // Первым запросом получаем список всех диалогов и кастим в Common модель
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener {
            mListItems = it.children.map { it.getCommonModel() }

            // Для каждого диалога, получаем пользователя
            mListItems.forEach { model ->

                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    private fun showGroup(model: Common) {

        val pathGroup = REF_DATABASE_ROOT.child(NODE_GROUPS)

        // Записываем пользователя в новую модель
        pathGroup.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener {
                val newModel = it.getCommonModel()

                // Получаем последнее сообщение
                pathGroup.child(model.id).child(NODE_MESSAGES).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener {

                        val tempList = it.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Чат очищен"
                        } else {
                            newModel.lastMessage = tempList[0].text
                        }

                        newModel.type = TYPE_GROUP
                        mAdapter.updateListItem(newModel)
                    })
            })
    }

    private fun showChat(model: Common) {

        // Записываем пользователя в новую модель
        mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {
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

                    if (newModel.fullname.isEmpty()) {
                        newModel.fullname = newModel.phone
                    }

                    newModel.type = TYPE_CHAT
                    mAdapter.updateListItem(newModel)
                })
        })
    }

}