package com.saltario.scribo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.saltario.scribo.R
import com.saltario.scribo.databinding.ActivityRegisterBinding
import com.saltario.scribo.ui.fragments.EnterPhoneNumberFragment
import com.saltario.scribo.utilits.replaceFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()

        mToolBar = mBinding.registerToolBar
        setSupportActionBar(mToolBar)
        title = getString(R.string.register_title_your_phone)
        replaceFragment(EnterPhoneNumberFragment())
    }
}