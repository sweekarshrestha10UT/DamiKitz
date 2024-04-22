package edu.utap.DamiKitz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.ui.activities.ProductDetailsActivity
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.dashboard_item_row.view.*

// Adapter for displaying products in the dashboard
open class DashboardListAdapter(
    private val context: Context, private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Returns the number of items in the data set
    override fun getItemCount(): Int {
        return list.size
    }

    // Creates ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DashboardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.dashboard_item_row, parent, false)
        )
    }

    // Binds data to ViewHolder instances
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is DashboardViewHolder) {
            // Loads product image using Glide
            LoadGlide(context).loadProductImage(
                model.image, holder.itemView.iv_dashboard_item_image
            )
            // Sets product title and price
            holder.itemView.tv_dashboard_item_title.text = model.title
            holder.itemView.tv_dashboard_item_price.text = "$${model.price}"
            // Sets a click listener to view product details
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, ProductDetailsActivity::class.java)
                // Passes product ID and owner ID to the details activity
                intent.putExtra("extra_product_id", model.productId)
                intent.putExtra("extra_product_owner_id", model.userId)
                context.startActivity(intent)
            }
        }
    }
}
