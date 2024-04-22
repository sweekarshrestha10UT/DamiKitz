package edu.utap.DamiKitz.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.DamiKitz.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.progress_bar.*

class ForgotPasswordActivity : AppCompatActivity() {

    // Dialog instance for progress bar
    private lateinit var progressDialog: Dialog

    // Function to show progress bar dialog
    private fun showProgressBar() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.tv_progress_text.setText(R.string.please_wait)
        progressDialog.show()
    }

    // Function to hide progress bar dialog
    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Set up action bar
        setSupportActionBar(toolbar_forgot_password_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        // Set up navigation click listener
        toolbar_forgot_password_activity.setNavigationOnClickListener {
            // Navigate back to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for reset password button
        button_send_resedited_email.setOnClickListener {
            // Call function to send reset email
            sendResetPasswordEmail()
        }
    }

    // Function to send reset password email
    fun sendResetPasswordEmail() {
        val email = edited_emailForgotPass.text.toString()

        // Show progress bar while sending reset email
        showProgressBar()

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                // Hide progress bar after task completion
                hideProgressBar()

                if (task.isSuccessful) {
                    // Show success message and navigate back to login activity
                    Toast.makeText(this, "An Email Has Been Sent to you to reset your password.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Show error message if task fails
                    Toast.makeText(this, "An error has occurred. Please re-enter your email.", Toast.LENGTH_LONG).show()
                }
            }
    }
}
