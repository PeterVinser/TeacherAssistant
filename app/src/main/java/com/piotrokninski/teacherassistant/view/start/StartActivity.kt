package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.util.notifications.FcmManager
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.viewmodel.start.StartViewModel
import kotlinx.coroutines.flow.collect

class StartActivity : AppCompatActivity() {
    private val TAG = "StartActivity"

    private lateinit var auth: FirebaseAuth

    private lateinit var startViewModel: StartViewModel

    private var newUser: User? = null

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
//            onSignedSuccessful(extras, null, true)
            launchApp(extras)
        }
    }

    fun onSignedSuccessful(newUser: User?) {
        val userId = auth.currentUser!!.uid

        FcmManager.subscribeToTopic(userId)

        this.newUser = newUser

        startViewModel.initApp(newUser, userId)
    }

    private fun launchApp(extras: Bundle?) {
        val intent = Intent(this, MainActivity::class.java)

        if (extras != null) {
            intent.putExtras(extras)
        }

        intent.putExtra(AppConstants.REGISTERED_USER_EXTRA, newUser)

        startActivity(intent)
        finish()
    }

    private fun setupViewModel() {
        val factory = StartViewModel.Factory(DataStoreRepository(this))

        startViewModel = ViewModelProvider(this, factory)[StartViewModel::class.java]

        observeChannel()
    }

    private fun observeChannel() {
        lifecycleScope.launchWhenCreated {
            startViewModel.eventFlow.collect { event ->
                when (event) {
                    is StartViewModel.StartEvent.SignedEvent -> launchApp(null)
                }
            }
        }
    }
}