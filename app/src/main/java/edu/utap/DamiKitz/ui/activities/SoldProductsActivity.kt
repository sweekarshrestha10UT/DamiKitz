package edu.utap.DamiKitz.ui.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.SoldProductAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.SoldProduct
import kotlinx.android.synthetic.main.activity_sold_products.*

class SoldProductsActivity : UiComponentsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_products)

        // Set up action bar
        setupActionBar()

        // Fetch and display the list of sold products
        getSoldProductList()
    }

    // Function to fetch the list of sold products from the database
    private fun getSoldProductList(){
        showProgressBar(getString(R.string.please_wait))
        Database().getSoldProductsList(this)
    }

    // Function called upon successful retrieval of the list of sold products
    fun successSoldProductList(soldProductList: ArrayList<SoldProduct>){
        hideProgressBar()
        if(soldProductList.size > 0){
            // If sold products are found, display the RecyclerView and hide the "No sold products found" message
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            // Set up RecyclerView with LinearLayoutManager and adapter
            rv_sold_product_items.layoutManager = LinearLayoutManager(this)
            rv_sold_product_items.setHasFixedSize(true)
            val soldProductsAdapter = SoldProductAdapter(this, soldProductList)
            rv_sold_product_items.adapter = soldProductsAdapter
        }
        else{
            // If no sold products are found, hide the RecyclerView and display the "No sold products found" message
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE
        }
    }

    // Function to set up the action bar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_sold_products_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        // Handle click on the back button in the action bar
        toolbar_sold_products_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
