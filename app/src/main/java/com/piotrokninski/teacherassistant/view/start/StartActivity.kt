package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity

class StartActivity : AppCompatActivity(), OnSigned {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        mAuth = FirebaseAuth.getInstance()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
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