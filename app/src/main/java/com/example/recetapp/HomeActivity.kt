package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recetapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize edge-to-edge and view binding
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Get the current logged-in user
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            // Extract the username from the email before '@'
            val email = currentUser.email
            val username = email?.substringBefore("@")
            binding.lblUser.text = "$username"
        } else {
            // If the user is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the logout button
        binding.btnLogout.setOnClickListener {
            // Sign out from FirebaseAuth
            firebaseAuth.signOut()
            // Redirect to LoginActivity after signing out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Close the current activity
        }
    }
}
