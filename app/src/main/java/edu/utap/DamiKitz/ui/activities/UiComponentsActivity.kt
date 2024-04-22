package edu.utap.DamiKitz.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.utap.DamiKitz.R
import kotlinx.android.synthetic.main.progress_bar.*

// Base class for activities with common UI components
open class UiComponentsActivity : AppCompatActivity() {

    // Progress dialog for displaying loading indicators
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ui_components) // Set the content view to a layout with common UI components
    }

    // Function to show the progress dialog with a custom text
    fun showProgressBar(text: String) {
        progressDialog = Dialog(this) // Create a new dialog instance
        progressDialog.setContentView(R.layout.dialog_progress) // Set the content view to a progress dialog layout
        progressDialog.tv_progress_text.text = text // Set the progress text
        progressDialog.setCancelable(false) // Disallow cancellation of the dialog by pressing back button
        progressDialog.setCanceledOnTouchOutside(false) // Disallow cancellation of the dialog by touching outside
        progressDialog.show() // Show the progress dialog
    }

    // Function to hide the progress dialog
    fun hideProgressBar() {
        progressDialog.dismiss() // Dismiss the progress dialog
    }
}
