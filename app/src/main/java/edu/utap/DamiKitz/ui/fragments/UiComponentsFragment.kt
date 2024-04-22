package edu.utap.DamiKitz.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.utap.DamiKitz.R
import kotlinx.android.synthetic.main.progress_bar.*

// Base fragment class for UI components
open class UiComponentsFragment : Fragment() {

    // Progress dialog instance
    private lateinit var progressDialog: Dialog

    // Called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ui_components, container, false)
    }

    // Function to show progress bar with a custom message
    fun showProgressBar(text: String) {
        progressDialog = Dialog(requireActivity())
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.tv_progress_text.text = text
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    // Function to hide progress bar
    fun hideProgressBar() {
        progressDialog.dismiss()
    }
}
