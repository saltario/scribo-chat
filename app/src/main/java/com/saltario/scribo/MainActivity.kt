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
import com.saltario.scribo.ui.screens.changes.SettingsFragment
import com.saltario.scribo.ui.screens.contacts.ContactsFragment
import com.saltario.scribo.ui.screens.main_list.MainListFragment
import com.saltario.scribo.ui.screens.register.EnterPhoneNumberFragment
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.READ_CONTACTS
import com.saltario.scribo.utilits.initContacts
import com.saltario.scribo.utilits.replaceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mToolBar: Toolbar
    lateinit var mNavBottom: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initDatabase()
        initUser(){
            initFields()
            initNavBottomBar()
            initFunc()
            initContacts()
        }
    }

    private fun initNavBottomBar() {

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
        mToolBar = mBinding.mainToolbar
        mNavBottom = mBinding.mainNavBottom
        mNavBottom.itemIconTintList = null
    }

    private fun initFunc() {

        setSupportActionBar(mToolBar)
        if (AUTH.currentUser != null){
            mNavBottom.selectedItemId = R.id.nav_bottom_chats
            replaceFragment(MainListFragment(), false)
        }
        else {
            replaceFragment(EnterPhoneNumberFragment(), false)
        }
    }
}