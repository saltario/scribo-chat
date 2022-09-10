package com.saltario.scribo.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.saltario.scribo.R
import com.saltario.scribo.activities.RegisterActivity
import com.saltario.scribo.utilits.*
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
        initListeners()
    }

    private fun initListeners() {
        settings_btn_change_username.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        settings_btn_change_bio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settings_btn_change_photo.setOnClickListener { changePhoto() }
    }

    private fun initFields() {
        settings_bio.text = USER.bio
        settings_fullname.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_status.text = USER.status
        settings_username.text = USER.username
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_action_menu_exit -> {
                AUTH.signOut()
                APP_ACTIVITY.replaceActivity(RegisterActivity())
            }
            R.id.settings_action_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }

    private fun changePhoto() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(500,500)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null) {

            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)

            path.putFile(uri).addOnCompleteListener{ task1 ->
                if (task1.isSuccessful){
                    path.downloadUrl.addOnCompleteListener {task2 ->
                        if (task2.isSuccessful) {
                            val photoUrl = task2.result.toString()
                            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL)
                                .setValue(photoUrl).addOnCompleteListener { task3 ->
                                    if (task3.isSuccessful) {
                                        settings_photo.downloadAndSetImage(photoUrl)
                                        USER.photoUrl = photoUrl
                                        showToast(getString(R.string.toast_data_update))
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}