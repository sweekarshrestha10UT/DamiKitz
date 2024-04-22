package edu.utap.DamiKitz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.ui.activities.SoldProductsDetailsActivity
import edu.utap.DamiKitz.model.SoldProduct
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.order_row.view.*

// Adapter for displaying sold products in a RecyclerView
class SoldProductAdapter(private val appContext: Context, private var productList: ArrayList<SoldProduct>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder for displaying sold product items
    class SoldProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Create View Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflate the layout for the item row
        val view = LayoutInflater.from(appContext).inflate(R.layout.order_row, parent, false)
        return SoldProductViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get the sold product at the specified position
        val soldProduct = productList[position]

        if (holder is SoldProductViewHolder) {
            // Load product image using Glide
            LoadGlide(appContext).loadProductImage(soldProduct.image, holder.itemView.iv_item_image)

            // Set product name and price
            holder.itemView.tv_item_name.text = soldProduct.title
            holder.itemView.tv_item_price.text = "$${soldProduct.price}"

            // Hide delete button (if any)
            holder.itemView.ib_delete_product.visibility = View.GONE

            // Set click listener to view sold product details
            holder.itemView.setOnClickListener {
                val intent = Intent(appContext, SoldProductsDetailsActivity::class.java)
                // Pass sold product details to the details activity
                intent.putExtra("extra_sold_product_details", soldProduct)
                appContext.startActivity(intent)
            }
        }
    }

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return productList.size
    }
}
