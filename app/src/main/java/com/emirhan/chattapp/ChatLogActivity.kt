package com.emirhan.chattapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.emirhan.chattapp.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLog : AppCompatActivity() {

    companion object{
        val TAG = "Chatlog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    private lateinit var recyclerviewChatLog: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var editTextChatLog: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        initThis()

        recyclerviewChatLog.adapter = adapter



        val username = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = username?.username

        /*setupDummyData()*/

        listenForMessages()


        sendButton.setOnClickListener {
            Log.d(TAG,"attempt to send message...")
            performSendMessage()
        }
    }
    private fun initThis(){
        sendButton = findViewById(R.id.send_button_chat_log)
        editTextChatLog = findViewById(R.id.editText_chat_log)
        recyclerviewChatLog = findViewById(R.id.recyclerview_chat_log)
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }

                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun performSendMessage(){

        val text = editTextChatLog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        val username = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId =username?.uid

        if(fromId == null) return
        if(toId == null) return


        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() /1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
            }
    }

    private fun setupDummyData(){


        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatToItem(""))
        adapter.add(ChatFromItem(""))
        adapter.add(ChatToItem(""))
        adapter.add(ChatFromItem(""))

        recyclerviewChatLog.adapter = adapter
    }
}


class ChatFromItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}