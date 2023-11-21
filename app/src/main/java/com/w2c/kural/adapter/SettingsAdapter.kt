package com.w2c.kural.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.w2c.kural.adapter.SettingsAdapter.SettingsViewHolder
import com.w2c.kural.databinding.ListItemSettingsBinding
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.utils.NotificationPreference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsAdapter(private val mList: MutableList<String>) :
    RecyclerView.Adapter<SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val settings = ListItemSettingsBinding.inflate(LayoutInflater.from(parent.context))
        return SettingsViewHolder(settings)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = mList[position]
        holder.mSettingsBinding.tvNotification.visibility =
            if (position > 0) View.VISIBLE else View.GONE
        holder.mSettingsBinding.switch1.visibility =
            if (position == 0) View.VISIBLE else View.GONE
        holder.mSettingsBinding.tvNotification.text = item
        if (position == 0) {
            holder.mSettingsBinding.switch1.isChecked =
                NotificationPreference.getInstance(holder.itemView.context)
                    .isDailyNotifyValue
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SettingsViewHolder(var mSettingsBinding: ListItemSettingsBinding) :
        RecyclerView.ViewHolder(
            mSettingsBinding.root
        ) {

        init {
            itemView.rootView.setOnClickListener {
                val position = adapterPosition
                val context: Context = itemView.rootView.context
                when (position) {
                    0 -> {
                        navigateToDailyNotification(context)
                    }
                    1 -> {
                        navigateToMailIntent(context)
                    }
                    2 -> {
                        navigateToRateAppIntent(context)
                    }
                    3 -> {
                        navigateToShareAppIntent(context)
                    }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun navigateToDailyNotification(context: Context) {
            val preference: NotificationPreference =
                NotificationPreference.getInstance(context)
            preference.isDailyNotifyValue = !preference.isDailyNotifyValue
            notifyDataSetChanged()
            scheduleNotificationWork()
        }

        private fun navigateToMailIntent(context: Context) {
            val selectorIntent = Intent(Intent.ACTION_SENDTO)
            selectorIntent.data = Uri.parse("mailto:")
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("w2csupport@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thirukkural App Feedback : $todayDate")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "")
            emailIntent.selector = selectorIntent
            context.startActivity(emailIntent)
        }

        private fun navigateToRateAppIntent(context: Context) {
            val rateAppIntent = Intent(Intent.ACTION_VIEW)
            try {
                rateAppIntent.data = Uri.parse("market://details?id=" + getPackageName(context))
                context.startActivity(rateAppIntent)
            } catch (e: ActivityNotFoundException) {
                rateAppIntent.data = Uri.parse(
                    "http://play.google.com/store/apps/details?id" + getPackageName(context)
                )
                context.startActivity(rateAppIntent)
            }
        }

        private fun navigateToShareAppIntent(context: Context) {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            val url = "http://play.google.com/store/apps/details?id=" + getPackageName(context)
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, url)
            shareAppIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(shareAppIntent, "Share via"))
        }

        private fun getPackageName(context: Context): String {
            return context.packageName
        }

        //Formatted date 15-07-2021 09:07 PM
        private val todayDate: String
            get() {
                val currentDate = Date()
                val simpleDateFormat = SimpleDateFormat(
                    "dd-MM-yyyy hh:mm a",
                    Locale.getDefault()
                )
                val formattedDate = simpleDateFormat.format(currentDate)
                Log.d("Formatted date", formattedDate) //Formatted date 15-07-2021 09:07 PM
                return formattedDate
            }

        fun scheduleNotificationWork() {
            if (NotificationPreference.getInstance(itemView.context)
                    .isDailyNotifyValue
            ) {
                val notification = OneTimeWorkRequest.Builder(NotificationWork::class.java)
                    .setInitialDelay(15, TimeUnit.HOURS).build()
                WorkManager.getInstance(itemView.context).enqueue(notification)
            }
        }
    }
}