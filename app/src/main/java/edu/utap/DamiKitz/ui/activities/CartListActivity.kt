package edu.utap.DamiKitz.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.CartItemAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Cart
import edu.utap.DamiKitz.model.Product
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : UiComponentsActivity() {
    private lateinit var prevProductsList: ArrayList<Product>
    private lateinit var prevCartItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        // Handle click event for checkout button
        button_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressActivity::class.java)
            intent.putExtra("extra_select_address", true)
            startActivity(intent)
        }
    }

    // Callback function invoked when cart item list is fetched successfully from the database
    fun cartFetchSuccess(cartList: ArrayList<Cart>) {
        hideProgressBar()

        // Update cart items with latest product details
        for (product in prevProductsList) {
            for (cart in cartList) {
                if (product.productId == cart.productId) {
                    cart.stockQuantity = product.stockQuantity
                    if (product.stockQuantity.toInt() == 0) {
                        cart.cartQuantity = product.stockQuantity
                    }
                }
            }
        }

        prevCartItems = cartList

        if (prevCartItems.size > 0) {

            // Show cart items if available
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            // Set up RecyclerView with CartItemAdapter
            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemAdapter(this@CartListActivity, prevCartItems, true)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal: Double = 0.0

            // Calculate subtotal and total amount
            for (item in prevCartItems) {
                val availableStock = item.stockQuantity.toInt()

                if (availableStock > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cartQuantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            // Format subtotal and display
            tv_sub_total.text = "$" + String.format("%.2f", subTotal)

            tv_shipping_charge.text = "$15.00"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 15
                // Format total amount and display
                tv_total_amount.text = "$" + String.format("%.2f", total)
            } else {
                ll_checkout.visibility = View.GONE
            }


        } else {
            // Show message if no cart items found
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    // Function to fetch cart items list from the database
    private fun getCartItemsList() {
        Database().getCartList(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()
        // Fetch products list from Firebase
        getProductsFromFireBase()
    }

    // Set up action bar
    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // Fetch products list from Firebase
    private fun getProductsFromFireBase() {
        showProgressBar(getString(R.string.please_wait))
        Database().getAllProductsList(this)
    }

    // Callback function invoked when products list is fetched successfully from Firebase
    fun successListOfProductFromFireStore(allProductList: ArrayList<Product>) {
        hideProgressBar()
        prevProductsList = allProductList
        // Get the cart items list after fetching products list
        getCartItemsList()
    }

    // Callback function invoked when an item is successfully removed from the cart
    fun itemRemovedSuccess() {
        hideProgressBar()
        Toast.makeText(this, R.string.message_item_removed_successfully, Toast.LENGTH_LONG).show()

        // Refresh cart items list
        getCartItemsList()
    }

    // Callback function invoked when an item is successfully updated in the cart
    fun itemUpdateSuccess() {
        hideProgressBar()
        // Refresh cart items list
        getCartItemsList()
    }
}
