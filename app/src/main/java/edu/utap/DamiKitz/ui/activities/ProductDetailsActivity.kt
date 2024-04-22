package edu.utap.DamiKitz.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Cart
import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.utils.Constants
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : UiComponentsActivity(), View.OnClickListener {

    // Initialize variables
    private var myProductId: String = ""
    private var editPerm : Int = 0
    private lateinit var productModel: Product
    var productOwnerId : String = ""
    private lateinit var myProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Set up action bar
        setupActionBar()

        // Retrieve product ID and edit permission from intent extras
        if(intent.hasExtra("extra_product_id")){
            myProductId = intent.getStringExtra("extra_product_id")!!
            editPerm = intent.getIntExtra("extra_edit_perm",0)
        }

        // Retrieve product owner ID from intent extras
        if(intent.hasExtra("extra_product_owner_id")){
            productOwnerId = intent.getStringExtra("extra_product_owner_id").toString()
        }

        // Show or hide add to cart button based on user's permission
        if(Database().getUserID() == productOwnerId){
            button_add_to_cart.visibility = View.GONE
            button_go_to_cart.visibility = View.GONE
        } else {
            button_add_to_cart.visibility = View.VISIBLE
        }

        // Get product details
        getProductDetails()

        // Set click listeners
        button_add_to_cart.setOnClickListener(this)
        button_go_to_cart.setOnClickListener {
            startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }
    }

    // Inflate options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_product_menu,menu)
        if(editPerm == 0){
            // Hide edit menu if user has no permission to edit product (if it's not user's product)
            val editMenu = menu!!.findItem(R.id.edit_product_menu_item)
            editMenu.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    // Function to retrieve product details from the database
    fun getProductDetails(){
        showProgressBar(getString(R.string.please_wait))
        Database().getProductDetails(this@ProductDetailsActivity,myProductId)
    }

    // Function to handle successful retrieval of product details
    fun productDetailsSuccess(product: Product){
        myProductDetails = product
        // Load product image using Glide
        LoadGlide(this@ProductDetailsActivity).loadProductImage(product.image,iv_product_detail_image)
        // Set product details on UI
        tv_product_details_available_quantity.text = product.stockQuantity
        tv_product_details_description.text = product.description
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_title.text = product.title
        product.productId = myProductId
        productModel = product

        // If product is out of stock, hide add to cart button and display appropriate message
        if(product.stockQuantity.toInt() == 0){
            hideProgressBar()
            button_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text = getString(R.string.out_of_stock_label)
            tv_product_details_available_quantity.setTextColor(ContextCompat.getColor(this@ProductDetailsActivity,
                R.color.colorSnackBarError
            ))
        } else {
            // If user is not the product owner, check if item exists in user's cart
            if(Database().getUserID() == product.userId){
                hideProgressBar()
            } else {
                Database().checkIfItemExistsInCart(this,product.productId)
            }
        }
    }

    // Function to handle product existence in user's cart
    fun productExistsInCart(){
        hideProgressBar()
        button_add_to_cart.visibility = View.GONE
        button_go_to_cart.visibility = View.VISIBLE
    }

    // Function to set up action bar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // Function to handle options menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this@ProductDetailsActivity, AddProductActivity::class.java)
        intent.putExtra("edit",1)
        intent.putExtra("productModel",productModel)
        when (item.itemId){
            R.id.edit_product_menu_item -> startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    // Function to add product to user's cart
    private fun addToCart(){
        val cartItem = Cart(
            Database().getUserID(),
            productOwnerId,
            myProductId,
            myProductDetails.title,
            myProductDetails.price,
            myProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressBar(getString(R.string.please_wait))
        Database().addItemToCart(this,cartItem)
    }

    // Handle click events
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.button_add_to_cart -> {
                    addToCart()
                }
            }
        }
    }

    // Function to handle successful addition of product to cart
    fun addToCartSuccess(){
        hideProgressBar()
        Toast.makeText(this,getString(R.string.add_to_cart_success),Toast.LENGTH_LONG).show()
        button_add_to_cart.visibility = View.GONE
        button_go_to_cart.visibility = View.VISIBLE
    }
}
