package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recetapp.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.gms.tasks.Task

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set OnClickListener for Register Button
        binding.btnRegister.setOnClickListener {
            val username = binding.txtUsername.text.toString()
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, update profile with display name
                            val user: FirebaseUser? = firebaseAuth.currentUser
                            user?.let {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()

                                it.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        // Redirect to SmsActivity after successful registration
                                        startActivity(Intent(this, SmsActivity::class.java))
                                        finish()
                                    } else {
                                        binding.lblError.text = "Profile update failed"
                                    }
                                }
                            }
                        } else {
                            binding.lblError.text = "Registration failed"
                        }
                    }
            } else {
                binding.lblError.text = "Username, email, or password is empty"
            }
        }

        // Set OnClickListener for Google Sign-In Button
        binding.btnGoogleSignUp.setOnClickListener {
            signInGoogle()
        }

        // Apply insets for view padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.go_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set OnClickListener for login label
        binding.lblLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Function to initiate Google Sign-In
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Handle activity result for Google Sign-In
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    // Handle Google Sign-In results
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Sign in to Firebase with Google credentials and navigate to SmsActivity
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Redirect to SmsActivity after successful Google sign-in
                startActivity(Intent(this, SmsActivity::class.java))
                finish() // Close RegisterActivity
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
