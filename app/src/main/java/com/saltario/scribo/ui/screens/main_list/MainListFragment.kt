package com.saltario.scribo.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.hideKeyboard
import kotlinx.android.synthetic.main.fragment_main_list.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private var mListItems = listOf<Common>()

    // Ссылка на список диалогов для текущего пользователя
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    // Ссылка на сообщения для текущего пользователя
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    // Ссылка на список пользователей
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    override fun onResume() {
        super.onResume()

        updateFragmentFields()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycle_view
        mAdapter = MainListAdapter()

        // Первым запросом получаем список всех диалогов и кастим в Common модель
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener {
            mListItems = it.children.map { it.getCommonModel() }

            // Для каждого диалога, получаем пользователя
            mListItems.forEach { model ->

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

                            mAdapter.updateMainListItem(newModel)
                        })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    private fun updateFragmentFields() {
        APP_ACTIVITY.title = "Чаты"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}