package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.viewmodel.start.StartActivityViewModel
import com.piotrokninski.teacherassistant.viewmodel.start.factory.StartActivityViewModelFactory

class StartActivity : AppCompatActivity() {

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

        if (auth.currentUser != null) {
            onSignedSuccessful(null)
        }
    }

    fun onSignedSuccessful(newUser: User?) {

        val userId = auth.currentUser!!.uid

        startActivityViewModel.setNotifications(userId)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.REGISTERED_USER_EXTRA, newUser)
        startActivity(intent)
        finish()
    }

    private fun setupViewModel() {
        val factory = StartActivityViewModelFactory()

        startActivityViewModel = ViewModelProvider(this, factory).get(StartActivityViewModel::class.java)
    }

}