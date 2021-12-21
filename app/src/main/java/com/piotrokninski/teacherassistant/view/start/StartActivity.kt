package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.util.AppConstants

class StartActivity : AppCompatActivity(), OnSigned {

    private lateinit var mAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        mAuth = FirebaseAuth.getInstance()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null) {
            onSignedSuccessful(false)
        }
    }

    override fun onSignedSuccessful(registered: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.START_REGISTERED_EXTRA, registered)
        startActivity(intent)
        finish()
    }
}