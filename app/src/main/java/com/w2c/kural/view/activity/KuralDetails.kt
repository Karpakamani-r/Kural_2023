package com.w2c.kural.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.ActivityKuralDetailsBinding
import com.w2c.kural.utils.IntentKeys
import java.util.concurrent.Executors

class KuralDetails : Fragment() {
    private lateinit var kuralBinding: ActivityKuralDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kuralBinding = ActivityKuralDetailsBinding.inflate(LayoutInflater.from(requireActivity()))
        updateUI()
        return kuralBinding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateUI() {
        //kuralBinding.toolbar.setNavigationOnClickListener { finish() }
//        val kuralNumber = intent.getIntExtra(IntentKeys.KURAL_NO, 1)
//        Executors.newSingleThreadExecutor().execute {
//            val kural: Kural? =
//                DatabaseController.getInstance(requireActivity()).kuralDAO
//                    .getKural(kuralNumber)
//            Handler(Looper.getMainLooper()).post {
//                kuralBinding.kural = kural
//            }
//        }
    }
}