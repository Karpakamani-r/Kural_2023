package com.w2c.kural.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.w2c.kural.R
import com.w2c.kural.databinding.FragmentPaalBinding
import com.w2c.kural.view.activity.MainActivity
import com.w2c.kural.viewmodel.MainActivityViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class PaalFragment : Fragment() {

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
        loadData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.updateBottomNav(this)
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
        binding.incLayArathuppaal.tvIyal.setText(arathuppaal.get("Iyal"))
        binding.incLayArathuppaal.tvAthikaram.setText(arathuppaal.get("Athikaram"))
        binding.incLayArathuppaal.tvKurals.setText(arathuppaal.get("Kural"))

        binding.incLayPorutpaal.tvIyal.setText(porutpaal.get("Iyal"))
        binding.incLayPorutpaal.tvAthikaram.setText(porutpaal.get("Athikaram"))
        binding.incLayPorutpaal.tvKurals.setText(porutpaal.get("Kural"))

        binding.incLayKamathuppaal.tvIyal.setText(kamathuppaal.get("Iyal"))
        binding.incLayKamathuppaal.tvAthikaram.setText(kamathuppaal.get("Athikaram"))
        binding.incLayKamathuppaal.tvKurals.setText(kamathuppaal.get("Kural"))
    }
}