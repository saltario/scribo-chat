package com.saltario.scribo

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saltario.scribo.database.AUTH
import com.saltario.scribo.database.initDatabase
import com.saltario.scribo.database.initUser
import com.saltario.scribo.databinding.ActivityMainBinding
import com.saltario.scribo.ui.objects.AppStates
import com.saltario.scribo.ui.screens.auth.LoginFragment
import com.saltario.scribo.ui.screens.changes.SettingsFragment
import com.saltario.scribo.ui.screens.contacts.ContactsFragment
import com.saltario.scribo.ui.screens.main_list.MainListFragment
import com.saltario.scribo.utilits.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mToolBar: Toolbar
    lateinit var mNavBottom: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initFields()
        initNavBottomBar()
        initToolbar()

        initDatabase()
        initUser(){
            loginNavigation()
            initContacts()
        }

    }

    private fun initNavBottomBar() {

        mNavBottom = mBinding.mainNavBottom
        mNavBottom.itemIconTintList = null
        mNavBottom.selectedItemId = R.id.nav_bottom_chats
        mNavBottom.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.nav_bottom_chats -> replaceFragment(MainListFragment(), false)
                R.id.nav_bottom_contacts -> replaceFragment(ContactsFragment(), false)
                R.id.nav_bottom_settings -> replaceFragment(SettingsFragment(), false)

                else -> {

                }
            }
            true
        }
    }

    private fun initToolbar() {
        mToolBar = mBinding.mainToolbar
        setSupportActionBar(mToolBar)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
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
    }

    private fun loginNavigation() {

        if (AUTH.currentUser != null){
            replaceFragment(MainListFragment(), false)
        }
        else {
            replaceFragment(LoginFragment(), false)
        }
    }
}