package com.saltario.scribo.utilits

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.saltario.scribo.MainActivity
import com.saltario.scribo.R
import com.saltario.scribo.database.TYPE_CHAT
import com.saltario.scribo.database.TYPE_GROUP
import com.saltario.scribo.database.updatePhonesFromDatabase
import com.saltario.scribo.models.Common
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String){
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity(){
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true){

    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment)
            .commit()
    }
}

fun Fragment.replaceFragment(fragment: Fragment){
    fragmentManager?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(R.id.data_container, fragment)
        ?.commit()
}

fun hideKeyboard(){
    val inputManager: InputMethodManager = APP_ACTIVITY
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String, chatType: String = TYPE_CHAT){

    var defaultPhoto: Int = R.drawable.dark_default_user_photo
    if (chatType == TYPE_GROUP) { defaultPhoto = R.drawable.dark_default_group_photo }

    Picasso.get().load(url)
        .fit()
        .placeholder(defaultPhoto)
        .into(this)
}

@SuppressLint("Range")
fun initContacts() {
    if (checkPermission(READ_CONTACTS)){
        var arrayContacts = arrayListOf<Common>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (cursor.moveToNext()) {
                val fullname = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val newModel = Common()
                newModel.fullname = fullname
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesFromDatabase(arrayContacts)
    }
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

@SuppressLint("Range")
fun getFileNameFromUri(uri: Uri): String {

    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)

    try {
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        showToast(e.message.toString())
    } finally {
        cursor?.close()
    }

    return result
}

fun getPlurals(count: Int) =
    APP_ACTIVITY.resources.getQuantityString(R.plurals.count_members, count, count)

fun hideNavBottom() {
    APP_ACTIVITY.main_nav_bottom.visibility = View.GONE
}

fun showNavBottom() {
    APP_ACTIVITY.main_nav_bottom.visibility = View.VISIBLE
}

fun hideToolBar() {
    APP_ACTIVITY.main_toolbar.visibility = View.GONE
}

fun showToolBar() {
    APP_ACTIVITY.main_toolbar.visibility = View.VISIBLE
}

fun setStatusBarColor(color: Int) {
    val window: Window = APP_ACTIVITY.window
    window.statusBarColor = APP_ACTIVITY.resources.getColor(color)
}
