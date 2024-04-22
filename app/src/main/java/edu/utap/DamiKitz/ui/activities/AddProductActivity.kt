package edu.utap.DamiKitz.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.database.Database
import edu.utap.DamiKitz.databinding.ActivityAddProductBinding
import edu.utap.DamiKitz.model.Product
import edu.utap.DamiKitz.utils.Constants
import edu.utap.DamiKitz.utils.LoadGlide
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_user_profile.view.*

class AddProductActivity : UiComponentsActivity() {

    private lateinit var binding : ActivityAddProductBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    var selectedPicture: Uri? = null
    private var edit : Int = 0
    private var productURL: String = ""
    private lateinit var productModel : Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register activity result launcher for picking images from the gallery
        registerLauncher()

        // Inflate the layout using view binding
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set up toolbar
        setSupportActionBar(add_product_button)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        // Navigate back when the back button in the toolbar is clicked
        add_product_button.setNavigationOnClickListener {
            onBackPressed()
        }

        // Handle click event for sending product details
        button_add_product.setOnClickListener {
            if(validateProductDetails()){
                uploadProductImage()
            }
        }

        // Handle click event for selecting product image
        product_photo_image_view.setOnClickListener {
            imageViewClicked()
        }

        // Check if this activity was launched for editing a product
        if(intent.hasExtra("edit")){
            edit = intent.getIntExtra("edit",0)
        }

        if(edit == 1){
            // Set appropriate text and title for the toolbar when editing a product
            button_add_product.text = getString(R.string.update_product_btn)
            add_product_button.tv_title.text = getString(R.string.edit_product_title)

            // Disable clicking on the product photo
            product_photo_image_view.isClickable = false

            // Retrieve product details to be edited
            productModel = intent.getParcelableExtra<Product>("productModel")!!
            product_title_edited.setText(productModel.title)
            product_price_edited.setText(productModel.price)
            product_description_edited.setText(productModel.description)
            product_quantity_edited.setText(productModel.stockQuantity)

            // Load product image using Glide
            LoadGlide(this).loadProductImage(productModel.image,product_photo_image_view)

            // Handle click event for updating product details
            button_add_product.setOnClickListener {
                updateProduct()
            }
        }
    }

    // Function to handle the click event of selecting an image from the gallery
    private fun imageViewClicked(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                // Show a Snackbar explaining the need for gallery permission
                Snackbar.make(this.product_price_edited.rootView,getString(R.string.permission_needed_for_gallery), Snackbar.LENGTH_INDEFINITE).setAction(getString(
                    R.string.give_permission
                )){
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // Launch gallery intent if permission is granted
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    // Function to register activity result launcher for handling permissions and gallery intent
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity,
                        R.drawable.ic_baseline_edit_24
                    ))
                    intentFromResult.data
                    selectedPicture = intentFromResult.data
                    selectedPicture.let {
                        LoadGlide(this).loadUserPicture(selectedPicture!!,product_photo_image_view)
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                // Launch gallery intent if permission is granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                // Show a toast if permission is denied
                Toast.makeText(this@AddProductActivity,getString(R.string.permission_needed), Toast.LENGTH_LONG).show()
            }
        }
    }

    // Function to validate product details
    private fun validateProductDetails(): Boolean {
        return when {
            selectedPicture == null -> {
                // Show a toast if product image is not selected
                Toast.makeText(this,resources.getString(R.string.error_message_select_product_image),Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(product_title_edited.text.toString().trim { it <= ' ' }) -> {
                // Show a toast if product title is empty
                Toast.makeText(this,resources.getString(R.string.error_message_enter_product_title),Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(product_price_edited.text.toString().trim { it <= ' ' }) -> {
                // Show a toast if product price is empty
                Toast.makeText(this,resources.getString(R.string.error_message_enter_product_price),Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(product_description_edited.text.toString().trim { it <= ' ' }) -> {
                // Show a toast if product description is empty
                Toast.makeText(this,
                    resources.getString(R.string.error_message_enter_product_description),
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            TextUtils.isEmpty(product_quantity_edited.text.toString().trim { it <= ' ' }) -> {
                // Show a toast if product quantity is empty
                Toast.makeText(this,
                    resources.getString(R.string.error_message_enter_product_quantity),
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    // Function to upload product image
    private fun uploadProductImage(){
        showProgressBar(resources.getString(R.string.please_wait))
        Database().uploadImageToFireStore(this@AddProductActivity,selectedPicture!!,"Product_Image")
    }

    // Callback function invoked when product image upload is successful
    fun imageUploadSuccess(imageURL : String){
        hideProgressBar()
        productURL = imageURL
        uploadProductDetails()
    }

    // Function to upload product details to the database
    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(
            Constants.SHOP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.CURRENT_NAME,"")!!

        val product = Product(
            Database().getUserID(),
            username,
            product_title_edited.text.toString().trim { it <= ' ' },
            product_price_edited.text.toString().trim { it <= ' ' },
            product_description_edited.text.toString().trim { it <= ' ' },
            product_quantity_edited.text.toString().trim { it <= ' ' },
            productURL
        )

        Database().uploadProductDetails(this,product)
    }

    // Callback function invoked when product details upload is successful
    fun productUploadSuccess(){
        hideProgressBar()
        Toast.makeText(this,getString(R.string.product_uploaded_successfully_message),Toast.LENGTH_LONG).show()
        finish()
    }

    // Function to update product details
    private fun updateProduct(){
        showProgressBar(getString(R.string.please_wait))
        val productList = HashMap<String,Any>()
        productList.put("description",product_description_edited.text.toString())
        productList.put("price",product_price_edited.text.toString())
        productList.put("title",product_title_edited.text.toString())
        productList.put("stockQuantity",product_quantity_edited.text.toString())

        Database().updateProduct(this,productList,productModel)
    }

    // Callback function invoked when product update is successful
    fun productUpdateSuccess(){
        hideProgressBar()
        Toast.makeText(this,getString(R.string.product_updated_successfully),Toast.LENGTH_LONG).show()
    }
}
