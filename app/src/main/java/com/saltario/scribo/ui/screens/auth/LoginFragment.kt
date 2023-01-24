package com.saltario.scribo.ui.screens.auth

import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.saltario.scribo.R
import com.saltario.scribo.database.AUTH
import com.saltario.scribo.ui.screens.changes.ChangeBioFragment
import com.saltario.scribo.ui.screens.register.EnterCodeFragment
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var firstTime: Boolean = false

    override fun onStart() {
        super.onStart()

        initListeners()
        hideToolBar()
        hideNavBottom()

        mCallback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        showToast("Welcome")
                        restartActivity()
                    } else {
                        showToast(task.exception?.message.toString())
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id, firstTime))
            }
        }
    }

    private fun initListeners() {
        login_btn_login.setOnClickListener{ login() }
        login_btn_registration.setOnClickListener { registration() }
    }

    private fun registration() {
        firstTime = true
        sentCode()
    }

    private fun login() {
        firstTime = false
        sentCode()
    }

    private fun sentCode() {

        if (login_enter_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone_number))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = login_enter_phone_number.text.toString()
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(APP_ACTIVITY)
                .setPhoneNumber(mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallback)
                .build()
        )
    }
}