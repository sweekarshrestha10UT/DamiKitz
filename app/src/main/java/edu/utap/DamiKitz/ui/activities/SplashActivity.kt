package edu.utap.DamiKitz.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import edu.utap.DamiKitz.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    // Coroutine scope for managing background tasks
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the status bar for a fullscreen experience
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Execute the wait function
        runWait()
    }

    // Launch a coroutine to wait for a certain duration
    private fun runWait() {
        coroutineScope.launch {
            wait()
        }
    }

    // Suspended function to delay the splash screen for 2000 milliseconds
    private suspend fun wait() {
        delay(2000L)

        // Redirect to the LoginActivity after the delay
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
