package edu.utap.DamiKitz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.ui.activities.ProductDetailsActivity
import edu.utap.DamiKitz.ui.fragments.ProductFragment
import edu.utap.DamiKitz.R

import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.product_item_row.view.*

open class ProductsListAdapter(private val appContext: Context, private var productList: ArrayList<Product>, private val fragment: ProductFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(LayoutInflater.from(appContext).inflate(R.layout.product_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productList[position]

        if (holder is ProductViewHolder) {
            LoadGlide(appContext).loadProductImage(model.image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.price}"

            // Delete product action
            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteProduct(model.productId)
            }

            // Open product details activity
            holder.itemView.setOnClickListener {
                val intent = Intent(appContext, ProductDetailsActivity::class.java).apply {
                    putExtra("extra_product_id", model.productId)
                    putExtra("extra_product_owner_id", model.userId)
                    putExtra("extra_edit_perm", 1)
                }
                appContext.startActivity(intent)
            }
        }
    }
}
