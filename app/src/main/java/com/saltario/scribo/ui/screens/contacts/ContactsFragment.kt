package com.saltario.scribo.ui.screens.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.models.Common
import com.saltario.scribo.ui.screens.single_chat.SingleChatFragment
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<Common, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        showNavBottom()
        showToolBar()
        APP_ACTIVITY.title = getString(R.string.app_title_contacts)
        initRecycleView()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
    }

    private fun initRecycleView() {
        mRecyclerView = contacts_list
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<Common>()
            .setQuery(mRefContacts, Common::class.java)
            .build()

        mAdapter = object: FirebaseRecyclerAdapter<Common, ContactsHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(holder: ContactsHolder, position: Int, model: Common) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)
                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()

                    if (contact.fullname.isEmpty()){
                        holder.name.text = model.fullname
                    } else {
                        holder.name.text = contact.fullname
                    }

                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(SingleChatFragment(model)) }
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener
            }
        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.contact_fullname
        val photo: CircleImageView = view.contact_photo
    }
}