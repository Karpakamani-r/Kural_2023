package com.w2c.kural.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class NotificationPreference private constructor(context: Context) {

    @set:SuppressLint("ApplySharedPref")
    var isDailyNotifyValue: Boolean
        get() = notification.getBoolean("switch", false)
        set(dailyNotifyValue) {
            val editor = notification.edit()
            editor.putBoolean("switch", dailyNotifyValue)
            editor.commit()
        }

    companion object {
        private lateinit var notification: SharedPreferences
        private lateinit var myPreference: NotificationPreference
        fun getInstance(context: Context): NotificationPreference {
            if (!Companion::myPreference.isInitialized) {
                myPreference = NotificationPreference(context)
            }
            return myPreference
        }
    }

    init {
        notification = context.getSharedPreferences("KURAL_NOTIFICATION", Context.MODE_PRIVATE)
    }
}