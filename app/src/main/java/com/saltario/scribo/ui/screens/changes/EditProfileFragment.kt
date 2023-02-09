package com.saltario.scribo.ui.screens.changes

import android.app.Activity
import android.content.Intent
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.util.*

class EditProfileFragment : BaseFragment(R.layout.fragment_edit_profile) {

    override fun onStart() {
        super.onStart()

        prepareView()
        updateFragmentFields()
        initListeners()
    }

    private fun prepareView() {
        setHasOptionsMenu(false)
        hideToolBar()
        hideNavBottom()
        setStatusBarColor(R.color.dark_background_bar)
    }

    private fun updateFragmentFields() {

        val fullnameList = USER.fullname.split(" ")

        if (fullnameList.size > 1) {
            edit_profile_input_name.setText(fullnameList[0])
            edit_profile_input_surname.setText(fullnameList[1])
        } else {
            edit_profile_input_name.setText(fullnameList[0])
        }

        edit_profile_enter_phone_number.setText(USER.phone)
        edit_profile_input_username.setText(USER.username)
        edit_profile_input_photo.downloadAndSetImage(USER.photoUrl)
    }

    private fun initListeners() {
        edit_profile_input_photo.setOnClickListener { changePhoto() }
        edit_profile_btn_back.setOnClickListener { parentFragmentManager.popBackStack() }
        edit_profile_btn_done.setOnClickListener { done() }
        edit_profile_btn_delete_profile.setOnClickListener { showToast("Удаление аккаунта (потом)") }
    }

    private fun done() {

        changeUserName()
        changeFullName()
        parentFragmentManager.popBackStack()
        replaceFragment(SettingsFragment())

    }

    private fun changeUserName() {

        val username = edit_profile_input_username.text.toString().lowercase(Locale.getDefault())

        if (username != USER.username) {

            if (username.isEmpty()) {
                showToast(getString(R.string.app_toast_username_is_empty))

            } else {

                REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(
                        AppValueEventListener {
                            if (it.hasChild(username)) {
                                showToast(getString(R.string.app_toast_username_not_unique))
                            } else {
                                setUsernameToDatabase(username)
                            }
                        })
            }
        }
    }

    private fun changeFullName() {

        val name = edit_profile_input_name.text.toString()
        val surname = edit_profile_input_surname.text.toString()

        if (name.isEmpty()){
            showToast(getString(R.string.app_toast_name_is_empty))

        } else {
            val fullname = "$name $surname"
            setFullNameToDatabase(fullname)
        }
    }

    private fun changePhoto() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(PROFILE_PHOTO_WIDTH, PROFILE_PHOTO_HEIGHT)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null
        ) {

            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

            putFileToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        edit_profile_input_photo.downloadAndSetImage(it)
                        USER.photoUrl = it
                        showToast(getString(R.string.app_toast_data_update))
                    }
                }
            }
        }
    }
}