package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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

        // Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            // Login the user with the email and password
            if (email.isNotEmpty() && password.isNotEmpty()) {
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

        // Forgot Password click listener
        binding.lblForgotPassword.setOnClickListener {
            showEmailInputDialog()  // Updated to show email input dialog
        }

        // Handle window insets (if needed)
        ViewCompat.setOnApplyWindowInsetsListener(binding.imageView3) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Register label click listener
        binding.lblRegister.setOnClickListener {
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

    // New function for password reset email input dialog
    private fun showEmailInputDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Enter your email"

        builder.setTitle("Password Reset")
        builder.setMessage("Please enter your email to receive a password reset link.")
        builder.setView(input)  // Set the EditText inside the AlertDialog

        builder.setPositiveButton("Send") { dialog, _ ->
            val email = input.text.toString()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showMessage("Password reset email sent.")
                        } else {
                            showMessage("Failed to send password reset email.")
                        }
                    }
            } else {
                showMessage("Please enter a valid email.")
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Function to show messages
    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
