package com.w2c.kural.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.w2c.kural.adapter.SettingsAdapter
import android.view.View
import androidx.fragment.app.Fragment
import com.w2c.kural.databinding.FragmentSettingsBinding
import java.util.ArrayList

class Settings : Fragment() {
    private var settingsBinding: FragmentSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater)
        settingsList
        return settingsBinding!!.root
    }

    private val settingsList: Unit
        get() {
            val list = ArrayList<String>()
            list.add("Daily notification")
            list.add("Send Feedback")
            list.add("Rate us")
            list.add("Share app")
            settingsBinding!!.rvSettings.layoutManager = LinearLayoutManager(requireActivity())
            val settingsAdapter = SettingsAdapter(list)
            settingsBinding!!.rvSettings.adapter = settingsAdapter
        }
}