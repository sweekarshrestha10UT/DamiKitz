package edu.utap.DamiKitz.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.ProductsListAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.ui.activities.AddProductActivity
import edu.utap.DamiKitz.ui.activities.SoldProductsActivity
import kotlinx.android.synthetic.main.fragment_products.*

// Fragment for displaying user's products
class ProductFragment : UiComponentsFragment() {

    // Called when the fragment's activity has been created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Indicate that this fragment has menu items to contribute
        setHasOptionsMenu(true)
    }

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    // Called when the fragment resumes
    override fun onResume() {
        super.onResume()
        // Load product list when the fragment resumes
        getProductList()
    }

    // Called to create options menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu resource file
        inflater.inflate(R.menu.add_product_top_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Called when a menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Get the ID of the selected menu item
        val id = item.itemId
        when (id) {
            R.id.add_product -> {
                // Open AddProductActivity when "Add Product" is selected
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }

            R.id.sold_products -> {
                // Open SoldProductsActivity when "Sold Products" is selected
                startActivity(Intent(activity, SoldProductsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Function called upon successfully retrieving the product list from the database
    fun successProductListFS(productList: ArrayList<Product>) {
        // Hide progress bar
        hideProgressBar()
        // Check if there are products in the list
        if (productList.size > 0) {
            // Hide "no products found" message and display the RecyclerView
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            // Set up RecyclerView with a linear layout manager
            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)

            // Create and set adapter for the RecyclerView
            val adapterProducts = ProductsListAdapter(requireActivity(), productList, this)
            rv_my_product_items.adapter = adapterProducts
        } else {
            // Hide RecyclerView and display "no products found" message
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    // Function to fetch the user's product list from the database
    fun getProductList() {
        // Show progress bar while fetching data
        showProgressBar(resources.getString(R.string.please_wait))

        // Call database function to get user's products
        Database().getProductList(this)
    }

    // Function to delete a product
    fun deleteProduct(productId: String) {
        // Create an alert dialog to confirm product deletion
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.delete_product_title))
        builder.setMessage(getString(R.string.are_you_sure_want_to_delete_product))
        builder.setIcon(R.drawable.ic_baseline_warning_24)

        // Positive button to confirm deletion
        builder.setPositiveButton(getString(R.string.yes)) { d, _ ->
            // Show progress bar while deleting product
            showProgressBar(getString(R.string.please_wait))

            // Call database function to delete product
            Database().deleteProduct(this, productId)

            // Dismiss the dialog
            d.dismiss()
        }
        // Negative button to cancel deletion
        builder.setNegativeButton(getString(R.string.no)) { d, _ ->
            // Dismiss the dialog
            d.dismiss()
        }
        // Create and display the dialog
        val alert: AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    // Function called upon successful deletion of a product
    fun productDeleteSuccess() {
        // Hide progress bar
        hideProgressBar()

        // Refresh the product list
        getProductList()
    }
}
