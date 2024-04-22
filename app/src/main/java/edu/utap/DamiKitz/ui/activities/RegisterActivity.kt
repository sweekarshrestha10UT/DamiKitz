package edu.utap.DamiKitz.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.databinding.ActivityRegisterBinding
import edu.utap.DamiKitz.model.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.progress_bar.*

class RegisterActivity : AppCompatActivity() {

    // Define binding for activity_register layout
    private lateinit var binding: ActivityRegisterBinding
    // Declare variable for progress dialog
    private lateinit var progressDialog: Dialog

    // Function to display progress bar
    fun showProgressBar() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.tv_progress_text.setText(R.string.please_wait)
        progressDialog.show()
    }

    // Function to hide progress bar
    fun hideProgressBar() {
        progressDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Hide the status bar for Android 12 and above
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Set click listener for register button
        button_register.setOnClickListener {
            registerUser()
        }

        // Set up action bar
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_register_activity.setNavigationOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for login text view
        tv_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Function to check registration details validity
    private fun checkRegistrationDetails(): Boolean {
        return when {
            TextUtils.isEmpty(edited_first_name.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,resources.getString(R.string.error_message_enter_first_name),Toast.LENGTH_LONG).show()
                false
            }
            else -> {
                true
            }
        }
    }

    // Function to register user
    private fun registerUser(){

        if(checkRegistrationDetails()){
            showProgressBar()
            val email : String = edited_email.text.toString().trim{it<= ' '}
            val password : String = edited_password.text.toString().trim{it<= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {task->
                    if(task.isSuccessful){
                        val currentUser = Firebase.auth.currentUser
                        val firstName = edited_first_name.text.toString()
                        val lastName = edited_last_name.text.toString()
                        val email = edited_email.text.toString()

                        val user = User(currentUser!!.uid,firstName,lastName,email)
                        Database().registerUser(this@RegisterActivity, user)
                    }else{
                        hideProgressBar()
                        Toast.makeText(this,"Registration failed.",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Function to handle user registration success
    fun userRegistrationSuccess() {
        hideProgressBar()
        Toast.makeText(this, "You've registered successfully.", Toast.LENGTH_LONG).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}
