package edu.utap.DamiKitz.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.CartItemAdapter
import edu.utap.DamiKitz.model.Order
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        // Set up action bar
        setupActionBar()

        // Initialize Order object with default values
        var orderDetails : Order = Order()

        // Check if intent contains extra_order_details
        if(intent.hasExtra("extra_order_details")){
            // Retrieve Order object from intent extras
            orderDetails = intent.getParcelableExtra<Order>("extra_order_details")!!
        }

        // Set order details on the UI
        setOrderDetails(orderDetails)
    }

    // Function to set order details on the UI
    private fun setOrderDetails(orderDetails: Order){
        // Set order ID
        tv_order_details_id.text = orderDetails.title
        // Format and set order date
        tv_order_details_date.text = dateTimeFormatter(orderDetails)

        // Set up RecyclerView for order items list
        rv_order_items_list.layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
        rv_order_items_list.setHasFixedSize(true)

        // Set up adapter for order items RecyclerView
        val cartItemAdapter = CartItemAdapter(this@OrderDetailsActivity,orderDetails.items,false)
        rv_order_items_list.adapter = cartItemAdapter

        // Set address details
        tv_order_details_address_type.text = orderDetails.address.type
        tv_order_details_full_name.text = orderDetails.address.fullName
        tv_order_details_address.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        tv_order_details_delivery_instructions.text = orderDetails.address.deliveryInstructions

        // Set other details if available, otherwise hide the view
        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tv_order_details_other_details.visibility = View.VISIBLE
            tv_order_details_other_details.text = orderDetails.address.otherDetails
        } else {
            tv_order_details_other_details.visibility = View.GONE
        }

        // Set phone number
        tv_order_details_phone_number.text = orderDetails.address.phoneNumber

        // Set order subtotal, shipping charge, and total amount
        tv_order_details_sub_total.text = orderDetails.subtotal
        tv_order_details_shipping_charge.text = orderDetails.shippingCost
        tv_order_details_total_amount.text = orderDetails.totalCost
    }

    // Function to format order date
    private fun dateTimeFormatter(orderDetails: Order): String {
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.orderDate
        return formatter.format(calendar.time)
    }

    // Function to set up action bar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        // Set click listener for navigation button
        toolbar_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
