package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.util.notifications.FcmManager
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.viewmodel.start.StartActivityViewModel
import com.piotrokninski.teacherassistant.viewmodel.start.factory.StartActivityViewModelFactory

class StartActivity : AppCompatActivity() {
    private val TAG = "StartActivity"

    private lateinit var auth: FirebaseAuth

    private lateinit var startActivityViewModel: StartActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth = FirebaseAuth.getInstance()

        setupViewModel()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()

        val extras = intent.extras

        if (auth.currentUser != null) {
            Log.d(TAG, "onStart: called")
            onSignedSuccessful(extras, null)
        }
    }

    fun onSignedSuccessful(extras: Bundle?, newUser: User?) {

        val userId = auth.currentUser!!.uid

        FcmManager.subscribeToTopic(userId)

//        startActivityViewModel.setNotifications(userId, true)

        Log.d(TAG, "onSignedSuccessful: called")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.REGISTERED_USER_EXTRA, newUser)

        if (extras != null) {
            intent.putExtras(extras)
        }

        startActivity(intent)
        finish()
    }

    private fun setupViewModel() {
        val factory = StartActivityViewModelFactory()

        startActivityViewModel = ViewModelProvider(this, factory).get(StartActivityViewModel::class.java)
    }

}