package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.example.recetapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val username = binding.txtUsername.text.toString()
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            // Register the user with the email and password
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, now update the user profile with display name
                            val user: FirebaseUser? = firebaseAuth.currentUser
                            user?.let {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(username) // Set the display name (username)
                                    .build()

                                it.updateProfile(profileUpdates)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            // User's profile updated successfully
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()  // Close RegisterActivity
                                        } else {
                                            binding.lblError.text = "Profile update failed"
                                        }
                                    }
                            }
                        } else {
                            // Registration failed
                            binding.lblError.text = "Registration failed"
                        }
                    }
            } else {
                // If any field is empty
                binding.lblError.text = "Username, email, or password is empty"
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set OnClickListener for lbl_login TextView
        val loginLabel: TextView = findViewById(R.id.lbl_login)
        loginLabel.setOnClickListener {
            // Launch LoginActivity when lbl_login is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
