package com.w2c.kural.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.KuralAdapter
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.KuralListBinding
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.Progress
import com.w2c.kural.viewmodel.MainActivityViewModel
import com.w2c.kural.viewmodel.MainVMFactory
import kotlinx.coroutines.*
import java.util.*

class KuralList : Fragment() {
    private lateinit var mBinding: KuralListBinding
    private var kuralAdapter: KuralAdapter? = null
    private lateinit var mKuralList: MutableList<Kural>
    private val mKuralListOriginal: MutableList<Kural> = mutableListOf()
    private var mProgress: Progress? = null

    lateinit var mViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        getKuralList()
        return mBinding.root
    }

    private fun initView() {
        mBinding = KuralListBinding.inflate(LayoutInflater.from(requireActivity()))
        mProgress = Progress.getInstance(requireActivity())
        mBinding.rvKuralList.layoutManager = LinearLayoutManager(requireActivity())
        mViewModel = ViewModelProvider(
            requireActivity(), MainVMFactory(
                MainRepository(
                    LocalDataSource(), RemoteDataSource()
                )
            )
        )[MainActivityViewModel::class.java]
        mBinding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Needs to be handle, If required.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Needs to be handle, If required.
            }

            override fun afterTextChanged(s: Editable) {
                val searchText = s.toString().lowercase(Locale.getDefault())
                if (!TextUtils.isEmpty(searchText)) {
                    val filteredList: MutableList<Kural> = mutableListOf()
                    for (kural in mKuralListOriginal) {
                        if (mViewModel.isMatchesFound(searchText, kural)) {
                            filteredList.add(kural)
                        }
                    }
                    updateList(filteredList)
                } else {
                    updateList(mKuralListOriginal)
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(kurals: MutableList<Kural>) {
        mKuralList.clear()
        mKuralList.addAll(kurals)
        kuralAdapter!!.notifyDataSetChanged()
    }

    private fun getKuralList() {
        mProgress?.showProgress()
        lifecycleScope.launch(Dispatchers.Main + getExceptionHandler()) {
            fetchKurals()
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

    private suspend fun fetchKurals() {
        mViewModel.getKurals(requireActivity())
            .observe(requireActivity()) { data: List<Kural> ->
                lifecycleScope.launch(Dispatchers.Main) {
                    setKuralList(data)
                    mProgress!!.hideProgress()
                }
            }
    }

    private fun setKuralList(data: List<Kural>) {
        mKuralListOriginal.clear()
        mKuralListOriginal.addAll(data)
        mKuralList = data.toMutableList()
        kuralAdapter = KuralAdapter(mKuralList)
        mBinding.rvKuralList.adapter = kuralAdapter
    }
}