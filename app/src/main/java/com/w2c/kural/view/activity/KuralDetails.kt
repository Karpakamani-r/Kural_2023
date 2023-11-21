package com.w2c.kural.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.ActivityKuralDetailsBinding
import com.w2c.kural.utils.IntentKeys
import java.util.concurrent.Executors

class KuralDetails : AppCompatActivity() {
    private lateinit var kuralBinding: ActivityKuralDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        removeActionBar()
        super.onCreate(savedInstanceState)
        kuralBinding = ActivityKuralDetailsBinding.inflate(LayoutInflater.from(this))
        setContentView(kuralBinding.root)
        updateUI()
    }

    private fun removeActionBar() {
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    private fun updateUI() {
        kuralBinding.toolbar.setNavigationOnClickListener { finish() }
        val kuralNumber = intent.getIntExtra(IntentKeys.KURAL_NO, 1)
        Executors.newSingleThreadExecutor().execute {
            val kural: Kural? =
                DatabaseController.getInstance(this@KuralDetails).kuralDAO
                    .getKural(kuralNumber)
            Handler(Looper.getMainLooper()).post {
                kuralBinding.kural = kural
            }
        }
    }
}