package com.saltario.scribo.ui.fragments

import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.utilits.replaceFragment
import com.saltario.scribo.utilits.showToast
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    override fun onStart() {
        super.onStart()

        register_btn_next.setOnClickListener { sentCode() }
    }

    private fun sentCode() {

        if (register_input_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone_number))
        } else {
            replaceFragment(EnterCodeFragment())
        }
    }
}