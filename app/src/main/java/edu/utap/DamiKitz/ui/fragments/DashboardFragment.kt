package edu.utap.DamiKitz.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.DashboardListAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.ui.activities.CartListActivity
import edu.utap.DamiKitz.ui.activities.SettingsActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*

// Fragment for displaying dashboard items
class DashboardFragment : UiComponentsFragment() {

    // Called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Indicate that the fragment has its own options menu
        setHasOptionsMenu(true)
    }

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    // Called when the fragment's activity has been created and the fragment's view hierarchy instantiated
    override fun onResume() {
        super.onResume()
        // Load dashboard list when the fragment resumes
        getDashboardList()
    }

    // Called to create the options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu resource file
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Called when an item in the options menu is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection based on its ID
        when (item.itemId) {
            R.id.action_settings -> {
                // Open settings activity
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.cartMenuButton -> {
                // Open cart activity
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Function called upon successfully retrieving the dashboard list from the database
    fun successDashboardList(dashboardList: ArrayList<Product>) {
        // Hide progress bar
        hideProgressBar()
        // Check if there are items in the dashboard list
        if (dashboardList.size > 0) {
            // Hide "no items found" message and display the RecyclerView
            tv_no_dashboard_items_found.visibility = View.GONE
            rv_dashboard_items.visibility = View.VISIBLE

            // Set up RecyclerView with a grid layout manager
            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            // Create and set adapter for the RecyclerView
            val adapterDashboard = DashboardListAdapter(requireActivity(), dashboardList)
            rv_dashboard_items.adapter = adapterDashboard
        } else {
            // Hide RecyclerView and display "no items found" message
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    // Function to fetch the dashboard list from the database
    private fun getDashboardList() {
        // Show progress bar while fetching data
        showProgressBar(getString(R.string.please_wait))

        // Call database function to get dashboard items
        Database().getItemsForDashboard(this@DashboardFragment)
    }
}
