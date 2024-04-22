package edu.utap.DamiKitz.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.adapter.AddressAdapter
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.model.Address
import edu.utap.DamiKitz.utils.Constants
import edu.utap.DamiKitz.utils.SwipeToDelete
import edu.utap.DamiKitz.utils.SwipeToEdit
import kotlinx.android.synthetic.main.activity_address.*

class AddressActivity : UiComponentsActivity() {
    private var deliveryAddressSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        // Set up toolbar
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        // Navigate back when the back button in the toolbar is clicked
        toolbar_address_list_activity.setNavigationOnClickListener {onBackPressed()}

        // Handle click event for adding a new address
        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressActivity, EditAddAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        // Check if the activity was launched for selecting an address
        if(intent.hasExtra("extra_select_address")){
            deliveryAddressSelected = intent.getBooleanExtra("extra_select_address",false)
        }

        // Change title if the activity is for selecting an address
        if(deliveryAddressSelected){
            tv_title.text = getString(R.string.title_select_address)
        }

        // Fetch address list from database
        getAddressList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Refresh address list if an address is added or edited
        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    // Callback function invoked when address list is fetched successfully from the database
    fun addressesFetchSuccess(addressList: ArrayList<Address>){
        hideProgressBar()

        if(addressList.size > 0){
            // Show address list if addresses are available
            tv_no_address_found.visibility = View.GONE
            rv_address_list.visibility = View.VISIBLE

            // Set up RecyclerView with AddressAdapter
            rv_address_list.layoutManager = LinearLayoutManager(this@AddressActivity)
            rv_address_list.setHasFixedSize(true)
            val addressAdapter = AddressAdapter(this,addressList,deliveryAddressSelected)
            rv_address_list.adapter = addressAdapter

            // Set up swipe gestures for editing and deleting addresses
            if(!deliveryAddressSelected){
                val editSwipeHandler = object: SwipeToEdit(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressAdapter
                        adapter.notifyEditItem(this@AddressActivity,viewHolder.adapterPosition)
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val deleteSwipeHandler = object : SwipeToDelete(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressBar(getString(R.string.please_wait))

                        // Delete the selected address
                        Database().deleteAddress(this@AddressActivity,addressList[viewHolder.adapterPosition].id)
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }

        }else{
            // Show message if no addresses are found
            tv_no_address_found.visibility = View.VISIBLE
        }

    }

    // Function to fetch address list from the database
    private fun getAddressList(){
        showProgressBar(getString(R.string.please_wait))
        Database().getAddresses(this)
    }

    // Callback function invoked when address deletion is successful
    fun deleteAddressSuccessful(){
        hideProgressBar()
        // Show a toast message indicating successful deletion
        Toast.makeText(this, R.string.error_address_deleted_unsuccessful,Toast.LENGTH_LONG).show()
        // Refresh the address list
        getAddressList()
    }
}
