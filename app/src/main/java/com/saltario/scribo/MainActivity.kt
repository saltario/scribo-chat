package com.saltario.scribo

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.saltario.scribo.database.AUTH
import com.saltario.scribo.database.initDatabase
import com.saltario.scribo.database.initUser
import com.saltario.scribo.databinding.ActivityMainBinding
import com.saltario.scribo.ui.fragments.ChatsMainFragment
import com.saltario.scribo.ui.fragments.register.EnterPhoneNumberFragment
import com.saltario.scribo.ui.objects.AppDrawer
import com.saltario.scribo.ui.objects.AppStates
import com.saltario.scribo.utilits.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mToolBar: Toolbar
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initDatabase()
        initUser(){
            initFields()
            initFunc()
            initContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    private fun initFields() {
        APP_ACTIVITY = this
        mToolBar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    private fun initFunc() {
        setSupportActionBar(mToolBar)
        if (AUTH.currentUser != null){
            mAppDrawer.create()
            replaceFragment(ChatsMainFragment(), false)
        }
        else {
            replaceFragment(EnterPhoneNumberFragment(), false)
        }
    }
}