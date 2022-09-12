package com.saltario.scribo.ui.fragments

import android.view.View
import com.google.firebase.database.DatabaseReference
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.toolbar_info.view.*

class SingleChatFragment(private val contact: Common) : BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mOtherUser: User
    private lateinit var mToolBarInfo: View
    private lateinit var mRefOtherUser: DatabaseReference

    override fun onResume() {
        super.onResume()
        initFields()
        mToolBarInfo.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
        mRefOtherUser.removeEventListener(mListenerInfoToolbar)
    }

    private fun initFields() {

        mToolBarInfo = APP_ACTIVITY.mToolBar.info_toolbar
        mListenerInfoToolbar = AppValueEventListener {
            mOtherUser = it.getUser()
            updateInfoToolBar()
        }
        mRefOtherUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefOtherUser.addValueEventListener(mListenerInfoToolbar)
    }

    private fun updateInfoToolBar() {
        mToolBarInfo.info_toolbar_fullname.text = mOtherUser.fullname
        mToolBarInfo.info_toolbar_state.text = mOtherUser.state
        mToolBarInfo.info_toolbar_photo.downloadAndSetImage(mOtherUser.photoUrl)
    }
}