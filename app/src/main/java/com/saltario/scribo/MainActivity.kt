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

        initFields()
        initFunc()
    }

    private fun initFields() {
        APP_ACTIVITY = this
        mToolBar = mBinding.mainToolBar
        mAppDrawer = AppDrawer(this, mToolBar)
        initDatabase()
        initUser()
    }

    private fun initUser() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
            .addListenerForSingleValueEvent(AppValueEventListener{
                USER = it.getValue(User::class.java) ?: User()
            })
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