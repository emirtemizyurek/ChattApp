package com.emirhan.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {

    private lateinit var recyclerViewNewMessages:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        initThis()
        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun initThis(){
        recyclerViewNewMessages = findViewById(R.id.recyclerview_newmessages)
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                snapshot.children.forEach{
                    Log.d("NewMessages",it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        adapter.add(UserItem(user))
                    }

                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLog::class.java)
                    /*intent.putExtra(USER_KEY,userItem.user.username)*/
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerViewNewMessages.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}

class UserItem(val user : User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val username = viewHolder.itemView.findViewById<TextView>(R.id.username_textview_newmessages)
        val image = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_new_messages)
        username.text = user.username
        Picasso.get().load(user.profileImageUrl).into(image)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_messages
    }
}