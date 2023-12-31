package com.w2c.kural.view.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.w2c.kural.R
import com.w2c.kural.databinding.ActivityMainBinding
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.ScreenTypes
import com.w2c.kural.utils.hide
import com.w2c.kural.utils.visible
import com.w2c.kural.viewmodel.MainActivityViewModel
import com.w2c.kural.viewmodel.MainVMFactory
import kotlinx.coroutines.launch
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.w2c.kural.utils.ATHIKARAM
import com.w2c.kural.utils.IYAL
import com.w2c.kural.utils.PAAL
import com.w2c.kural.utils.NOTIFICATION_REQ_CODE

class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private var lastActionId: Int? = null
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
        checkNotificationPermission()
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
    }

    private fun updateFavIcon() {
        val resource = if (favorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        binding.ivFav.setImageResource(resource)
    }

    private fun checkNotificationPermission() {
        if (shouldShowRequestPermissionRationale("Manifest.permission.POST_NOTIFICATIONS")) {

        } else if (ContextCompat.checkSelfPermission(
                this@MainActivity, "Manifest.permission.POST_NOTIFICATIONS"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf("Manifest.permission.POST_NOTIFICATIONS"),
                NOTIFICATION_REQ_CODE
            )
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

            }
        }
    }
}