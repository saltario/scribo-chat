package com.saltario.scribo.database

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.models.User
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.showToast

// Realtime Database
lateinit var REF_DATABASE_ROOT: DatabaseReference
// Storage
lateinit var REF_STORAGE_ROOT: StorageReference

// Данные аутентификации (текущий авторизованный пользователь)
lateinit var AUTH: FirebaseAuth
// Модель пользователя (локальная)
lateinit var USER: User
// Идентификатор текущего пользователя (id)
lateinit var CURRENT_UID: String

// Адрес по которому хранятся аватарки пользователей
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSAGE_IMAGE = "message_image"

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

// Хранит все сообщения пользователей
const val NODE_MESSAGES = "messages"
// Состав сообщения
const val CHILD_TEXT = "text"
const val CHILD_FROM = "from"
const val CHILD_TYPE = "type"
const val CHILD_TIME = "time"
const val CHILD_IMAGE_URL = "imageUrl"

// Типы сообщений
const val TYPE_TEXT = "text"
const val TYPE_IMAGE = "image"

// Инициализация БД
fun initDatabase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

    AUTH = FirebaseAuth.getInstance()
    USER = User()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

// Кладем адрес фотографии пользователя в БД
inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
// Получаем адрес фотографии пользователя из БД
inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url :String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}
// По адресу фотографии получаем картинку и отправляем ее в хранилище
inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Заполняем общую модель
fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()
// Заполняем модель пользователя
fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User()

// Инициализируем пользователя
inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener{
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()){
                USER.username = CURRENT_UID
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
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }
                        // Также записываем полное имя на всякий случай( если нету fullname в БД)
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.app_toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setFullNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.app_toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.mAppDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setUsernameToDatabase(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername)
        .setValue(CURRENT_UID)
        .addOnSuccessListener {
            updateCurrentUsername(newUsername)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

private fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.app_toast_data_update))
            deleteOldUsername(newUserName)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username)
        .removeValue()
        .addOnSuccessListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsText(message: String, otherUserId: String, function: () -> Unit) {
    // Ссылка на диалог для текущего пользователя
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$otherUserId"
    // Ссылка на диалог для нашего собеседника
    val refDialogOtherUser = "$NODE_MESSAGES/$otherUserId/$CURRENT_UID"
    // Уникальный ключ для сообщения
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key
    // Заполненное по модели сообщение для отправки
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = TYPE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogOtherUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsImage(imageUrl: String, otherUserId: String, messageKey: String) {
    // Ссылка на диалог для текущего пользователя
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$otherUserId"
    // Ссылка на диалог для нашего собеседника
    val refDialogOtherUser = "$NODE_MESSAGES/$otherUserId/$CURRENT_UID"

    // Заполненное по модели сообщение для отправки
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = TYPE_IMAGE
    mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP
    mapMessage[CHILD_IMAGE_URL] = imageUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogOtherUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}
