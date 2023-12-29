package com.w2c.kural.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.R
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.FragmentFavouriteBinding
import com.w2c.kural.utils.OnItemClickListener
import com.w2c.kural.utils.SwipeHelper
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Favourites : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var viewModel: MainActivityViewModel
    private var favAdapter: KuralAdapter? = null
    private var favourites: MutableList<Kural> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this
        binding = FragmentFavouriteBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        fetchFavorites()
        return binding.root
    }

    private fun fetchFavorites() {
        setupAdapter()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getFavoriteKurals(requireActivity()).observe(requireActivity()) {
                if (favourites.isNotEmpty()) favourites.clear()
                favourites.addAll(it)
                favAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setupAdapter() {
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireActivity())
        favAdapter = KuralAdapter(favourites, this)
        favAdapter?.setFromFavourite()
        binding.rvFavourite.adapter = favAdapter
        setUpSwipeInRecyclerView()
    }

    override fun onItemClick(position: Int) {
        val kuralNumber = favourites[position].number
        val kuralDetailDirection = FavouritesDirections.actionFavouriteToKuralDetails(kuralNumber)
        findNavController().navigate(kuralDetailDirection)
    }

    private fun setUpSwipeInRecyclerView() {
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.rvFavourite) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(favoriteButton(position))
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvFavourite)
    }

    private fun favoriteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireActivity(),
            "Mark as Unfavorite",
            18.0f,
            R.color.primaryDark,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val kural = favourites[position].apply {
                            favourite = 0
                        }
                        viewModel.manageFavorite(requireActivity(), kural)
                    }
                }
            })
    }
}