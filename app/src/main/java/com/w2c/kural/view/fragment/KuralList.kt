package com.w2c.kural.view.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.KuralListBinding
import com.w2c.kural.utils.Progress
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.utils.SwipeHelper
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.utils.OnItemClickListener

import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.*
import com.w2c.kural.R

class KuralList : Fragment(), OnItemClickListener {
    private lateinit var mBinding: KuralListBinding
    private var kuralAdapter: KuralAdapter? = null
    private var mKuralList: MutableList<Kural> = mutableListOf()
    private val mKuralListOriginal: MutableList<Kural> = mutableListOf()
    private var mProgress: Progress? = null

    private lateinit var mViewModel: MainActivityViewModel
    private var handler = Handler()
    private lateinit var runnable: Runnable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        getKuralList()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        manageUI()
    }

    private fun manageUI() {
        val activity = requireActivity() as MainActivity
        if (isFromHomeCard()) {
            activity.hideBottomNav()
            activity.showToolBar()
            mBinding.edtSearch.hide()
            mBinding.ivSearch.hide()
        } else {
            activity.hideToolBar()
            mBinding.edtSearch.visible()
            mBinding.ivSearch.visible()
            activity.updateBottomNav(this)
        }
    }

    private fun initView() {
        mBinding = KuralListBinding.inflate(LayoutInflater.from(requireActivity()))
        mProgress = Progress.getInstance(requireActivity())
        mBinding.rvKuralList.layoutManager = LinearLayoutManager(requireActivity())
        mViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        mBinding.edtSearch.addTextChangedListener {
            val key = it.toString()
            if (key.isNotEmpty()) mBinding.ivCancel.visible() else mBinding.ivCancel.hide()
            runnable = Runnable {
                searchKural(key)
            }
            handler.postDelayed(runnable, 1000)
        }
        mBinding.ivCancel.setOnClickListener {
            mBinding.edtSearch.setText("")
        }
        setUpSwipeInRecyclerView()
    }

    private fun setUpSwipeInRecyclerView(){
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(mBinding.rvKuralList) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(favoriteButton(position))
            }
        })
        itemTouchHelper.attachToRecyclerView(mBinding.rvKuralList)
    }

    private fun favoriteButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireActivity(),
            if (mKuralList[position].favourite == 0) "Mark as Favorite" else "Mark as Unfavorite",
            18.0f,
            R.color.primaryDark,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val kural = mKuralList[position]
                        kural.favourite = if (kural.favourite == 0) 1 else 0
                        mViewModel.manageFavorite(requireActivity(), kural)
                    }
                }
            })
    }

    private fun searchKural(searchKey: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val filteredKurals = mViewModel.filterKuralBySearch(searchKey)
            mKuralList.clear()
            if (filteredKurals.isNotEmpty()) {
                mKuralList.addAll(filteredKurals)
            } else {
                mKuralList.addAll(mKuralListOriginal)
            }
            kuralAdapter?.notifyDataSetChanged()
        }
    }

    private fun getKuralList() {
        lifecycleScope.launch(getExceptionHandler()) {
            getKural()
        }
    }

    private fun getExceptionHandler(): CoroutineExceptionHandler {
        val handler = CoroutineExceptionHandler { _, exception ->
            lifecycleScope.launch(Dispatchers.Main) {
                mProgress?.hideProgress()
                Toast.makeText(requireActivity(), exception.message, Toast.LENGTH_SHORT).show()
            }
        }
        return handler
    }

    private suspend fun getKural() {
        val data = mViewModel.kuralCache
        if (isFromHomeCard()) {
            fetchKuralsByRange(getNavArgs().paal, getNavArgs().athikaram)
        } else if (data.isNotEmpty()) {
            setKuralList(data)
        } else {
            fetchKurals()
        }
    }

    private fun isFromHomeCard(): Boolean {
        return getNavArgs().screenType == ScreenTypes.KURALH && !getNavArgs().paal.isNullOrEmpty()
    }

    private fun getNavArgs(): KuralListArgs {
        val kuralArgs: KuralListArgs by navArgs()
        return kuralArgs
    }

    private suspend fun fetchKuralsByRange(paal: String, athikaram: String?) {
        mProgress?.showProgress()
        mViewModel.getKuralsByRange(requireActivity(), paal, athikaram)
            .observe(requireActivity()) { data: List<Kural> ->
                setKuralList(data)
                mProgress?.hideProgress()
            }
    }

    private suspend fun fetchKurals() {
        mProgress?.showProgress()
        mViewModel.getKurals(requireActivity())
            .observe(requireActivity()) { data: List<Kural> ->
                setKuralList(data)
                mProgress?.hideProgress()
            }
    }

    private fun setKuralList(data: List<Kural>) {
        mKuralListOriginal.clear()
        mKuralListOriginal.addAll(data)
        mKuralList.clear()
        mKuralList.addAll(data)
        kuralAdapter = KuralAdapter(mKuralList, this)
        mBinding.rvKuralList.adapter = kuralAdapter
    }

    override fun onItemClick(position: Int) {
        val kuralNumber = position + 1
        val kuralDetailDirection = KuralListDirections.actionHomeToKuralDetails(kuralNumber)
        findNavController().navigate(kuralDetailDirection)
    }

    override fun onStop() {
        super.onStop()
        val activity = requireActivity() as MainActivity
        if (activity.isToolbarGone()) {
            activity.showToolBar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::runnable.isInitialized && handler.hasCallbacks(runnable)) {
            handler.removeCallbacks(runnable)
        }
    }
}