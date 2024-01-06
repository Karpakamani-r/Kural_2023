package com.w2c.kural.view.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.SettingsAdapter
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.w2c.kural.databinding.FragmentSettingsBinding
import com.w2c.kural.utils.OnItemClickListener
import com.w2c.kural.utils.todayDate
import java.util.ArrayList
import com.w2c.kural.utils.*
import com.w2c.kural.R
import com.w2c.kural.model.Setting
import com.w2c.kural.viewmodel.MainActivityViewModel

class Settings : Fragment(), OnItemClickListener {
    private lateinit var settingsBinding: FragmentSettingsBinding
    private lateinit var adapter: SettingsAdapter
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater)
        setUpAd()
        loadSettingsData()
        observeUIUpdate()
        return settingsBinding.root
    }

    private fun setUpAd() {
        MobileAds.initialize(requireActivity())
        val adRequest = AdRequest.Builder().build()
        settingsBinding.adView.loadAd(adRequest)
    }

    private fun loadSettingsData() {
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        val data = viewModel.getSettingsData(requireActivity())
        settingsBinding.rvSettings.layoutManager = LinearLayoutManager(requireActivity())
        adapter = SettingsAdapter(this, data)
        settingsBinding.rvSettings.adapter = adapter
    }

    private fun observeUIUpdate() {
        viewModel.notificationRefreshUILiveData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(0)
        }
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                updateNotificationStatus()
            }

            1 -> {
                navigateToMailIntent()
            }

            2 -> {
                navigateToRateAppIntent()
            }

            3 -> {
                navigateToShareAppIntent()
            }
        }
    }

    private fun updateNotificationStatus() {
        viewModel.observeNotificationChanges(requireActivity())
    }

    private fun navigateToMailIntent() {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse(MAIL_TO)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_MAIL_ID))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$APP_FEEDBACK : $todayDate")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.selector = selectorIntent
        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(
                requireActivity(),
                APP_NOT_FOUND,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToRateAppIntent() {
        val rateAppIntent = Intent(Intent.ACTION_VIEW)
        try {
            rateAppIntent.data = Uri.parse(MARKET_PATH + requireActivity().packageName)
            startActivity(rateAppIntent)
        } catch (e: ActivityNotFoundException) {
            rateAppIntent.data = Uri.parse(
                STORE_PATH + requireActivity().packageName
            )
            startActivity(rateAppIntent)
        }
    }

    private fun navigateToShareAppIntent() {
        val shareAppIntent = Intent(Intent.ACTION_SEND)
        val url = "$STORE_PATH=" + requireActivity().packageName
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, url)
        shareAppIntent.type = PLAIN_TEXT
        startActivity(Intent.createChooser(shareAppIntent, SHARE_VIA))
    }

    override fun onManageFavorite(position: Int) {
        //Needs to handle, If required
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeAdView()
    }

    private fun removeAdView() {
        val adView = settingsBinding.adView
        if (adView.parent != null) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        adView.destroy()
    }
}