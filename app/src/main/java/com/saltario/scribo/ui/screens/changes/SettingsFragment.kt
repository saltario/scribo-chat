package com.saltario.scribo.ui.screens.changes

import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.ui.objects.AppStates
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.ui.screens.auth.LoginFragment
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()

        setUserInfoToLayout()
        prepareView()
        initListeners()
    }

    private fun prepareView() {
        setHasOptionsMenu(false)
        hideToolBar()
        showNavBottom()
        setStatusBarColor(R.color.dark_background)
    }

    private fun initListeners() {
        settings_btn_edit_profile.setOnClickListener { profileEdit() }
        settings_btn_exit_profile.setOnClickListener { profileExit() }

        settings_btn_data.setOnClickListener { dataSettingsEdit() }
        settings_btn_appearance.setOnClickListener { appearanceSettingsEdit() }

        settings_hyperlink_processing_and_storage.setOnClickListener { showToast("Обработка данных (потом)") }
        settings_hyperlink_privacy_policy.setOnClickListener { showToast("Политика (потом)") }
    }

    private fun appearanceSettingsEdit() {
        showToast("Редактировать оформление (потом)")
    }

    private fun dataSettingsEdit() {
        showToast("Редактировать данные (потом)")
    }

    private fun profileExit() {
        AppStates.updateState(AppStates.OFFLINE)
        AUTH.signOut()
        replaceFragment(LoginFragment())
    }

    private fun setUserInfoToLayout() {
        settings_fullname.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_status.text = USER.state
        settings_username.text = getString(R.string.text_dog) + USER.username
        settings_photo.downloadAndSetImage(USER.photoUrl)
    }

    private fun profileEdit() {
        replaceFragment(EditProfileFragment())
    }

}