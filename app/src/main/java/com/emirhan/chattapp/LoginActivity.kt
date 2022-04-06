package com.emirhan.chattapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var loginButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initThis()
        initClickableItems()
    }

    private fun initThis(){
        emailEditText = findViewById(R.id.email_edittext_login)
        passwordEditText = findViewById(R.id.password_edittext_login)

        loginButton = findViewById(R.id.login_button_login)
    }

    private fun initClickableItems(){
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                /*.addOnCompleteListener {

                }*/
        }
    }
}