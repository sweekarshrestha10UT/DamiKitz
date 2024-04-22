package edu.utap.DamiKitz.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.ui.activities.CheckoutActivity
import edu.utap.DamiKitz.ui.activities.EditAddAddressActivity
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.model.Address
import edu.utap.DamiKitz.utils.Constants
import kotlinx.android.synthetic.main.address_row.view.*

// AddressAdapter is responsible for managing address data in a RecyclerView
open class AddressAdapter(
    private val appContext: Context,
    private var addressList: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Returns the number of items in the data set
    override fun getItemCount(): Int {
        return addressList.size
    }

    // Creates ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflates the layout for each item in the RecyclerView
        return AddressViewHolder(
            LayoutInflater.from(appContext).inflate(R.layout.address_row, parent, false)
        )
    }

    // Binds data to ViewHolder instances
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = addressList[position]

        if (holder is AddressViewHolder) {
            // Sets up the UI for each item in the RecyclerView
            holder.itemView.tv_address_full_name.text = model.fullName
            holder.itemView.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.itemView.tv_address_phone_number.text = model.phoneNumber
            holder.itemView.tv_address_type.text = model.type

            // Sets a click listener for selecting an address
            if (selectAddress) {
                holder.itemView.setOnClickListener {
                    val selectAddressString: String =
                        (appContext.getString(R.string.selected_address)) + "${model.address}, ${model.zipCode}"
                    // Displays a toast message when an address is selected
                    Toast.makeText(appContext, selectAddressString, Toast.LENGTH_LONG).show()
                    val intent = Intent(appContext, CheckoutActivity::class.java)
                    intent.putExtra("extra_selected_address", model)
                    appContext.startActivity(intent)
                }
            }
        }
    }

    // Notifies the adapter to edit an address item
    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(appContext, EditAddAddressActivity::class.java)
        intent.putExtra("extra_address_details", addressList[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
}