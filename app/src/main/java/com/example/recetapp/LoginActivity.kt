package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recetapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Use binding.root here
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Firebase Auth
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            // Login the user with the email and password
            if(email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login success
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Login failed
                            binding.lblError.text = "Login failed"
                        }
                    }
            } else {
                // Email or password is empty
                binding.lblError.text = "Email or password is empty"
            }
        }
        // Handle window insets (if needed)
        ViewCompat.setOnApplyWindowInsetsListener(binding.imageView3) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set an OnClickListener for the lbl_register TextView using binding
        binding.lblRegister.setOnClickListener {
            // Launch RegisterActivity when lbl_register is clicked
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}