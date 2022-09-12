package com.saltario.scribo.utilits

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.objects.AppValueEventListener
import java.util.ArrayList

lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var USER: User
lateinit var UID: String

const val NODE_USERS = "users"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
// Хранит контакты пользователя, которые зарегестрированы в приложении
const val NODE_PHONES_CONTACTS = "phones_contacts"

const val FOLDER_PROFILE_IMAGE = "profile_image"

fun initDatabase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

    AUTH = FirebaseAuth.getInstance()
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url :String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener{
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()){
                USER.username = UID
            }
            function()
        })
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

fun updatePhonesFromDatabase(arrayContacts: ArrayList<Common>) {
    REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
        it.children.forEach{ snapshot ->
            arrayContacts.forEach { contact ->
                if (snapshot.key == contact.phone){
                    REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                        .child(snapshot.value.toString()).child(CHILD_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
    })
}

fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()

fun DataSnapshot.getUser(): User =
    this.getValue(User::class.java) ?: User()
