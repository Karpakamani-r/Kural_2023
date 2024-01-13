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
import com.w2c.kural.utils.AdapterActions
import com.w2c.kural.utils.NotificationPreference
import com.w2c.kural.utils.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsAdapter(private val callback: (pos: Int, action: AdapterActions) -> Unit, private val mList: List<Setting>) :
    RecyclerView.Adapter<SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val settings =
            ListItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsViewHolder(settings, callback)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = mList[position]
        if (position == 0) {
            holder.settingsBinding.switch1.visibility = View.VISIBLE
            holder.settingsBinding.tvSettings.visibility = View.GONE
            val notify = NotificationPreference.getInstance(holder.itemView.context)
                .isDailyNotifyValue
            holder.settingsBinding.switch1.isChecked = notify
            holder.settingsBinding.ivSettings.setImageResource(if (notify) item.images.first() else item.images[1])
        } else {
            holder.settingsBinding.tvSettings.visibility = View.VISIBLE
            holder.settingsBinding.switch1.visibility = View.GONE
            holder.settingsBinding.tvSettings.text = item.description
            holder.settingsBinding.ivSettings.setImageResource(item.images.first())
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class SettingsViewHolder(
        val settingsBinding: ListItemSettingsBinding,
        val callback: (pos: Int, action: AdapterActions) -> Unit
    ) :
        RecyclerView.ViewHolder(
            settingsBinding.root
        ) {

        init {
            settingsBinding.root.setOnClickListener {
                callback(adapterPosition, AdapterActions.ITEM_CLICK)
            }
        }
    }
}