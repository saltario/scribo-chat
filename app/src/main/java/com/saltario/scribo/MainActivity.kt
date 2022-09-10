package com.saltario.scribo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.saltario.scribo.activities.RegisterActivity
import com.saltario.scribo.databinding.ActivityMainBinding
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.fragments.ChatsFragment
import com.saltario.scribo.ui.objects.AppDrawer
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mToolBar: Toolbar
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initDatabase()
        initUser(){
            initFields()
            initFunc()
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
        mToolBar = mBinding.mainToolBar
        mAppDrawer = AppDrawer(this, mToolBar)
    }

    private fun initFunc() {

        if (AUTH.currentUser != null){
            setSupportActionBar(mToolBar)
            mAppDrawer.create()
            replaceFragment(ChatsFragment(), false)
        }
        else {
            replaceActivity(RegisterActivity())
        }
    }
}