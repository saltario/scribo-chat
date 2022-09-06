package com.saltario.scribo.ui.fragments

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.utilits.AppTextWatcher
import com.saltario.scribo.utilits.showToast
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment : Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()

        register_input_code.addTextChangedListener(AppTextWatcher{

            val string: String = register_input_code.text.toString()
            if (string.length == 6){
                verifyCode()
            }
        })
    }

    private fun verifyCode() {

        showToast("OK")

    }
}