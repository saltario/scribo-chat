package com.saltario.scribo.ui.screens.changes

import com.saltario.scribo.R
import com.saltario.scribo.database.USER
import com.saltario.scribo.database.setFullNameToDatabase
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_change_name.*

class ChangeFullnameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()

        initFullnameList()
    }

    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ")

        if (fullnameList.size > 1) {
            settings_input_name.setText(fullnameList[0])
            settings_input_surname.setText(fullnameList[1])
        } else {
            settings_input_name.setText(fullnameList[0])
        }
    }

    override fun change() {
        super.change()

        val name = settings_input_name.text.toString()
        val surname = settings_input_surname.text.toString()

        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))

        } else {
            val fullname = "$name $surname"
            setFullNameToDatabase(fullname)
        }

    }
}