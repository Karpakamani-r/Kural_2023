package com.w2c.kural.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.w2c.kural.R

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private val mSplashHandler = Handler(Looper.getMainLooper())
    private var mSplashRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mSplashHandler.postDelayed(Runnable {
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.also { mSplashRunnable = it }, INTERVAL_TIME.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        mSplashHandler.removeCallbacks(mSplashRunnable!!)
    }

    companion object {
        private const val INTERVAL_TIME = 2000
    }
}