package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SmsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        firebaseAuth = FirebaseAuth.getInstance()

        // Send phone number
        findViewById<Button>(R.id.button_send_phone).setOnClickListener {
            val phoneNumber = findViewById<EditText>(R.id.editText_phone).text.toString()
            sendVerificationCode(phoneNumber)
        }

        // Verify code
        findViewById<Button>(R.id.button_verify).setOnClickListener {
            val code = findViewById<EditText>(R.id.editText_code).text.toString()
            verifyCode(code)
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    findViewById<TextView>(R.id.textView_error).text = "Error: ${e.message}"
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@SmsActivity.verificationId = verificationId
                }
            })
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Redirect to HomeActivity after successful verification
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                findViewById<TextView>(R.id.textView_error).text = "Verification failed"
            }
        }
    }
}
