package com.w2c.kural.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.FragmentFavouriteBinding
import com.w2c.kural.utils.OnItemClickListener
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.w2c.kural.R
import com.w2c.kural.utils.AdapterActions

class Favourites : Fragment() {
    private var binding_: FragmentFavouriteBinding? = null
    private val binding get() = binding_!!

    private lateinit var viewModel: MainActivityViewModel
    private var favAdapter: KuralAdapter? = null
    private var favourites: MutableList<Kural> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this
        binding_ = FragmentFavouriteBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        fetchFavorites()
        return binding.root
    }

    private fun fetchFavorites() {
        setupAdapter()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getFavoriteKurals(requireActivity()).observe(viewLifecycleOwner) {
                if (favourites.isNotEmpty()) favourites.clear()
                favourites.addAll(it)
                favAdapter?.notifyDataSetChanged()
                manageKuralEmptyView(favourites.isEmpty())
            }
        }
    }

    private fun manageKuralEmptyView(empty: Boolean) {
        if (empty) {
            binding.tvNoFavorites.visible()
            binding.rvFavourite.hide()
        } else {
            binding.rvFavourite.visible()
            binding.tvNoFavorites.hide()
        }
    }

    private fun setupAdapter() {
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireActivity())
        favAdapter = KuralAdapter(favourites, getCallBack)
        favAdapter?.setFromFavourite()
        binding.rvFavourite.adapter = favAdapter
    }

    private val getCallBack = { pos: Int, action: AdapterActions ->
        if(action==AdapterActions.ITEM_CLICK) onItemClick(pos)
        else onManageFavorite(pos)
    }

    private fun onItemClick(position: Int) {
        val kuralNumber = favourites[position].number
        val kuralDetailDirection = FavouritesDirections.actionFavouriteToKuralDetails(kuralNumber)
        findNavController().navigate(kuralDetailDirection)
    }

    private fun onManageFavorite(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val kural = favourites[position].apply {
                favourite = 0
            }
            favAdapter?.notifyItemChanged(position, kural)
            viewModel.manageFavorite(requireActivity(), kural)
        }
    }

    override fun onDestroyView() {
        binding.rvFavourite.adapter = null
        super.onDestroyView()
        binding_ = null
    }
}