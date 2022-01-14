package com.piotrokninski.teacherassistant.view.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.ActivityMainBinding
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.util.PermissionsHelper
import com.piotrokninski.teacherassistant.view.main.fragment.CalendarFragment
import com.piotrokninski.teacherassistant.view.start.StartActivity
import com.piotrokninski.teacherassistant.viewmodel.main.MainActivityViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.MainActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        MainPreferences.instantiate(this)

        createNotificationChannel()

        setupNavigation()

        setupViewModel()

        onUserRegistered()
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    private fun setupViewModel() {
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val factory = MainActivityViewModelFactory(sharedPreferences)
        mainActivityViewModel =
            ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)

        observeViewType()
    }

    private fun observeViewType() {
        mainActivityViewModel.viewType.observe(this, { viewType ->

        })
    }

    private fun onUserRegistered() {

        val registeredUser =
            intent.getSerializableExtra(AppConstants.REGISTERED_USER_EXTRA) as User?

        if (registeredUser != null) {
//            navController.navigate(R.id.action_home_to_user)
            mainActivityViewModel.initUser(registeredUser)

            Log.d(TAG, "onUserRegistered: registered")
        }
    }

    private fun setupNavigation() {

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Setting the action bar
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //Setting the bottom navigation
        binding.bottomNav.setupWithNavController(navController)
    }

    fun isBottomNavVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun hideKeyboard() {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val currentFocusedView = this.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, StartActivity::class.java))
    }

    fun checkCalendarPermissions() {
        PermissionsHelper.checkCalendarPermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsHelper.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    fun notifyCalendarFragment(permissionGranted: Boolean) {
        val activeFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is CalendarFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }
    }
}