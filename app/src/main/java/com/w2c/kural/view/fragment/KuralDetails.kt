package com.w2c.kural.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import com.w2c.kural.databinding.FragmentKuralDetailsBinding

class KuralDetails : Fragment() {
    private lateinit var kuralBinding: FragmentKuralDetailsBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var kural: Kural

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kuralBinding = FragmentKuralDetailsBinding.inflate(LayoutInflater.from(requireActivity()))
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        updateUI()
        setUpFavoriteClickObserver()
        return kuralBinding.root
    }

    private fun updateUI() {
        val args: KuralDetailsArgs by navArgs<KuralDetailsArgs>()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getKuralDetail(requireActivity(), args.number)
                .observe(viewLifecycleOwner) { it ->
                    kural = it
                    kuralBinding.kural = kural
                    viewModel.updateFavToolBarIcon(kural.favourite == 1)
                }
        }
    }

    private fun setUpFavoriteClickObserver() {
        viewModel.favClickLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (::kural.isInitialized) {
                    kural.apply {
                        favourite = if (favourite == 0) 1 else 0
                    }
                    viewModel.manageFavorite(requireActivity(), kural)
                }
            }
        }
    }
}