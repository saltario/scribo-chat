package com.saltario.scribo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.saltario.scribo.activities.RegisterActivity
import com.saltario.scribo.databinding.ActivityMainBinding
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.fragments.ChatsFragment
import com.saltario.scribo.ui.objects.AppDrawer
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mToolBar: Toolbar
    lateinit var mAppDrawer: AppDrawer

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK
            && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)

            path.putFile(uri).addOnCompleteListener{
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))
                }
            }
        }
    }

    private fun initFields() {
        APP_ACTIVITY = this
        mToolBar = mBinding.mainToolBar
        mAppDrawer = AppDrawer(this, mToolBar)
        initFirebase()
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

    fun hideKeyboard(){
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}