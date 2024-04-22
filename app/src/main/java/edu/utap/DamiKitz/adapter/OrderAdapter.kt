package edu.utap.DamiKitz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.ui.activities.OrderDetailsActivity
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.model.Order
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.order_row.view.*

class OrderAdapter(private val appContext: Context, private var orderList: ArrayList<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // View holder for order items
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Get total number of items
    override fun getItemCount(): Int {
        return orderList.size
    }

    // Inflate layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(appContext).inflate(R.layout.order_row, parent, false)
        )
    }

    // Bind data to views
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val model = orderList[position]

        // Load image using Glide
        LoadGlide(appContext).loadProductImage(model.image, holder.itemView.iv_item_image)

        // Set item name and price
        holder.itemView.tv_item_name.text = model.title
        holder.itemView.tv_item_price.text = "$${model.totalCost}"

        // Hide delete button
        holder.itemView.ib_delete_product.visibility = View.GONE

        // Open order details activity on item click
        holder.itemView.setOnClickListener {
            val intent = Intent(appContext, OrderDetailsActivity::class.java)
            intent.putExtra("extra_order_details", model)
            appContext.startActivity(intent)
        }
    }
}
