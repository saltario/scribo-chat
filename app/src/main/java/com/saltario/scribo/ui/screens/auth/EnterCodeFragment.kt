package com.saltario.scribo.ui.screens.auth

import androidx.fragment.app.Fragment
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.ui.objects.AppTextWatcher
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment(val phoneNumber: String, val id: String, val firstTime: Boolean) : Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()

        hideNavBottom()

        back_btn.setOnClickListener { parentFragmentManager.popBackStack() }

        enter_code.addTextChangedListener(AppTextWatcher{

            val string: String = enter_code.text.toString()
            if (string.length == 6){
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code: String = enter_code.text.toString()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(id, code)

        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val uid: String = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener{

                        if (!it.hasChild(CHILD_USERNAME)) dateMap[CHILD_USERNAME] = uid

                        REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                    .addOnFailureListener { showToast(it.message.toString()) }
                                    .addOnSuccessListener{
                                        if (firstTime) replaceFragment(RegisterFragment(uid))
                                        else restartActivity()
                                }
                            }
                    })
            } else {
                showToast(task.exception?.message.toString())
            }
        }
    }
}