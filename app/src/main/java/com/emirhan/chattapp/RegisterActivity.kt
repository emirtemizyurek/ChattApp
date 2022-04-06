package com.emirhan.chattapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var userEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var registerButton: Button

    private lateinit var alreadyHaveAccountTextView:TextView
    private lateinit var selectPhotoTextView:TextView

    private lateinit var selectPhotoImageView:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initThis()
        initClickableItems()



    }


    private fun initThis(){
        emailEditText = findViewById(R.id.email_edittext_register)
        passwordEditText = findViewById(R.id.password_edittext_register)
        userEditText = findViewById(R.id.username_edittext_register)

        registerButton = findViewById(R.id.register_button_register)

        alreadyHaveAccountTextView = findViewById(R.id.already_have_account_text_view)
        selectPhotoTextView = findViewById(R.id.selectphoto_textview_register)

        selectPhotoImageView = findViewById(R.id.selectphoto_imageview_register)
    }


    private fun initClickableItems(){
        registerButton.setOnClickListener {
            performRegister()

        }
        alreadyHaveAccountTextView.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        selectPhotoImageView.setOnClickListener {
            Log.d("Main","Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    private var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Photo was selected")
        }

        selectedPhotoUri = data!!.data
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

        selectPhotoImageView.setImageBitmap(bitmap)
        selectPhotoTextView.visibility = View.INVISIBLE
    }

    private fun performRegister(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please, fill in the blanks",Toast.LENGTH_SHORT).show()
            return
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    Toast.makeText(this, "Error"+ it.exception!!.message, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                Log.d("Main","Successfully created user with uid: ${it.result.user!!.uid} ")
                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("Main","Failed to create user: ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("Register","Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity","File Location $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, userEditText.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally we saved the user Firebase Database")
            }
    }


}

class User(val uid: String, val username: String, val profileImageUrl: String)