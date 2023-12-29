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
import com.w2c.kural.model.Setting
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.utils.NotificationPreference
import com.w2c.kural.utils.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsAdapter(private val callback: OnItemClickListener, private val mList: List<Setting>) :
    RecyclerView.Adapter<SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val settings =
            ListItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsViewHolder(settings)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = mList[position]
        holder.settingsBinding.tvNotification.visibility =
            if (position > 0) View.VISIBLE else View.GONE
        holder.settingsBinding.switch1.visibility =
            if (position == 0) View.VISIBLE else View.GONE
        holder.settingsBinding.tvNotification.text = item.description
        if (position == 0) {
            holder.settingsBinding.switch1.isChecked =
                NotificationPreference.getInstance(holder.itemView.context)
                    .isDailyNotifyValue
        }
        holder.settingsBinding.ivSettings.setImageResource(item.images.first())
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SettingsViewHolder(var settingsBinding: ListItemSettingsBinding) :
        RecyclerView.ViewHolder(
            settingsBinding.root
        ) {

        init {
            settingsBinding.root.setOnClickListener {
                callback.let {
                    callback.onItemClick(adapterPosition)
                }
            }
        }
    }
}