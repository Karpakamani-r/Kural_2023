package com.w2c.kural.view.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.SettingsAdapter
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.w2c.kural.databinding.FragmentSettingsBinding
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.utils.NotificationPreference
import com.w2c.kural.utils.OnItemClickListener
import com.w2c.kural.utils.todayDate
import com.w2c.kural.view.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Settings : Fragment(), OnItemClickListener {
    private lateinit var settingsBinding: FragmentSettingsBinding
    private lateinit var adapter: SettingsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater)
        settingsList
        return settingsBinding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.updateBottomNav(this)
    }

    private val settingsList: Unit
        get() {
            val list = ArrayList<String>()
            list.add("Daily notification")
            list.add("Send Feedback")
            list.add("Rate us")
            list.add("Share app")
            settingsBinding.rvSettings.layoutManager = LinearLayoutManager(requireActivity())
            adapter = SettingsAdapter(this, list)
            settingsBinding.rvSettings.adapter = adapter
        }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                navigateToDailyNotification()
                adapter.notifyItemChanged(position)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun navigateToDailyNotification() {
        val preference: NotificationPreference =
            NotificationPreference.getInstance(requireActivity())
        preference.isDailyNotifyValue = !preference.isDailyNotifyValue
        scheduleNotificationWork()
    }

    private fun navigateToMailIntent() {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("w2csupport@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thirukkural App Feedback : $todayDate")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.selector = selectorIntent
        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(
                requireActivity(),
                "No mail application available in this device, please install mail application and try again..",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToRateAppIntent() {
        val rateAppIntent = Intent(Intent.ACTION_VIEW)
        try {
            rateAppIntent.data = Uri.parse("market://details?id=" + requireActivity().packageName)
            startActivity(rateAppIntent)
        } catch (e: ActivityNotFoundException) {
            rateAppIntent.data = Uri.parse(
                "http://play.google.com/store/apps/details?id" + requireActivity().packageName
            )
            startActivity(rateAppIntent)
        }
    }

    private fun navigateToShareAppIntent() {
        val shareAppIntent = Intent(Intent.ACTION_SEND)
        val url = "http://play.google.com/store/apps/details?id=" + requireActivity().packageName
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, url)
        shareAppIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareAppIntent, "Share via"))
    }

    fun scheduleNotificationWork() {
        if (NotificationPreference.getInstance(requireActivity()).isDailyNotifyValue) {
            val notification = OneTimeWorkRequest.Builder(NotificationWork::class.java)
                .setInitialDelay(15, TimeUnit.HOURS).build()
            WorkManager.getInstance(requireActivity()).enqueue(notification)
        }
    }
}