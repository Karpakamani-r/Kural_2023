package com.w2c.kural.view.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.KuralListBinding
import com.w2c.kural.utils.AdapterActions
import com.w2c.kural.utils.Progress
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.utils.OnItemClickListener

import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.*

class KuralList : Fragment() {
    private var binding_: KuralListBinding? = null
    private val binding get() = binding_!!

    private var kuralAdapter: KuralAdapter? = null
    private var mKuralList: MutableList<Kural> = mutableListOf()
    private val mKuralListOriginal: MutableList<Kural> = mutableListOf()
    private var mProgress: Progress? = null

    private lateinit var viewModel: MainActivityViewModel
    private var handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initView()
        getKuralList()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        manageUI()
    }

    private fun manageUI() {
        if (isFromHomeCard()) {
            binding.edtSearch.hide()
            binding.ivSearch.hide()
        } else {
            binding.edtSearch.visible()
            binding.ivSearch.visible()
        }
    }

    private fun initView() {
        binding_ = KuralListBinding.inflate(LayoutInflater.from(requireActivity()))
        mProgress = Progress.getInstance(requireActivity())
        binding.rvKuralList.layoutManager = LinearLayoutManager(requireActivity())
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        if (!isFromHomeCard()) {
            setUpListeners()
        }
    }

    private fun setUpListeners() {
        binding.edtSearch.tag = false
        binding.edtSearch.setOnFocusChangeListener { v, _ -> v.tag = true }
        binding.edtSearch.addTextChangedListener {
            val tag = binding.edtSearch.tag as Boolean
            val key = it.toString().lowercase().trimStart().trimEnd()
            if (tag || key.isNotEmpty()) {
                manageCancelView(key)
                runnable = Runnable {
                    searchKural(key)
                }
                handler.postDelayed(runnable, 500)
            }
        }
        binding.ivCancel.setOnClickListener {
            binding.edtSearch.setText("")
            mKuralList.addAll(mKuralListOriginal)
            kuralAdapter?.notifyDataSetChanged()
            binding.tvNotFound.hide()
        }

    }

    private fun manageCancelView(key: String) {
        if (key.isNotEmpty()) {
            binding.ivCancel.visible()
        } else {
            binding.ivCancel.hide()
        }
    }

    private fun searchKural(searchKey: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val filteredKurals = viewModel.filterKuralBySearch(searchKey)
            mKuralList.clear()
            if (filteredKurals.isNotEmpty()) {
                mKuralList.addAll(filteredKurals)
                binding.tvNotFound.hide()
            } else {
                binding.tvNotFound.visible()
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
        val data = viewModel.kuralCache
        if (isFromHomeCard()) {
            fetchKuralsByRange(getNavArgs().paal!!, getNavArgs().athikaram)
        } else if (data.isNotEmpty()) {
            setKuralList(data)
        } else {
            fetchKurals()
        }
    }

    private fun isFromHomeCard(): Boolean {
        return getNavArgs().screenType == ScreenTypes.KURALH
    }

    private fun getNavArgs(): KuralListArgs {
        val kuralArgs: KuralListArgs by navArgs()
        return kuralArgs
    }

    private suspend fun fetchKuralsByRange(paal: String, athikaram: String?) {
        mProgress?.showProgress()
        viewModel.getKuralsByRange(requireActivity(), paal, athikaram)
            .observe(viewLifecycleOwner) { data: List<Kural> ->
                setKuralList(data)
                mProgress?.hideProgress()
            }
    }

    private suspend fun fetchKurals() {
        mProgress?.showProgress()
        viewModel.getKurals(requireActivity()).observe(viewLifecycleOwner) { data: List<Kural> ->
                setKuralList(data)
                mProgress?.hideProgress()
            }
    }

    private fun setKuralList(data: List<Kural>) {
        mKuralListOriginal.clear()
        mKuralListOriginal.addAll(data)
        mKuralList.clear()
        mKuralList.addAll(data)
        kuralAdapter = KuralAdapter(mKuralList, getCallBack)
        binding.rvKuralList.adapter = kuralAdapter
    }

    private val getCallBack = { pos: Int, action: AdapterActions ->
        if (action == AdapterActions.ITEM_CLICK) onItemClick(pos)
        else onManageFavorite(pos)
    }

    private fun onItemClick(position: Int) {
        val kuralNumber = mKuralList[position].number
        val kuralDetailDirection = KuralListDirections.actionHomeToKuralDetails(kuralNumber)
        findNavController().navigate(kuralDetailDirection)
    }

    private fun onManageFavorite(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val kural = mKuralList[position].apply {
                favourite = if (favourite == 0) 1 else 0
            }
            kuralAdapter?.notifyItemChanged(position, kural)
            viewModel.manageFavorite(requireActivity(), kural)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::runnable.isInitialized && handler.hasCallbacks(runnable)) {
            handler.removeCallbacks(runnable)
        }
        binding_ = null
    }
}