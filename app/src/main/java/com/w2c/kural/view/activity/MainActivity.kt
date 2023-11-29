package com.w2c.kural.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.w2c.kural.R
import com.w2c.kural.databinding.ActivityMainBinding
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.NotificationPreference
import com.w2c.kural.view.fragment.Favourite
import com.w2c.kural.view.fragment.KuralList
import com.w2c.kural.view.fragment.Settings
import com.w2c.kural.viewmodel.MainActivityViewModel
import com.w2c.kural.viewmodel.MainVMFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private var lastActionId: Int? = null
    private lateinit var bottomNavigationBar: ChipNavigationBar
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController

        bottomNavigationBar = findViewById(R.id.bottomNav)

        viewModel = ViewModelProvider(
            this, MainVMFactory(
                this, MainRepository(
                    LocalDataSource(), RemoteDataSource()
                )
            )
        ).get(MainActivityViewModel::class.java)

        bottomNavigationBar.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    navigate(R.id.paalFragment)
                }

                R.id.search -> {
                    navigate(R.id.home)
                }

                R.id.favourite -> {
                    navigate(R.id.favourite)
                }

                R.id.setting -> {
                    navigate(R.id.setting)
                }
            }
        }

        preLoadKural()

        //scheduleNotificationWork()
    }

    fun preLoadKural() {
        lifecycleScope.launch {
            viewModel.getKurals(this@MainActivity).observe(this@MainActivity) {
                viewModel.cacheKural(it)
            }
        }
    }

    private fun navigate(action: Int) {
        val currentId = controller.currentDestination?.id
        if (action != currentId) {
            lastActionId = currentId
            controller.navigate(action)
        }
    }

    fun updateBottomNav(fragment: Fragment) {
        val selectedId = if (fragment is Settings) {
            R.id.setting
        } else if (fragment is Favourite) {
            R.id.favourite
        } else if (fragment is KuralList) {
            R.id.search
        } else {
            R.id.home
        }
        bottomNavigationBar.setItemSelected(selectedId, true)
    }

    fun scheduleNotificationWork() {
        if (NotificationPreference.getInstance(this).isDailyNotifyValue) {
            val notification = OneTimeWorkRequest.Builder(NotificationWork::class.java)
                .setInitialDelay(15, TimeUnit.HOURS).build()
            WorkManager.getInstance(this).enqueue(notification)
        }
    }
}