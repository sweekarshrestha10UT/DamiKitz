package edu.utap.DamiKitz.ui.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.databinding.ActivityUserProfileBinding
import edu.utap.DamiKitz.model.User
import edu.utap.DamiKitz.utils.Constants
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import kotlinx.android.synthetic.main.activity_user_profile.view.*
import kotlinx.android.synthetic.main.progress_bar.*

// Activity responsible for displaying and editing user profile details
class UserProfileActivity : AppCompatActivity() {
    // User details and UI binding
    private lateinit var myUserDetails : User
    private lateinit var binding : ActivityUserProfileBinding

    // Activity result launchers for gallery access and permissions
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    // Variables for storing selected picture and user profile image URL
    var selectedPicture: Uri? = null
    var userProfileImageURL : String = ""

    // Activity lifecycle method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating layout and setting content view
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Registering activity result launchers

        registerLauncher()

        // Retrieving user details from intent extras
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            myUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        // Checking if activity was launched from settings
        val comingFromSettings = intent.getIntExtra("fromSettings",0)
        if(comingFromSettings == 1){
            // Updating UI and toolbar if launched from settings
            toolbar_user_profile_activity.tv_title.text = getString(R.string.edit_profile)
            setupActionBar()
            LoadGlide(this@UserProfileActivity).loadUserPicture(myUserDetails.profilePicture, iv_user_photo)
            edited_first_name.setText(myUserDetails.firstName)
            edited_last_name.setText(myUserDetails.lastName)

            edited_email.isEnabled = false
            edited_email.setText(myUserDetails.email)

            if (myUserDetails.phoneNumber != 0L) {
                edited_phone_number.setText(myUserDetails.phoneNumber.toString())
            }
            if (myUserDetails.gender == "Male") {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }

        }

        // Disabling input fields for user details
        edited_first_name.isEnabled = false
        edited_first_name.setText(myUserDetails.firstName)

        edited_last_name.isEnabled = false
        edited_last_name.setText(myUserDetails.lastName)

        edited_email.isEnabled = false
        edited_email.setText(myUserDetails.email)

        // Setting click listener for user photo
        iv_user_photo.setOnClickListener {
            imageViewClicked(view)
        }

        // Setting click listener for submit button
        button_submit.setOnClickListener {
            // Checking user details and initiating profile update
            if(checkUserDetails()){
                showProgressBar()
                if(selectedPicture != null){
                    Database().uploadImageToFireStore(this,selectedPicture!!,"User_Profile_Image")
                }
                else{
                    updateUserProfileDetails()
                }
            }
        }
    }

    // Setting up action bar with back navigation
    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Updating user profile details based on input fields
    private fun updateUserProfileDetails(){

        val userDetailsHashMap = HashMap<String, Any>()

        val phoneNumber = edited_phone_number.text.toString()
        val firstName = edited_first_name.text.toString().trim { it <= ' ' }
        if (firstName != myUserDetails.firstName) {
            userDetailsHashMap["firstName"] = firstName
        }

        val lastName = edited_last_name.text.toString().trim { it <= ' ' }
        if (lastName != myUserDetails.lastName) {
            userDetailsHashMap["lastName"] = lastName
        }

        val gender = if(rb_male.isChecked){
            "Male"
        }else{
            "Female"
        }
        if(phoneNumber.isNotEmpty() && phoneNumber != myUserDetails.phoneNumber.toString()){
            userDetailsHashMap["phoneNumber"] = phoneNumber.toLong()
        }
        userDetailsHashMap["gender"] = gender
        userDetailsHashMap["profileCompleted"] = 1
        if(userProfileImageURL.isNotEmpty()){
            userDetailsHashMap["image"] = userProfileImageURL
        }
        Database().updateProfileDetails(this,userDetailsHashMap)
    }

    // Callback for successful user details update
    fun userDetailsUpdateSuccess(){
        hideProgressBar()
        Toast.makeText(this,"Your profile has been updated successfully.",Toast.LENGTH_LONG).show()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Handling click event on user photo
    private fun imageViewClicked(view: View){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission Needed For Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    // Registering activity result launchers
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    intentFromResult.data
                    selectedPicture = intentFromResult.data
                    selectedPicture.let {
                        LoadGlide(this).loadUserPicture(selectedPicture!!,iv_user_photo)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@UserProfileActivity,"Permission Required", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Validating user input details
    private fun checkUserDetails(): Boolean {
        return when {
            TextUtils.isEmpty(edited_phone_number.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your phone number.",Toast.LENGTH_LONG).show()
                false
            }
            else -> {
                true
            }
        }
    }

    // Callback for successful image upload
    fun imageUploadSuccess(imageURL : String){
        userProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    // Showing progress bar during tasks execution
    fun showProgressBar() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.tv_progress_text.setText(R.string.please_wait)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    // Hiding progress bar after task completion
    fun hideProgressBar() {
        progressDialog.dismiss()
    }
}
