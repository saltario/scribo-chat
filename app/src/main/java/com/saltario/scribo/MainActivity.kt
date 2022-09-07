package com.saltario.scribo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.saltario.scribo.activities.RegisterActivity
import com.saltario.scribo.databinding.ActivityMainBinding
import com.saltario.scribo.ui.fragments.ChatsFragment
import com.saltario.scribo.ui.objects.AppDrawer
import com.saltario.scribo.utilits.AUTH
import com.saltario.scribo.utilits.initFirebase
import com.saltario.scribo.utilits.replaceActivity
import com.saltario.scribo.utilits.replaceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mToolBar: Toolbar
    private lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()
        initFields()
        initFunc()
    }

    private fun initFields() {
        mToolBar = mBinding.mainToolBar
        mAppDrawer = AppDrawer(this, mToolBar)
        initFirebase()
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