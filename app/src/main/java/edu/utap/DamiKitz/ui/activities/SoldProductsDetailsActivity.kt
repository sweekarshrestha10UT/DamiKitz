package edu.utap.DamiKitz.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.model.SoldProduct
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.activity_sold_products_details.*
import java.text.SimpleDateFormat
import java.util.*

class SoldProductsDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_products_details)

        // Set up action bar
        setupActionBar()

        // Initialize productDetails with a default SoldProduct object
        var productDetails : SoldProduct = SoldProduct()

        // Check if intent contains extra_sold_product_details and retrieve the SoldProduct object
        if(intent.hasExtra("extra_sold_product_details")){
            productDetails = intent.getParcelableExtra<SoldProduct>("extra_sold_product_details")!!
        }

        // Fill the details of the sold product on the UI
        fillDetails(productDetails)
    }

    // Function to fill details of the sold product on the UI
    private fun fillDetails(productDetails: SoldProduct) {
        // Set text for sold product ID
        tv_sold_product_details_id.text = productDetails.orderId

        // Format and set text for sold product order date
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.orderDate
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        // Load product image using Glide
        LoadGlide(this@SoldProductsDetailsActivity).loadProductImage(
            productDetails.image,
            iv_product_item_image
        )

        // Set text for sold product quantity, name, and price
        tv_sold_product_quantity.text = productDetails.quantitySold
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text = "$${productDetails.price}"

        // Set text for sold product address details
        tv_sold_details_address_type.text = productDetails.address.type
        tv_sold_details_full_name.text = productDetails.address.fullName
        tv_sold_details_address.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        tv_sold_details_delivery_instructions.text = productDetails.address.deliveryInstructions

        // Set visibility and text for additional address details
        if (productDetails.address.otherDetails.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = productDetails.address.otherDetails
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        // Set text for sold product phoneNumber number
        tv_sold_details_phone_number.text = productDetails.address.phoneNumber

        // Set text for sold product subtotal, shipping charge, and total amount
        tv_sold_product_sub_total.text = productDetails.subtotal
        tv_sold_product_shipping_charge.text = productDetails.shippingCost
        tv_sold_product_total_amount.text = productDetails.totalCost
    }

    // Function to set up the action bar
    private fun setupActionBar() {
        setSupportActionBar(sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        // Handle click on the back button in the action bar
        sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
