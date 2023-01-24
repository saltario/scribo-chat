package com.saltario.scribo.ui.screens.auth

import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.ui.objects.AppTextWatcher
import com.saltario.scribo.utilits.hideNavBottom
import com.saltario.scribo.utilits.restartActivity
import kotlinx.android.synthetic.main.fragment_enter_code.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onStart() {
        super.onStart()

        hideNavBottom()

        register_btn_done.setOnClickListener { restartActivity() }

    }

}