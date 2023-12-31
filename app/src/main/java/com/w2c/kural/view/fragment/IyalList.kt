package com.w2c.kural.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.w2c.kural.R
import com.w2c.kural.adapter.AthikaramAdapter
import com.w2c.kural.databinding.FragmentIyalListBinding
import com.w2c.kural.utils.AdapterActions
import com.w2c.kural.utils.AthikaramClickListener
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class IyalList : Fragment(), AthikaramClickListener {
    private var binding_: FragmentIyalListBinding? = null
    private val binding get() = binding_!!

    private lateinit var viewmodel: MainActivityViewModel
    private lateinit var list: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding_ = FragmentIyalListBinding.inflate(inflater)
        viewmodel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        loadData()
        return binding.root
    }

    private fun loadData() {
        val args: AthikaramListArgs by navArgs()
        loadIyal(args.paal, getString(R.string.iyal))
    }
    private fun loadIyal(paal: String, title: String) {
        lifecycleScope.launch(Dispatchers.Main + getExceptionHandler()) {
            viewmodel.getIyalByPaal(requireActivity(), paal).observe(viewLifecycleOwner) {
                it?.let {
                    list = it
                    val adapter = AthikaramAdapter(title, list, getCallBack)
                    binding.rvIyal.adapter = adapter
                }
            }
        }
    }

    private val getCallBack = { pos: Int, action: AdapterActions ->
        onItemClick(pos)
    }

    private fun getExceptionHandler(): CoroutineExceptionHandler {
        val handler = CoroutineExceptionHandler { _, exception ->
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireActivity(), exception.message, Toast.LENGTH_SHORT).show()
            }
        }
        return handler
    }

    override fun onItemClick(position: Int) {
        val item = list[position]
        val args: AthikaramListArgs by navArgs()
        val kuralList = IyalListDirections.actionIyalListToAthikaramList(
            paal = args.paal,
            iyal = item
        )
        findNavController().navigate(kuralList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_=null
    }
}