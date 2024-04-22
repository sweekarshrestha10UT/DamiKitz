package edu.utap.DamiKitz.database

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.utap.DamiKitz.model.*
import edu.utap.DamiKitz.ui.activities.*
import edu.utap.DamiKitz.ui.fragments.DashboardFragment
import edu.utap.DamiKitz.ui.fragments.OrdersFragment
import edu.utap.DamiKitz.ui.fragments.ProductFragment
import edu.utap.DamiKitz.utils.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*
/**
 * This class handles interactions with the Firestore database.
 */
class Database {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Registers a new user.
     * @param activity The RegisterActivity instance.
     * @param userInfo The user information to register.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        db.collection("users").document(userInfo.id).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "An error occurred while registering the user.",
                    e
                )
            }
    }

    /**
     * Retrieves details of the current user.
     * @param activity The current Activity instance.
     */
    fun getCurrentUserDetails(activity: Activity) {
        db.collection("users").document(getUserID()).get().addOnSuccessListener { document ->
            Log.i(javaClass.simpleName, document.toString())
            val user = document.toObject(User::class.java)

            val sharedPreferences =
                activity.getSharedPreferences(Constants.SHOP_PREFERENCES, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(Constants.CURRENT_NAME, user!!.firstName).apply()
            sharedPreferences.edit().putString(Constants.CURRENT_SURNAME, user.lastName).apply()

            when (activity) {
                is LoginActivity -> {
                    activity.userLoggedInSuccess(user)
                }
            }
            when (activity) {
                is UserProfileActivity -> {
                    activity.edited_email.setText(user.email)
                    activity.edited_first_name.setText(user.firstName)
                    activity.edited_last_name.setText(user.lastName)
                }

                is SettingsActivity -> {
                    activity.userDetailsRetrievedSuccess(user)
                }

            }
        }
    }

    /**
     * Retrieves the current user ID.
     * @return The current user ID.
     */
    fun getUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID

    }

    /**
     * Updates the profile details of the current user.
     * @param activity The current Activity instance.
     * @param userDetailsHashmap A hashmap containing the user details to update.
     */
    fun updateProfileDetails(activity: Activity, userDetailsHashmap: HashMap<String, Any>) {
        db.collection("users").document(getUserID()).update(userDetailsHashmap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userDetailsUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressBar()
                    }

                    is SettingsActivity -> {
                        activity.hideProgressBar()
                    }

                }
                Log.e("Database", "An error occurred while updating the user data.", it)
            }
    }

    /**
     * Uploads an image to the Firebase Storage.
     * @param activity The current Activity instance.
     * @param imageUri The URI of the image to upload.
     * @param imageType The type of the image.
     */
    fun uploadImageToFireStore(activity: Activity, imageUri: Uri, imageType: String) {
        val storage: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExt(
                activity, imageUri
            )
        )
        storage.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())

                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                        Log.e("Firebase", "Firebase Image uploaded successfully.")
                    }

                    is AddProductActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }

            }
        }.addOnFailureListener { exception ->
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressBar()
                }

                is AddProductActivity -> {
                    activity.hideProgressBar()
                }
            }
            Log.e(
                activity.javaClass.simpleName, exception.message, exception
            )
        }
    }

    /**
     * Retrieves the list of products from Firestore.
     * @param fragment The current Fragment instance.
     */
    fun getProductList(fragment: Fragment) {
        db.collection("products").whereEqualTo("userId", getUserID()).get()
            .addOnSuccessListener { document ->
                val productList: ArrayList<Product> = ArrayList()
                for (p in document.documents) {
                    val product = p.toObject(Product::class.java)
                    product!!.productId = p.id
                    productList.add(product)
                }
                when (fragment) {
                    is ProductFragment -> {
                        fragment.successProductListFS(productList)
                    }
                }
            }
    }

    /**
     * Uploads product details to Firestore.
     * @param activity The current Activity instance.
     * @param productInfo The product information to upload.
     */
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        db.collection("products").document().set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }.addOnFailureListener {
                activity.hideProgressBar()
                Toast.makeText(activity, "Error!", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Retrieves items for the dashboard from Firestore.
     * @param fragment The current DashboardFragment instance.
     */
    fun getItemsForDashboard(fragment: DashboardFragment) {
        db.collection("products").get().addOnSuccessListener { document ->
            Log.e("Dashboard Items", document.documents.toString())
            val productList: ArrayList<Product> = ArrayList()
            for (p in document.documents) {
                val product = p.toObject(Product::class.java)
                product!!.productId = p.id
                productList.add(product)
            }
            fragment.successDashboardList(productList)
        }.addOnFailureListener {
            fragment.hideProgressBar()
            Log.e("Database", "Error getting dashboard items.", it)
        }
    }

    /**
     * Deletes a product from Firestore.
     * @param fragment The current ProductFragment instance.
     * @param productId The ID of the product to delete.
     */
    fun deleteProduct(fragment: ProductFragment, productId: String) {
        db.collection("products").document(productId).delete().addOnSuccessListener {
            fragment.productDeleteSuccess()
        }.addOnFailureListener {
            Log.e("Database", "Error deleting product: $productId", it)
            fragment.hideProgressBar()
        }
    }

    /**
     * Retrieves product details from Firestore.
     * @param activity The current ProductDetailsActivity instance.
     * @param productId The ID of the product to retrieve details for.
     */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        db.collection("products").document(productId).get().addOnSuccessListener { document ->
            val product = document.toObject(Product::class.java)
            activity.productDetailsSuccess(product!!)
        }.addOnFailureListener {
            Log.e("Database", "Error getting product details for: $productId", it)
            activity.hideProgressBar()
        }
    }

    /**
     * Updates a product in Firestore.
     * @param activity The current AddProductActivity instance.
     * @param productList The hashmap containing the product details to update.
     * @param product The product to update.
     */
    fun updateProduct(
        activity: AddProductActivity, productList: HashMap<String, Any>, product: Product
    ) {
        db.collection("products").document(product.productId).update(productList)
            .addOnSuccessListener {
                activity.productUpdateSuccess()
            }.addOnFailureListener {
                activity.hideProgressBar()
                Log.e("Database", "Error updating product: " + product.productId, it)
            }
    }

    /**
     * Adds an item to the user's cart.
     * @param activity The current ProductDetailsActivity instance.
     * @param addToCart The item to add to the cart.
     */
    fun addItemToCart(activity: ProductDetailsActivity, addToCart: Cart) {

        db.collection("cart_items").document().set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }.addOnFailureListener {
                Log.e("Database", "Error while creating the cart doc.", it)
            }
    }

    /**
     * Checks if an item exists in the user's cart.
     * @param activity The current ProductDetailsActivity instance.
     * @param productId The ID of the product to check for in the cart.
     */
    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {

        db.collection("cart_items").whereEqualTo("userId", getUserID())
            .whereEqualTo("productId", productId).get().addOnSuccessListener { document ->
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressBar()
                }
            }.addOnFailureListener {
                activity.hideProgressBar()
                Log.e("Database", "Error while checking if item exists in cart.", it)
            }
    }

    /**
     * Retrieves the list of all products from Firestore.
     * @param activity The current Activity instance.
     */
    fun getAllProductsList(activity: Activity) {
        db.collection("products").get().addOnSuccessListener { document ->
            val productsList: ArrayList<Product> = ArrayList()
            for (i in document.documents) {

                val product = i.toObject(Product::class.java)
                product!!.productId = i.id

                productsList.add(product)
            }
            when (activity) {
                is CartListActivity -> {
                    activity.successListOfProductFromFireStore(productsList)
                }

                is CheckoutActivity -> {
                    activity.successListOfProductFromFireStore(productsList)
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is CartListActivity -> {
                    activity.hideProgressBar()
                }


                is CheckoutActivity -> {
                    activity.hideProgressBar()
                }
            }
            Log.e("Database", "Error while getting all products list.", e)
        }
    }


    /**
     * Creates a new order in Firestore.
     * @param activity The current CheckoutActivity instance.
     * @param order The order information to be created.
     */
    fun createOrder(activity: CheckoutActivity, order: Order) {
        db.collection("orders").document().set(order, SetOptions.merge()).addOnSuccessListener {
            activity.orderCreatedSuccess()
        }.addOnFailureListener {
            activity.hideProgressBar()
            Log.e("Database", "Error while creating order.", it)
        }
    }

    /**
     * Retrieves the list of items in the user's cart from Firestore.
     * @param activity The current Activity instance.
     */
    fun getCartList(activity: Activity) {
        db.collection("cart_items").whereEqualTo("userId", getUserID()).get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Cart> = ArrayList()
                for (cart in document.documents) {
                    val cartItem = cart.toObject(Cart::class.java)
                    cartItem!!.id = cart.id
                    list.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.cartFetchSuccess(list)
                    }

                    is CheckoutActivity -> {
                        activity.cartFetchSuccess(list)
                    }
                }
            }.addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressBar()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressBar()
                    }
                }
                Log.e("Database", "Error while getting cart list.", it)
            }
    }

    /**
     * Updates the details of a product in the Firestore database.
     * @param context The context from which the function is called.
     * @param cartId The ID of the cart item to update.
     * @param itemHashMap The hashmap containing the updated item details.
     */
    fun updateCartList(context: Context, cartId: String, itemHashMap: HashMap<String, Any>) {
        db.collection("cart_items").document(cartId).update(itemHashMap).addOnSuccessListener {
            when (context) {
                is CartListActivity -> {
                    context.itemUpdateSuccess()
                }
            }
        }.addOnFailureListener {
            when (context) {
                is CartListActivity -> {
                    context.hideProgressBar()
                }
            }
            Log.e("Database", "Error while updating cart list.", it)
        }

    }

    /**
     * Removes an item from the user's cart in Firestore.
     * @param context The context from which the function is called.
     * @param cartId The ID of the cart item to remove.
     */
    fun removeItemInCart(context: Context, cartId: String) {
        db.collection("cart_items").document(cartId).delete().addOnSuccessListener {
            when (context) {
                is CartListActivity -> {
                    context.itemRemovedSuccess()
                }
            }
        }.addOnFailureListener {
            when (context) {
                is CartListActivity -> {
                    context.hideProgressBar()
                }
            }
            Log.e("Database", "Error while removing cart item.", it)
        }
    }

    /**
     * Updates the details of products in the user's cart and moves them to the sold_products collection.
     * @param activity The current CheckoutActivity instance.
     * @param cartList The list of items in the user's cart.
     * @param order The order information.
     */
    fun updateProductCartDetails(
        activity: CheckoutActivity, cartList: ArrayList<Cart>, order: Order
    ) {
        val write = db.batch()

        for (cart in cartList) {

            val soldProduct = SoldProduct(
                cart.productOwnerId,
                cart.title,
                cart.price,
                cart.cartQuantity,
                cart.image,
                order.title,
                order.orderDate,
                order.subtotal,
                order.shippingCost,
                order.totalCost,
                order.address
            )

            val documentReference = db.collection("sold_products").document(cart.productId)

            write.set(documentReference, soldProduct)
        }

        for (cart in cartList) {

            val documentReference = db.collection("cart_items").document(cart.id)
            write.delete(documentReference)
        }

        write.commit().addOnSuccessListener {

            activity.cartDetailsUpdatedSuccessfully()

        }.addOnFailureListener {
            activity.hideProgressBar()
            Log.e("Database", "Error while updating product cart details.", it)
        }

    }

    /**
     * Retrieves the list of orders associated with the current user from Firestore.
     * @param fragment The current OrdersFragment instance.
     */
    fun getOrderList(fragment: OrdersFragment) {
        db.collection("orders").whereEqualTo("userId", getUserID()).get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()
                for (i in document.documents) {
                    val orderItem = i.toObject(Order::class.java)
                    orderItem!!.id = i.id
                    list.add(orderItem)
                }
                fragment.showOrderList(list)

            }.addOnFailureListener {
                fragment.hideProgressBar()
                Log.e("Database", "Error while getting order list.", it)
            }

    }

    /**
     * Retrieves the list of sold products associated with the current user from Firestore.
     * @param activity The current SoldProductsActivity instance.
     */
    fun getSoldProductsList(activity: SoldProductsActivity) {
        db.collection("sold_products").whereEqualTo("userId", getUserID()).get()
            .addOnSuccessListener { document ->
                val list: ArrayList<SoldProduct> = ArrayList()
                for (i in document.documents) {
                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    list.add(soldProduct)
                }
                activity.successSoldProductList(list)

            }.addOnFailureListener {
                it.printStackTrace()
                activity.hideProgressBar()
                Log.e("Database", "Error while getting sold products", it)
            }
    }

    /**
     * Adds a new address to the database.
     * @param activity The current EditAddAddressActivity instance.
     * @param addressInfo The address information to add.
     */
    fun addAddress(activity: EditAddAddressActivity, addressInfo: Address) {
        db.collection("addresses").document().set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.updateAddressSuccess()
            }.addOnFailureListener {
                activity.hideProgressBar()
                Log.e("Database", "Error while adding address.", it)
            }
    }

    /**
     * Retrieves the list of addresses associated with the current user from Firestore.
     * @param activity The current AddressActivity instance.
     */
    fun getAddresses(activity: AddressActivity) {
        db.collection("addresses").whereEqualTo("userId", getUserID()).get()
            .addOnSuccessListener { document ->
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)
                    address!!.id = i.id
                    addressList.add(address)
                }
                activity.addressesFetchSuccess(addressList)

            }.addOnFailureListener {
                activity.hideProgressBar()
                Log.e("Database", "Error while getting addresses.", it)
            }
    }

    /**
     * Updates an existing address in Firestore.
     * @param activity The current EditAddAddressActivity instance.
     * @param addressInfo The updated address information.
     * @param addressId The ID of the address to update.
     */
    fun updateAddress(activity: EditAddAddressActivity, addressInfo: Address, addressId: String) {
        db.collection("addresses").document(addressId).set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.updateAddressSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressBar()
                Log.e(activity.javaClass.simpleName, "Error while updating the Address.", e)
            }
    }

    /**
     * Deletes an existing address in Firestore.
     * @param activity The current AddressActivity instance.
     * @param addressId The ID of the address to delete.
     */
    fun deleteAddress(activity: AddressActivity, addressId: String) {
        db.collection("addresses")
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccessful()
            }
            .addOnFailureListener {
                activity.hideProgressBar()
            }
    }
}
