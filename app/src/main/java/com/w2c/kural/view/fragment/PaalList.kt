package com.w2c.kural.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.w2c.kural.R
import com.w2c.kural.databinding.FragmentPaalBinding
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class PaalList : Fragment() {

    private lateinit var binding: FragmentPaalBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaalBinding.inflate(LayoutInflater.from(requireActivity()))
        mainActivityViewModel =
            ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        setUpListeners()
        loadData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.updateBottomNav(this)
    }

    private fun setUpListeners() {
        binding.incLayArathuppaal.tvIyal.setOnClickListener {
            navigateToIyal(getString(R.string.arathuppaal))
        }
        binding.incLayArathuppaal.tvAthikaram.setOnClickListener {
            navigateToAthikaram(getString(R.string.arathuppaal))
        }
        binding.incLayArathuppaal.tvKurals.setOnClickListener {
            navigateToKuralList(getString(R.string.arathuppaal))
        }

        binding.incLayPorutpaal.tvIyal.setOnClickListener {
            navigateToIyal( getString(R.string.porutpaal))
        }
        binding.incLayPorutpaal.tvAthikaram.setOnClickListener {
            navigateToAthikaram(getString(R.string.porutpaal))
        }
        binding.incLayPorutpaal.tvKurals.setOnClickListener {
            navigateToKuralList(getString(R.string.porutpaal))
        }
        binding.incLayKamathuppaal.tvIyal.setOnClickListener {
            navigateToIyal(getString(R.string.kamathuppaal))
        }
        binding.incLayKamathuppaal.tvAthikaram.setOnClickListener {
            navigateToAthikaram(getString(R.string.kamathuppaal))
        }
        binding.incLayKamathuppaal.tvKurals.setOnClickListener {
            navigateToKuralList(getString(R.string.kamathuppaal))
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
        supervisorScope {
            val arathuppaalAsync = lifecycleScope.async {
                mainActivityViewModel.getHomeCardData(
                    requireActivity(),
                    getString(R.string.arathuppaal)
                )
            }
            val porutpaalAsync = lifecycleScope.async {
                mainActivityViewModel.getHomeCardData(
                    requireActivity(),
                    getString(R.string.porutpaal)
                )
            }
            val kamathuppaalAsync = lifecycleScope.async {
                mainActivityViewModel.getHomeCardData(
                    requireActivity(),
                    getString(R.string.kamathuppaal)
                )
            }
            setUpValues(
                arathuppaalAsync.await(),
                porutpaalAsync.await(),
                kamathuppaalAsync.await()
            )
        }
        }
    }

    private fun setUpValues(
        arathuppaal: Map<String, String>,
        porutpaal: Map<String, String>,
        kamathuppaal: Map<String, String>
    ) {
        binding.incLayArathuppaal.tvIyal.text = arathuppaal["Iyal"]
        binding.incLayArathuppaal.tvAthikaram.text = arathuppaal["Athikaram"]
        binding.incLayArathuppaal.tvKurals.text = arathuppaal["Kural"]

        binding.incLayPorutpaal.tvIyal.text = porutpaal["Iyal"]
        binding.incLayPorutpaal.tvAthikaram.text = porutpaal["Athikaram"]
        binding.incLayPorutpaal.tvKurals.text = porutpaal["Kural"]

        binding.incLayKamathuppaal.tvIyal.text = kamathuppaal["Iyal"]
        binding.incLayKamathuppaal.tvAthikaram.text = kamathuppaal["Athikaram"]
        binding.incLayKamathuppaal.tvKurals.text = kamathuppaal["Kural"]
    }

    private fun navigateToAthikaram(paal: String) {
        val iyalDirection = PaalListDirections.actionPaalFragmentToAthikaramList(
            paal = paal, iyal = null
        )
        findNavController().navigate(iyalDirection)
    }

    private fun navigateToIyal(paal: String) {
        val iyalDirection = PaalListDirections.actionPaalFragmentToIyalList(paal = paal)
        findNavController().navigate(iyalDirection)
    }

    private fun navigateToKuralList(paal: String) {
        val kuralListDirection = PaalListDirections.actionPaalFragmentToHome(
            screenType = ScreenTypes.KURALH, paal = paal, athikaram = null, iyal = null
        )
        findNavController().navigate(kuralListDirection)
    }
}