package com.w2c.kural.view.fragment

import android.content.Context
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
import com.w2c.kural.databinding.FragmentAthikaramListBinding
import com.w2c.kural.utils.AthikaramClickListener
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AthikaramList : Fragment(), AthikaramClickListener {

    private lateinit var binding: FragmentAthikaramListBinding
    private lateinit var viewmodel: MainActivityViewModel
    private lateinit var list: List<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAthikaramListBinding.inflate(inflater)
        viewmodel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        loadData()
        return binding.root
    }

    private fun loadData() {
        val args: AthikaramListArgs by navArgs()
        loadAthikaram(args.paal, args.iyal, getString(R.string.athikaram))
    }

    private fun loadAthikaram(paal: String, iyal: String?, title: String) {
        lifecycleScope.launch(Dispatchers.Main + getExceptionHandler()) {
            viewmodel.getAthikaramByPaal(requireActivity(), paal, iyal).observe(requireActivity()) {
                it?.let {
                    list = it
                    val adapter = AthikaramAdapter(title, it, this@AthikaramList)
                    binding.rvChapter.adapter = adapter
                }
            }
        }
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
        if (!::list.isInitialized) {
            throw IllegalArgumentException("List cannot be empty or null")
        }
        val item = list[position]
        val args: AthikaramListArgs by navArgs()
        val kuralList = AthikaramListDirections.actionAthikaramListToHome(
            screenType = ScreenTypes.KURALH,
            athikaram = item,
            paal = args.paal,
            iyal = args.iyal
        )
        findNavController().navigate(kuralList)
    }

}