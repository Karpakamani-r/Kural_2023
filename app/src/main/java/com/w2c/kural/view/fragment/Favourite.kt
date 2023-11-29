package com.w2c.kural.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.FragmentFavouriteBinding
import com.w2c.kural.view.activity.MainActivity
import java.util.concurrent.Executors

class Favourite : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this
        binding = FragmentFavouriteBinding.inflate(inflater)
        favFromDB()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.updateBottomNav(this)
    }

    private fun favFromDB() {
        Executors.newSingleThreadExecutor().execute {
            val kuralFav: MutableList<Kural> =
                DatabaseController.getInstance(requireActivity()).kuralDAO
                    .favKuralList
            Handler(Looper.getMainLooper()).post { setupAdapter(kuralFav) }
        }
    }


    private fun setupAdapter(kuralFav: MutableList<Kural>) {
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireActivity())
        val favAdapter = KuralAdapter(kuralFav)
        favAdapter.setFromFavourite()
        binding.rvFavourite.adapter = favAdapter
    }
}