package com.w2c.kural.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.w2c.kural.R
import com.w2c.kural.databinding.FragmentPaalBinding
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import com.w2c.kural.utils.ATHIKARAM
import com.w2c.kural.utils.IYAL
import com.w2c.kural.utils.KURAL

class PaalList : Fragment() {

    private var binding_: FragmentPaalBinding? = null
    private val binding get() = binding_!!
    private lateinit var mainActivityViewModel: MainActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding_ = FragmentPaalBinding.inflate(LayoutInflater.from(requireActivity()))
        mainActivityViewModel =
            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        setUpAd()
        setUpListeners()
        loadData()
        return binding.root
    }

    private fun setUpAd() {
        MobileAds.initialize(requireActivity())
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
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
            navigateToIyal(getString(R.string.porutpaal))
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
        val scope = viewLifecycleOwner.lifecycleScope
        scope.launch {
            supervisorScope {
                val arathuppaalAsync = scope.async {
                    mainActivityViewModel.getHomeCardData(
                        requireActivity(),
                        getString(R.string.arathuppaal)
                    )
                }
                val porutpaalAsync = scope.async {
                    mainActivityViewModel.getHomeCardData(
                        requireActivity(),
                        getString(R.string.porutpaal)
                    )
                }
                val kamathuppaalAsync = scope.async {
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
        binding.incLayArathuppaal.tvIyal.text = arathuppaal[IYAL]
        binding.incLayArathuppaal.tvAthikaram.text = arathuppaal[ATHIKARAM]
        binding.incLayArathuppaal.tvKurals.text = arathuppaal[KURAL]

        binding.incLayPorutpaal.tvIyal.text = porutpaal[IYAL]
        binding.incLayPorutpaal.tvAthikaram.text = porutpaal[ATHIKARAM]
        binding.incLayPorutpaal.tvKurals.text = porutpaal[KURAL]

        binding.incLayKamathuppaal.tvIyal.text = kamathuppaal[IYAL]
        binding.incLayKamathuppaal.tvAthikaram.text = kamathuppaal[ATHIKARAM]
        binding.incLayKamathuppaal.tvKurals.text = kamathuppaal[KURAL]
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

    override fun onDestroyView() {
        super.onDestroyView()
        removeAdView()
        binding_ = null
    }

    private fun removeAdView() {
        val adView = binding.adView
        if (adView.parent != null) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        adView.destroy()
    }
}