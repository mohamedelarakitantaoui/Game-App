 package com.example.gameproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gameproject.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

 class ForgotPasswordActivity : AppCompatActivity() {
     lateinit var forgotBinding: ActivityForgotPasswordBinding
     val auth = FirebaseAuth.getInstance()

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
         val view = forgotBinding.root
         setContentView(view)

         forgotBinding.buttonReset.setOnClickListener {
             val userEmail = forgotBinding.editTextForgotEmail.text.toString()

             auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                     finish()
                 } else {
                     Toast.makeText(this, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                 }
             }
         }
     }
 }
