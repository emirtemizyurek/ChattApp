package com.emirhan.chattapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatLog : AppCompatActivity() {

    private lateinit var recyclerviewChatLog: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        val username = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = username?.username

        recyclerviewChatLog = findViewById(R.id.recyclerview_chat_log)

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())

        recyclerviewChatLog.adapter = adapter

    }
}

class ChatFromItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}