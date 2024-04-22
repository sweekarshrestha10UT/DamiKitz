package edu.utap.DamiKitz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.OrderAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Order
import kotlinx.android.synthetic.main.fragment_orders.*

// Fragment for displaying user orders
class OrdersFragment() : UiComponentsFragment() {

    // Called when the fragment's activity has been created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Called when the fragment resumes
    override fun onResume() {
        super.onResume()
        // Load order list when the fragment resumes
        getOrderList()
    }

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    // Function called upon successfully retrieving the order list from the database
    fun showOrderList(orderList: ArrayList<Order>) {
        // Hide progress bar
        hideProgressBar()
        // Check if there are orders in the list
        if (orderList.size > 0) {
            // Hide "no orders found" message and display the RecyclerView
            rv_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            // Set up RecyclerView with a linear layout manager
            rv_order_items.layoutManager = LinearLayoutManager(activity)
            rv_order_items.setHasFixedSize(true)

            // Create and set adapter for the RecyclerView
            val orderListAdapter = OrderAdapter(requireActivity(), orderList)
            rv_order_items.adapter = orderListAdapter
        } else {
            // Hide RecyclerView and display "no orders found" message
            rv_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
    }

    // Function to fetch the user's order list from the database
    private fun getOrderList() {
        // Show progress bar while fetching data
        showProgressBar(getString(R.string.please_wait))

        // Call database function to get user's orders
        Database().getOrderList(this)
    }
}
