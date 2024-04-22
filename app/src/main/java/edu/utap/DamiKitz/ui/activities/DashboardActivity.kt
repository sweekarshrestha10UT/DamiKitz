package edu.utap.DamiKitz.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.utap.DamiKitz.R
import edu.utap.DamiKitz.databinding.ActivityDashboardBinding
import edu.utap.DamiKitz.ui.fragments.DashboardFragment
import edu.utap.DamiKitz.ui.fragments.OrdersFragment
import edu.utap.DamiKitz.ui.fragments.ProductFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set action bar background
        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )

        // Replace initial fragment with DashboardFragment
        replaceFragment(DashboardFragment())
        // Set action bar title
        supportActionBar!!.title = getString(R.string.explore_buy_title)

        // Handle bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> replaceFragment(DashboardFragment())
                R.id.orders -> replaceFragment(OrdersFragment())
                R.id.products -> replaceFragment(ProductFragment())
                else -> {
                }
            }

            // Set action bar title based on selected bottom navigation item
            when (it.itemId) {
                R.id.dashboard -> supportActionBar!!.title = getString(R.string.explore_buy_title)
                R.id.orders -> supportActionBar!!.title = getString(R.string.orders_title)
                R.id.products -> supportActionBar!!.title = getString(R.string.sell_product_title)
            }

            true
        }
    }

    // Function to replace current fragment with a new fragment
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}
