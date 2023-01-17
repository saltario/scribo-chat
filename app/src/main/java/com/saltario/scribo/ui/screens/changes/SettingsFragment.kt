package com.saltario.scribo.ui.screens.changes

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.ui.objects.AppStates
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)
        hideToolBar()
        showNavBottom()
        initFields()
        initListeners()
    }

    private fun initListeners() {

        settings_btn_edit_profile.setOnClickListener { showToast("Редактировать настройки (потом)") }
        settings_btn_data.setOnClickListener { showToast("Редактировать данные (потом)") }
        settings_btn_appearance.setOnClickListener { showToast("Редактировать оформление (потом)") }
        settings_btn_exit_profile.setOnClickListener { profileExit() }
        settings_hyperlink_processing_and_storage.setOnClickListener { showToast("Обработка данных (потом)") }
        settings_hyperlink_privacy_policy.setOnClickListener { showToast("Политика (потом)") }

//        settings_btn_change_bio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
//        settings_btn_change_username.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
//        settings_btn_change_photo.setOnClickListener { changePhoto() }
    }

    private fun profileExit() {
        AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
    }

    private fun initFields() {
//        settings_bio.text = USER.bio
        settings_fullname.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_status.text = USER.state
        settings_username.text = USER.username
        settings_photo.downloadAndSetImage(USER.photoUrl)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.settings_action_menu_exit -> {
//                AppStates.updateState(AppStates.OFFLINE)
//                AUTH.signOut()
//                restartActivity()
//            }
//            R.id.settings_action_menu_change_name -> {
//                replaceFragment(ChangeFullnameFragment())
//            }
//        }
//        return true
//    }

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
            && resultCode == RESULT_OK && data != null
        ) {

            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

            putFileToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        settings_photo.downloadAndSetImage(it)
                        USER.photoUrl = it
                        showToast(getString(R.string.app_toast_data_update))
                    }
                }
            }
        }
    }
}