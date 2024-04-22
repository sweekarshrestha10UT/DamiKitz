package edu.utap.DamiKitz.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.CartItemAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Address
import edu.utap.DamiKitz.model.Cart
import edu.utap.DamiKitz.model.Order
import edu.utap.DamiKitz.model.Product
import kotlinx.android.synthetic.main.activity_checkout.*
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : UiComponentsActivity() {
    private lateinit var prevProductList: ArrayList<Product>
    private lateinit var prevCartItemList: ArrayList<Cart>
    private var prevSubTotal: Double = 0.0
    private var prevTotal: Double = 0.0
    private var prevAddressDetails: Address? = null
    private lateinit var orderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()
        getProductList()

        // Handle click event for place order button
        button_place_order.setOnClickListener {
            placeOrder()
        }

        // Retrieve selected address details from intent if available
        if(intent.hasExtra("extra_selected_address")){
            prevAddressDetails = intent.getParcelableExtra<Address>("extra_selected_address")
        }

        // Populate UI with selected address details
        if(prevAddressDetails != null){
            tv_checkout_address_type.text = prevAddressDetails?.type
            tv_checkout_full_name.text = prevAddressDetails?.fullName
            tv_checkout_address.text = "${prevAddressDetails!!.address}, ${prevAddressDetails!!.zipCode}"
            tv_checkout_delivery_instructions.text = prevAddressDetails?.deliveryInstructions

            if (prevAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = prevAddressDetails?.otherDetails
            }
            tv_checkout_phone_number.text = prevAddressDetails?.phoneNumber
        }
    }

    // Function to place order
    private fun placeOrder(){
        showProgressBar(getString(R.string.please_wait))
        val uuid = UUID.randomUUID()
        orderDetails = Order(
            Database().getUserID(),
            prevCartItemList,
            prevAddressDetails!!,
            "Order-$uuid",
            prevCartItemList[0].image,
            prevSubTotal.toString(),
            "15.00",
            prevTotal.toString(),
            System.currentTimeMillis()
        )
        Database().createOrder(this,orderDetails)
    }

    // Callback function invoked when order creation is successful
    fun orderCreatedSuccess(){
        Database().updateProductCartDetails(this,prevCartItemList,orderDetails)
    }

    // Callback function invoked when cart details are updated successfully
    fun cartDetailsUpdatedSuccessfully(){
        hideProgressBar()
        Toast.makeText(this,getString(R.string.your_order_has_placed_success),Toast.LENGTH_LONG).show()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Function to fetch product list from Firebase Firestore
    private fun getProductList(){
        showProgressBar(resources.getString(R.string.please_wait))
        Database().getAllProductsList(this@CheckoutActivity)
    }

    // Callback function invoked when product list is fetched successfully from Firebase Firestore
    fun successListOfProductFromFireStore(productList: ArrayList<Product>){
        prevProductList = productList
        getCartItemList()
    }

    // Function to fetch cart item list from Firebase Firestore
    private fun getCartItemList(){
        Database().getCartList(this@CheckoutActivity)
    }

    // Callback function invoked when cart item list is fetched successfully from Firebase Firestore
    fun cartFetchSuccess(cartList: ArrayList<Cart>){
        hideProgressBar()
        prevCartItemList = cartList

        // Update cart item list with latest product details
        for (product in prevProductList) {
            for (cart in cartList) {
                if (product.productId == cart.productId) {
                    cart.stockQuantity = product.stockQuantity
                }
            }
        }

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemAdapter(this@CheckoutActivity,prevCartItemList,false)
        rv_cart_list_items.adapter = cartListAdapter

        // Calculate subtotal
        for(item in prevCartItemList){
            val availableStock = item.stockQuantity.toInt()
            if(availableStock>0){
                val price = item.price.toDouble()
                val quantity = item.cartQuantity.toInt()
                prevSubTotal += (price*quantity)
            }
        }

        // Display subtotal, shipping charge, and total amount
        tv_checkout_sub_total.text = "$" + String.format("%.2f", prevSubTotal)
        tv_checkout_shipping_charge.text = "$15.00"
        if (prevSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE
            prevTotal = prevSubTotal + 15.0
            tv_checkout_total_amount.text = "$" +  String.format("%.2f", prevTotal)
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }

    }

    // Set up action bar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
