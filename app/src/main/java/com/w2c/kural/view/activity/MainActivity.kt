package com.w2c.kural.view.activity

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.w2c.kural.R
import com.w2c.kural.databinding.ActivityMainBinding
import com.w2c.kural.databinding.SearchListBinding
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.utils.IntentKeys
import com.w2c.kural.utils.NotificationPreference
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val view = findViewById<View>(R.id.navigation)
        val controller = Navigation.findNavController(view)
        NavigationUI.setupWithNavController(binding.bottomNav, controller)

        //Changing title in Toolbar
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.home, R.id.favourite, R.id.setting
        ).build()
        NavigationUI.setupActionBarWithNavController(this, controller, appBarConfiguration)
        scheduleNotificationWork()
    }

    fun showSearchDialog(view: View?) {
        val dialogBinding = SearchListBinding.inflate(LayoutInflater.from(this))
        val dialog =
            Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSubmit.setOnClickListener {
            val number = dialogBinding.edtSearch.text.toString()
            navigateToDetailScreen(number)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToDetailScreen(number: String) {
        val intent = Intent(this@MainActivity, KuralDetails::class.java)
        intent.putExtra(IntentKeys.KURAL_NO, number.toInt())
        startActivity(intent)
    }

    fun scheduleNotificationWork() {
        if (NotificationPreference.getInstance(this).isDailyNotifyValue) {
            val notification = OneTimeWorkRequest.Builder(NotificationWork::class.java)
                .setInitialDelay(15, TimeUnit.HOURS).build()
            WorkManager.getInstance(this).enqueue(notification)
        }
    }
}