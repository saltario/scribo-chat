package com.saltario.scribo.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.APP_ACTIVITY
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts: List<Common>) : BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter

    override fun onResume() {
        super.onResume()

        updateFragmentFields()
        initRecyclerView()
        initFloatingActionButton()
    }

    private fun updateFragmentFields() {
        APP_ACTIVITY.title = "Создание группы"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        create_group_input_name.requestFocus()
    }

    private fun initFloatingActionButton() {
        create_group_btn_finish.setOnClickListener {

        }
    }

    private fun initRecyclerView() {

        mRecyclerView = create_group_contacts_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter

        listContacts.forEach { mAdapter.updateListItem(it) }
    }
}