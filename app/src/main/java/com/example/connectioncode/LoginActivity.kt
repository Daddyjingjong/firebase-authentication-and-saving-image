package com.example.connectioncode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            val email = email_login.text.toString()
            val password = password_login.text.toString()

            Log.d("Login","Attempt login with email/pw:$email/$password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    //else if successful
                    Log.d("RegisterActivity", "Successfully created user with uid:${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Log.d("Register","Failed to create user:${it.message}")
                }
        }
        back_to_register.setOnClickListener {
            finish()
        }
    }
}
