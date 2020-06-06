package com.locationtracker.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.locationtracker.BuildConfig
import com.locationtracker.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        versionNumberTv.text = getString(R.string.version_number, BuildConfig.VERSION_NAME)
        handler.postDelayed(
            {
                startActivity(MainActivity.newIntent(this))
                finish()
            },
            1500
        )
    }
}
