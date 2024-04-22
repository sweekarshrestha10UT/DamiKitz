package edu.utap.DamiKitz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.ui.activities.CartListActivity
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Cart
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.cart_item_row.view.*

open class CartItemAdapter(
    private val context: Context,
    private var itemList: ArrayList<Cart>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Returns the number of items in the data set
    override fun getItemCount(): Int {
        return itemList.size
    }

    // Creates ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cart_item_row, parent, false)
        )
    }

    // Binds data to ViewHolder instances
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = itemList[position]
        setupUI(holder, model) // Set up UI elements
        setupClickListeners(holder, model) // Set up click listeners
    }

    // Set up UI elements based on item data
    private fun setupUI(holder: RecyclerView.ViewHolder, model: Cart) {
        // Load product image using Glide
        LoadGlide(context).loadProductImage(model.image, holder.itemView.iv_cart_item_image)
        // Set price, title, and quantity text
        holder.itemView.tv_cart_item_price.text = "$${model.price}"
        holder.itemView.tv_cart_item_title.text = model.title
        holder.itemView.tv_cart_quantity.text = model.cartQuantity

        // Constants for out of stock text and colors
        val outOfStockText = context.resources.getString(R.string.out_of_stock_label)
        val errorColor = ContextCompat.getColor(context, R.color.colorSnackBarError)
        val secondaryTextColor = ContextCompat.getColor(context, R.color.colorTextSecondary)

        // Update visibility and text color based on cart quantity
        if (model.cartQuantity == "0") {
            holder.itemView.ib_remove_cart_item.visibility = View.GONE
            holder.itemView.ib_add_cart_item.visibility = View.GONE
            holder.itemView.ib_delete_cart_item.visibility =
                if (updateCartItems) View.VISIBLE else View.GONE

            holder.itemView.tv_cart_quantity.text = outOfStockText
            holder.itemView.tv_cart_quantity.setTextColor(errorColor)
        } else {
            holder.itemView.ib_remove_cart_item.visibility =
                if (updateCartItems) View.VISIBLE else View.GONE
            holder.itemView.ib_add_cart_item.visibility =
                if (updateCartItems) View.VISIBLE else View.GONE
            holder.itemView.ib_delete_cart_item.visibility =
                if (updateCartItems) View.VISIBLE else View.GONE

            holder.itemView.tv_cart_quantity.setTextColor(secondaryTextColor)
        }
    }

    // Set up click listeners for buttons
    private fun setupClickListeners(holder: RecyclerView.ViewHolder, model: Cart) {
        holder.itemView.ib_delete_cart_item.setOnClickListener {
            try {
                // Show progress bar if in CartListActivity
                if (context is CartListActivity) {
                    context.showProgressBar(context.getString(R.string.please_wait))
                }
                // Remove item from cart
                Database().removeItemInCart(context, model.id)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
            }
        }

        holder.itemView.ib_remove_cart_item.setOnClickListener {
            try {
                // Decrease quantity or remove item if quantity is 1
                if (model.cartQuantity == "1") {
                    Database().removeItemInCart(context, model.id)
                } else {
                    val cartQuantity: Int = model.cartQuantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap["cartQuantity"] = (cartQuantity - 1).toString()
                    if (context is CartListActivity) {
                        context.showProgressBar(context.getString(R.string.please_wait))
                    }
                    Database().updateCartList(context, model.id, itemHashMap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
            }
        }

        holder.itemView.ib_add_cart_item.setOnClickListener {
            try {
                // Increase quantity if below stock quantity
                val cartQuantity: Int = model.cartQuantity.toInt()
                if (cartQuantity < model.stockQuantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap["cartQuantity"] = (cartQuantity + 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressBar(context.getString(R.string.please_wait))
                    }
                    Database().updateCartList(context, model.id, itemHashMap)
                } else {
                    // Show toast if quantity exceeds stock quantity
                    if (context is CartListActivity) {
                        val stringForStock: String = context.getString(
                            R.string.message_for_available_stock, model.stockQuantity
                        )
                        Toast.makeText(context, stringForStock, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
