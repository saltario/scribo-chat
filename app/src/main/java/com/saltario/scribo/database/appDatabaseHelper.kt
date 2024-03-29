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
import java.io.File
import java.util.HashMap

//<editor-fold desc="VARIABLES">

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
const val FOLDER_MESSAGE_FILES = "message_files"

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
const val CHILD_FILE_URL = "fileUrl"

// Типы сообщений
const val TYPE_TEXT = "text"
const val TYPE_IMAGE = "image"
const val TYPE_VOICE = "voice"
const val TYPE_FILE = "file"

const val NODE_MAIN_LIST = "main_list"

// Типы чатов
const val TYPE_CHAT = "chat"
const val TYPE_GROUP = "group"
const val TYPE_CHANNEL = "channel"

const val NODE_GROUPS = "groups"
const val NODE_GROUP_MEMBERS = "members"
const val FOLDER_GROUPS_IMAGE = "groups_image"
const val TYPE_USER_GROUP_CREATOR = "creator"
const val TYPE_USER_GROUP_ADMIN = "admin"
const val TYPE_USER_GROUP_MEMBER = "member"


//</editor-fold>

//<editor-fold desc="INIT">

// Инициализация БД
fun initDatabase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

    AUTH = FirebaseAuth.getInstance()
    USER = User()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

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

//</editor-fold>

//<editor-fold desc="SET MODELS">

// Заполняем общую модель
fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()
// Заполняем модель пользователя
fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User()

//</editor-fold>

//<editor-fold desc="SET USER INFORMATION">

fun setBioToDatabase(newBio: String, UID: String = CURRENT_UID) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.app_toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setFullNameToDatabase(fullname: String, UID: String = CURRENT_UID) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.app_toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

//<editor-fold desc="USERNAME">

fun setUsernameToDatabase(newUsername: String, UID: String = CURRENT_UID) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername)
        .setValue(UID)
        .addOnSuccessListener {
            updateCurrentUsername(newUsername)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

private fun updateCurrentUsername(newUserName: String, UID: String = CURRENT_UID) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME)
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

//</editor-fold>

//</editor-fold>

//<editor-fold desc="PHONE CONTACTS">

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

//</editor-fold>

//<editor-fold desc="PUT GET PUT">

// Кладем адрес файла пользователя в БД
inline fun putUrlToDatabase(url: String, UID: String = CURRENT_UID, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
// Получаем адрес файла пользователя из БД
inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url :String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}
// По адресу файла получаем файл и отправляем его в хранилище
inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

//</editor-fold>

//<editor-fold desc="SEND FILE">

fun uploadAnSendFileMessageToStorage(uri: Uri, messageKey: String, otherUserId: String, messageType: String, fileName: String = "") {

    val path = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(it, otherUserId, messageKey, messageType, fileName)
        }
    }
}

private fun sendMessageAsFile(fileUrl: String, otherUserId: String, messageKey: String, messageType: String, fileName: String) {

    // Ссылка на диалог для текущего пользователя
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$otherUserId"
    // Ссылка на диалог для нашего собеседника
    val refDialogOtherUser = "$NODE_MESSAGES/$otherUserId/$CURRENT_UID"

    // Заполненное по модели сообщение для отправки
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = messageType
    mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = fileName

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogOtherUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

//</editor-fold>

//<editor-fold desc="SEND TEXT MESSAGE">

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

fun sendMessageToGroupAsText(message: String, groupId: String, function: () -> Unit) {

    val refGroupMessages = "$NODE_GROUPS/$groupId/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refGroupMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = TYPE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP

    REF_DATABASE_ROOT.child(refGroupMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

//</editor-fold>

//<editor-fold desc="OTHER">

fun getMessageKey(id: String): String {
    return REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
        .child(id).push().key.toString()
}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString())}
}

//</editor-fold>

fun saveToMainList(otherUserId: String, type: String) {

    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$otherUserId"
    val refOtherUser = "$NODE_MAIN_LIST/$otherUserId/$CURRENT_UID"

    val mapUser = hashMapOf<String, Any>()
    val mapOtherUser = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = otherUserId
    mapUser[CHILD_TYPE] = type

    mapOtherUser[CHILD_ID] = CURRENT_UID
    mapOtherUser[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refOtherUser] = mapOtherUser

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteSingleChat(id: String, function: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun clearSingleChat(id: String, function: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {

            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID)
                .removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener { function() }
        }
}

fun addNewGroupToDatabase(
    nameGroup: String,
    photoGroupUri: Uri,
    listContacts: List<Common>,
    function: () -> Unit) {

    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val pathGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathGroupStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    val mapGroupMembers = hashMapOf<String, Any>()
    listContacts.forEach { mapGroupMembers[it.id] = TYPE_USER_GROUP_MEMBER }
    mapGroupMembers[CURRENT_UID] = TYPE_USER_GROUP_CREATOR

    val mapGroupData = hashMapOf<String, Any>()
    mapGroupData[CHILD_ID] = keyGroup
    mapGroupData[CHILD_FULLNAME] = nameGroup
    mapGroupData[CHILD_PHOTO_URL] = "empty"
    mapGroupData[NODE_GROUP_MEMBERS] = mapGroupMembers

    pathGroup.updateChildren(mapGroupData)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {

            if (photoGroupUri != Uri.EMPTY) {
                putFileToStorage(photoGroupUri, pathGroupStorage) {
                    getUrlFromStorage(pathGroupStorage) { photoUri ->
                        pathGroup.child(CHILD_PHOTO_URL).setValue(photoUri)
                        addGroupToMainList(mapGroupData, listContacts){ function() }
                    }
                }
            } else {
                addGroupToMainList(mapGroupData, listContacts){ function() }
            }
        }
}

fun addGroupToMainList(
    mapGroupData: HashMap<String, Any>,
    listContacts: List<Common>,
    function: () -> Unit) {

    val pathMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val mapMainList = hashMapOf<String, Any>()

    mapMainList[CHILD_ID] = mapGroupData[CHILD_ID].toString()
    mapMainList[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach { contact ->
        pathMainList.child(contact.id).child(mapMainList[CHILD_ID].toString()).updateChildren(mapMainList)
    }
    pathMainList.child(CURRENT_UID).child(mapMainList[CHILD_ID].toString()).updateChildren(mapMainList)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}
