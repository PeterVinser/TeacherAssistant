package com.piotrokninski.teacherassistant.view.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.User
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity

class StartActivity : AppCompatActivity() {

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
            onSignedSuccessful(null)
        }
    }

    fun onSignedSuccessful(user: User?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.REGISTERED_USER_EXTRA, user)
        startActivity(intent)
        finish()
    }

}