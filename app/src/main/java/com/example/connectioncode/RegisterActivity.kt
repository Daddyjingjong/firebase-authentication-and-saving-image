package com.example.connectioncode

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
           performRegister()
        }
        already_have_account.setOnClickListener {
            Log.d("RegisterActivity","Try logging in")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        selectphoto_imageview_register.setOnClickListener {
            Log.d("RegisterActivity","Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)



            selectphoto_imageview_register.setImageBitmap(bitmap)

        }
    }

    private fun performRegister(){
        val email = email_register.text.toString()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please fill in the fields above",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity","Email is: "+email)
        Log.d("RegisterActivity","Password is: "+password)

        //Firebase Authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if successful
                Log.d("RegisterActivity", "Successfully created user with uid:${it.result?.user?.uid} ")

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("Register","Failed to create user:${it.message}")
                Toast.makeText(this,"Failed to create user:${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null)return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Successfully uploaded image:${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","File location:$it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,username_register.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally we saved user to firebase database")
            }
    }
}

class User(val uid:String,val username:String, val profileImageUrl:String)