package edu.utap.DamiKitz.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.User
import edu.utap.DamiKitz.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progress_bar.*

// Global progress bar dialog instance
lateinit var progressDialog: Dialog

// Firebase authentication instance
private lateinit var auth: FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Function to show progress bar dialog
    private fun showProgressBar() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.tv_progress_text.setText(R.string.please_wait)
        progressDialog.show()
    }

    // Function to hide progress bar dialog
    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Button click listener for logging in
        button_login.setOnClickListener {
            loginUser()
        }

        // Initialize Firebase authentication
        auth = Firebase.auth

        // Check if a user is already logged in
        val currentUser = auth.currentUser

        // Redirect to forgot password activity
        tv_forgot_password.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Redirect to dashboard activity if user is already logged in
        if (currentUser != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Hide status bar
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Redirect to registration activity
        tv_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to log in the user
    fun loginUser() {
        val email = edited_email.text.toString()
        val password = edited_password.text.toString()

        // Check if email and password are not empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Show progress bar while attempting to log in
            showProgressBar()
            // Sign in with email and password
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // Check if login is successful
                if (task.isSuccessful) {
                    // Fetch user details from database
                    Database().getCurrentUserDetails(this@LoginActivity)
                } else {
                    // Hide progress bar and display login failed message
                    hideProgressBar()
                    Toast.makeText(this, getString(R.string.login_failed_try_again), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // Hide progress bar and display error message for empty fields
            hideProgressBar()
            Toast.makeText(this, getString(R.string.dont_leave_entries_blank), Toast.LENGTH_LONG).show()
        }
    }

    // Function to handle successful user login
    fun userLoggedInSuccess(user: User) {
        // Hide progress bar
        hideProgressBar()

        // Retrieve username from shared preferences
        val sharedPreferences = getSharedPreferences(Constants.SHOP_PREFERENCES, MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.CURRENT_NAME, "")

        // Construct welcome message with username
        val welcomeString: String = getString(R.string.welcome_user) + "$username"

        // Display welcome message
        Toast.makeText(this, welcomeString, Toast.LENGTH_LONG).show()

        // Check if user profile is completed
        if (user.profileCompleted == 0) {
            // Redirect to user profile activity if profile is incomplete
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirect to dashboard activity if profile is complete
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        // Finish the login activity
        finish()
    }
}
