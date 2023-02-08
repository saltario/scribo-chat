package com.saltario.scribo.ui.screens.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.ui.screens.main_list.MainListFragment
import com.saltario.scribo.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts: List<Common>) : BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mToolBarInfo: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY

    override fun onResume() {
        super.onResume()

        initToolBar()
        updateFragmentFields()
        initRecyclerView()
        initListeners()
    }

    private fun initToolBar() {
        mToolBarInfo = APP_ACTIVITY.mToolBar.toolbar_new_group
        mToolBarInfo.visibility = View.VISIBLE
    }

    private fun updateFragmentFields() {
        create_group_input_name.requestFocus()
        create_group_counts.text = getPlurals(listContacts.size)
    }

    private fun initListeners() {

        create_group_input_photo.setOnClickListener {
            updateGroupPhoto()
        }

        create_group_btn_finish.setOnClickListener {
            val nameGroup = create_group_input_name.text.toString()
            if (nameGroup.isEmpty()){
                showToast(getString(R.string.app_toast_group_name_is_empty))
            } else {
                addNewGroupToDatabase(nameGroup, mUri, listContacts) {
                    replaceFragment(MainListFragment())
                }
            }
        }
    }

    private fun initRecyclerView() {

        mRecyclerView = create_group_contacts_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter

        listContacts.forEach { mAdapter.updateListItem(it) }
    }

    private fun updateGroupPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(PROFILE_PHOTO_WIDTH, PROFILE_PHOTO_HEIGHT)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null)
        {
            // Получаем фото, но не отправляем в БД
            mUri = CropImage.getActivityResult(data).uri
            create_group_input_photo.setImageURI(mUri)
        }
    }
}