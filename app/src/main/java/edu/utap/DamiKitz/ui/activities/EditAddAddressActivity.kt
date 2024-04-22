package edu.utap.DamiKitz.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Address
import kotlinx.android.synthetic.main.activity_edit_add_address.*

class EditAddAddressActivity : UiComponentsActivity() {

    // Variable to hold the last address details
    private var prevAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_address)

        // Set up the action bar
        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        // Set up navigation click listener
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }

        // Check if intent contains extra address details
        if (intent.hasExtra("extra_address_details")) {
            prevAddressDetails = intent.getParcelableExtra("extra_address_details")
        }

        // Populate UI fields if editing existing address
        if (prevAddressDetails != null) {
            if (prevAddressDetails!!.id.isNotEmpty()) {
                // Update UI for editing an existing address
                tv_title.text = resources.getString(R.string.title_edit_address)
                button_submit_address.text = resources.getString(R.string.update_button_label)

                // Set text fields with existing address details
                edited_full_name.setText(prevAddressDetails?.fullName)
                edited_phone_number.setText(prevAddressDetails?.phoneNumber)
                edited_address.setText(prevAddressDetails?.address)
                edited_zip_code.setText(prevAddressDetails?.zipCode)
                edited_delivery_instructions.setText(prevAddressDetails?.deliveryInstructions)

                // Set radio button for address type
                when (prevAddressDetails?.type) {
                    "Home" -> rb_home.isChecked = true
                    "Office" -> rb_office.isChecked = true
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        edited_other_details.setText(prevAddressDetails?.otherDetails)
                    }
                }
            }
        }

        // Set click listener for submit button
        button_submit_address.setOnClickListener {
            saveAddressToDatabase()
        }

        // Set up radio group change listener to show/hide other details input
        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                til_other_details.visibility = View.VISIBLE
            } else {
                til_other_details.visibility = View.GONE
            }
        }
    }

    // Function called when saving address to database is successful
    fun updateAddressSuccess() {
        hideProgressBar()
        // Display success message based on whether the address was updated or added
        val successMessage: String = if (prevAddressDetails != null && prevAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.address_updated_successfully_message)
        } else {
            resources.getString(R.string.error_address_unsuccessful)
        }
        // Show toast message and finish activity
        Toast.makeText(this@EditAddAddressActivity, successMessage, Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
        finish()
    }

    // Function to save address details to the database
    private fun saveAddressToDatabase() {
        // Retrieve input values from UI fields
        val fullName: String = edited_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = edited_phone_number.text.toString().trim { it <= ' ' }
        val address: String = edited_address.text.toString().trim { it <= ' ' }
        val zipCode: String = edited_zip_code.text.toString().trim { it <= ' ' }
        val deliveryInstructions: String = edited_delivery_instructions.text.toString().trim { it <= ' ' }
        val otherDetails: String = edited_other_details.text.toString().trim { it <= ' ' }

        // Validate input data
        if (validateData()) {
            // Show progress bar
            showProgressBar(resources.getString(R.string.please_wait))
            // Determine address type based on selected radio button
            val addressType: String = when {
                rb_home.isChecked -> "Home"
                rb_office.isChecked -> "Office"
                else -> "Other"
            }
            // Create Address model object
            val addressModel = Address(
                Database().getUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                deliveryInstructions,
                addressType,
                otherDetails
            )
            // Update or add address based on whether an existing address is being edited
            if (prevAddressDetails != null && prevAddressDetails!!.id.isNotEmpty()) {
                Database().updateAddress(this, addressModel, prevAddressDetails!!.id)
            } else {
                Database().addAddress(this, addressModel)
            }
        }
    }

    // Function to validate input data
    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(edited_full_name.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, R.string.error_message_please_enter_full_name, Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(edited_phone_number.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, R.string.error_message_please_enter_phone_number, Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(edited_address.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, R.string.error_message_please_enter_address, Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(edited_zip_code.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, R.string.error_message_please_enter_zip_code, Toast.LENGTH_LONG).show()
                false
            }
            rb_other.isChecked && TextUtils.isEmpty(edited_other_details.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, R.string.error_message_please_enter_zip_code, Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }
}
