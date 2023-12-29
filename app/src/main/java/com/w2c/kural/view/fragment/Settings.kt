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
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
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
import com.w2c.kural.utils.*
import com.w2c.kural.R
import com.w2c.kural.model.Setting

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

    private val settingsList: Unit
        get() {
            val list = ArrayList<Setting>()

            val dailyNotify = Setting(
                getString(R.string.daily_one_kural),
                listOf<Int>(R.drawable.ic_bell_enable, R.drawable.ic_bell_disable)
            )
            list.add(dailyNotify)

            val feedBack = Setting(
                getString(R.string.feedback),
                listOf<Int>(R.drawable.ic_email)
            )
            list.add(feedBack)

            val rateUs = Setting(
                getString(R.string.rate_us),
                listOf<Int>(R.drawable.ic_rate)
            )
            list.add(rateUs)

            val shareApp = Setting(
                getString(R.string.share_app),
                listOf<Int>(R.drawable.ic_share)
            )
            list.add(shareApp)

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

    fun scheduleNotificationWork() {
        if (NotificationPreference.getInstance(requireActivity()).isDailyNotifyValue) {
            val workRequest: PeriodicWorkRequest =
                PeriodicWorkRequestBuilder<NotificationWork>(1, TimeUnit.DAYS)
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
                    .build()
            WorkManager.getInstance(requireActivity())
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        } else {
            WorkManager.getInstance(requireActivity()).cancelUniqueWork(WORK_NAME)
        }
    }
}