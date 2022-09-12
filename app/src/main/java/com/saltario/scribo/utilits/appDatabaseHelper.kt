package com.saltario.scribo.utilits

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.objects.AppValueEventListener

// Realtime Database
lateinit var REF_DATABASE_ROOT: DatabaseReference
// Storage
lateinit var REF_STORAGE_ROOT: StorageReference

// Данные аутентификации (текущий авторизованный пользователь)
lateinit var AUTH: FirebaseAuth
// Модель пользователя (локальная)
lateinit var USER: User
// Идентификатор текущего пользователя (id)
lateinit var UID: String

// Список всех пользователей приложения
const val NODE_USERS = "users"
// Свойства пользователя
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

// Все никнеймы пользователей (для исключения повторов)
const val NODE_USERNAMES = "usernames"
// Список номеров телефонов зарегестрированных пользователей
const val NODE_PHONES = "phones"
// Хранит контакты пользователя, которые зарегестрированы в приложении
const val NODE_PHONES_CONTACTS = "phones_contacts"
// Адрес по которому хранятся аватарки пользователей
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

fun updatePhonesFromDatabase(arrayContacts: ArrayList<Common>) {
    if (AUTH.currentUser != null){
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach{ snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone){
                        // Получаем данные контакта, чьи данные есть в БД
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }
                        // Также записываем полное имя на всякий случай( если нету fullname в БД)
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                            .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()

fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User()
