package edu.utap.DamiKitz.ui.activities

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.User
import edu.utap.DamiKitz.utils.Constants
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.iv_user_photo

class SettingsActivity : UiComponentsActivity() {

    // Declare variable to store user details
    private lateinit var myUserDetails : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set up action bar
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        // Handle click on back button in action bar
        toolbar_settings_activity.setNavigationOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle logout button click
        button_logout.setOnClickListener {
            showProgressBar("Logging You Out")
            Firebase.auth.signOut()
            hideProgressBar()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Handle click on edit profile text view
        tv_edit.setOnClickListener {
            val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
            intent.putExtra("fromSettings",1) // Inform UserProfileActivity that we are coming from settings
            intent.putExtra(Constants.EXTRA_USER_DETAILS,myUserDetails)
            startActivity(intent)
        }

        // Handle click on address layout
        ll_address.setOnClickListener{
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to fetch user details
    private fun getUserDetails(){
        showProgressBar(resources.getString(R.string.please_wait))
        Database().getCurrentUserDetails(this)
    }

    // Function to handle successful retrieval of user details
    fun userDetailsRetrievedSuccess(user: User){
        myUserDetails = user
        hideProgressBar()
        LoadGlide(this@SettingsActivity).loadUserPicture(user.profilePicture,iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_email.text = user.email
        tv_phone_number.text = user.phoneNumber.toString()
        tv_gender.text = user.gender
    }

    override fun onResume() {
        super.onResume()
        // Fetch user details onResume
        getUserDetails()
    }
}
