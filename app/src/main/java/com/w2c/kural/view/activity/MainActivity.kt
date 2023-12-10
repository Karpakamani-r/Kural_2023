package com.w2c.kural.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
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
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.view.fragment.Favourites
import com.w2c.kural.view.fragment.FavouritesDirections
import com.w2c.kural.view.fragment.KuralList
import com.w2c.kural.view.fragment.KuralListDirections
import com.w2c.kural.view.fragment.Settings
import com.w2c.kural.view.fragment.SettingsDirections
import com.w2c.kural.viewmodel.MainActivityViewModel
import com.w2c.kural.viewmodel.MainVMFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.appcompat.widget.Toolbar
import com.w2c.kural.view.fragment.PaalList

class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private var lastActionId: Int? = null
    private lateinit var bottomNavigationBar: ChipNavigationBar
    private lateinit var binding: ActivityMainBinding
    private var firstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        preLoadKural()
        initView()
    }

    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        bottomNavigationBar = findViewById(R.id.bottomNav)
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
        attachBackPressCallBack()
        controller.addOnDestinationChangedListener { _, destination, args ->
            val title: String? = getTitleFromDestination(destination.id, destination.label, args)
            binding.title.text = title ?: ""
        }
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getTitleFromDestination(id: Int, label: CharSequence?, args: Bundle?): String? {
        return when (id) {
            R.id.iyalList, R.id.athikaramList, R.id.home -> args?.getString("paal")
            else -> label?.toString()
        }
    }

    private fun attachBackPressCallBack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onHandleBackClick(this)
            }
        }
        this@MainActivity.onBackPressedDispatcher.addCallback(this@MainActivity, callback)
    }

    private fun preLoadKural() {
        val repo = MainRepository(LocalDataSource(), RemoteDataSource())
        val viewModel = ViewModelProvider(
            this,
            MainVMFactory(context = this, repository = repo)
        )[MainActivityViewModel::class.java]
        lifecycleScope.launch {
            viewModel.getKurals(this@MainActivity).observe(this@MainActivity) {
                if (firstTime) {
                    firstTime = false
                    viewModel.cacheKural(it)
                }
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
        val selectedId = when (fragment) {
            is Settings -> {
                R.id.setting
            }

            is Favourites -> {
                R.id.favourite
            }

            is KuralList -> {
                R.id.search
            }

            is PaalList -> {
                R.id.home
            }

            else -> {
                0
            }
        }

        //Handling bottom bar visibility
        val bottomNavNotVisible = !binding.bottomNavCard.isVisible
        if (bottomNavNotVisible) {
            binding.bottomNavCard.visible()
        }

        //Handling back icon
        binding.ivBack.hide()

        //Handling bottom bar selection
        bottomNavigationBar.setItemSelected(selectedId, true)
    }

    fun hideBottomNav() {
        binding.bottomNavCard.hide()
        //Handling back
        binding.ivBack.visible()
    }

    fun hideToolBar() {
        binding.toolbar.hide()
    }

    fun showToolBar() {
        binding.toolbar.visible()
    }

    fun isToolbarGone(): Boolean {
        return binding.toolbar.isGone
    }

    private fun onHandleBackClick(callback: OnBackPressedCallback) {
        when (controller.currentDestination?.id) {
            R.id.search -> {
                val paalList = KuralListDirections.actionHomeToPaalFragment()
                controller.navigate(paalList)
            }

            R.id.favourite -> {
                val paalList = FavouritesDirections.actionFavouriteToPaalFragment()
                controller.navigate(paalList)
            }

            R.id.setting -> {
                val paalList = SettingsDirections.actionSettingToPaalFragment()
                controller.navigate(paalList)
            }

            R.id.paalFragment -> {
                finish()
            }

            else -> {
                callback.remove()
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}