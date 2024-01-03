package com.w2c.kural.view.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.w2c.kural.R
import com.w2c.kural.databinding.ActivityMainBinding
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import com.w2c.kural.notificationwork.MyNotificationManager
import com.w2c.kural.notificationwork.NotificationWork
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.ATHIKARAM
import com.w2c.kural.utils.IYAL
import com.w2c.kural.utils.NOTIFICATION_REQ_CODE
import com.w2c.kural.utils.NotificationPreference
import com.w2c.kural.utils.PAAL
import com.w2c.kural.utils.POST_NOTIFICATIONS
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.utils.WORK_NAME
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.utils.getDifferentMillsToNextDay
import com.w2c.kural.viewmodel.MainActivityViewModel
import com.w2c.kural.viewmodel.MainVMFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var bottomNavigationBar: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private var firstTime = true
    private val topLevelDestinations = setOf(
        R.id.paalFragment, R.id.home, R.id.favourite, R.id.setting
    )
    private var favorite = false
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        preLoadKural()
        initView()
        setUpFavoriteObserver()
        setUpNotificationObserver()
    }

    override fun onResume() {
        super.onResume()
        verifyPermission()
    }

    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        bottomNavigationBar = findViewById(R.id.bottomNav)
        controller.addOnDestinationChangedListener { _, destination, args ->
            //Mange Title
            val title: String? = getTitleFromDestination(destination.label, args)
            binding.title.text = title ?: ""
            val showTitle =
                topLevelDestinations.contains(destination.id) && args?.get("screenType") != ScreenTypes.KURALH
            //Manage BottomBar and Back Button Visibility
            manageBottomBarAndBackBtn(showTitle)
            //Manage Favorite Icon in Detail Screen
            if (destination.id == R.id.kuralDetails) binding.ivFav.visible() else binding.ivFav.hide()
        }
        binding.bottomNav.setupWithNavController(controller)
    }

    private fun manageBottomBarAndBackBtn(show: Boolean) {
        if (show) {
            binding.ivBack.hide()
            bottomNavigationBar.visible()
        } else {
            binding.ivBack.visible()
            bottomNavigationBar.hide()
        }
        binding.ivBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun getTitleFromDestination(label: CharSequence?, args: Bundle?): String? {
        val athikaram = args?.getString(ATHIKARAM)
        val iyal = args?.getString(IYAL)
        val paal = args?.getString(PAAL)
        return if (!athikaram.isNullOrEmpty()) athikaram
        else if (!iyal.isNullOrEmpty()) iyal
        else if (!paal.isNullOrEmpty()) paal
        else label?.toString()
    }

    private fun preLoadKural() {
        val repo = MainRepository(LocalDataSource(), RemoteDataSource())
        viewModel = ViewModelProvider(
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

    private fun setUpFavoriteObserver() {
        binding.ivFav.setOnClickListener {
            favorite = !favorite
            updateFavIcon()
            viewModel.onFavClick()
        }
        viewModel.favUpdateTBIconLiveData.observe(this) {
            favorite = it
            updateFavIcon()
        }
        viewModel.favStatusLiveData.observe(this) {
            val message =
                if (it[0]) "Successfully, ${if (it[1]) "Added to favorites" else "Removed from favorites"}" else "Something went wrong!, Unable to ${if (it[1]) "add into favorites" else "remove from favorites"}"
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavIcon() {
        val resource = if (favorite) R.drawable.ic_remove_favorite else R.drawable.ic_add_favorite
        binding.ivFav.setImageResource(resource)
        binding.ivFav.setColorFilter(
            ContextCompat.getColor(this, R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    fun setUpNotificationObserver() {
        viewModel.notificationLiveData.observe(this) {
            checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) {
            scheduleNotificationWork()
            return
        }

        when {
            shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                controller.navigate(R.id.notificationEducationFragment)
            }

            ContextCompat.checkSelfPermission(
                this@MainActivity, POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                scheduleNotificationWork()
            }

            else -> {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(POST_NOTIFICATIONS),
                    NOTIFICATION_REQ_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.notification_granted_info),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.notificationUpdateUI()
                scheduleNotificationWork()
            } else if (!shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                showPermissionDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(POST_NOTIFICATIONS),
                    NOTIFICATION_REQ_CODE
                )
            }
        }
    }

    private fun showPermissionDialog(notify: Boolean = false) {
        val alertDialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.permission_required))
                .setCancelable(false).setMessage(getString(R.string.permission_required_desc))
                .setPositiveButton(
                    getString(R.string.go_to_settings)
                ) { dialogInterface: DialogInterface, _: Int ->
                    navigateToSettings()
                    dialogInterface.dismiss()
                }
        if (!notify) {
            alertDialog.setNegativeButton(
                getString(R.string.not_now),
            ) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun navigateToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts(
            "package",
            packageName, null
        )
        intent.data = uri
        startActivity(intent)
    }

    private fun scheduleNotificationWork() {
        val notify = viewModel.updateNotificationStatus(this)
        if (notify) {
            val workRequest: PeriodicWorkRequest =
                PeriodicWorkRequestBuilder<NotificationWork>(1, TimeUnit.DAYS)
                    .setInitialDelay(getDifferentMillsToNextDay(), TimeUnit.MILLISECONDS)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
                    .build()
            WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        } else {
            WorkManager.getInstance(this).cancelUniqueWork(WORK_NAME)
        }
        Toast.makeText(
            this,
            "Notification turned ${if (notify) "ON" else "OFF"}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun verifyPermission() {
        if (NotificationPreference.getInstance(this).isDailyNotifyValue &&
            ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionDialog(true)
        }
    }

}