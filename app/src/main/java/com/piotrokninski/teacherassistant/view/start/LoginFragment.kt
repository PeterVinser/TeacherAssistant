package com.piotrokninski.teacherassistant.view.start

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentLoginBinding
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            Toast.makeText(activity, "Google sign in succeeded", Toast.LENGTH_SHORT).show()
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.d("tag", "$e ")
            Toast.makeText(activity, "Google sign in failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_manual))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        callbackManager = CallbackManager.Factory.create()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginGoogleSignIn.setOnClickListener {
//            onGoogleLoginClicked(it)
        }

//        binding.loginFacebookSignIn.setOnClickListener {
//            Toast.makeText(activity, "Logowanie przez Facebook niedostępne", Toast.LENGTH_SHORT).show()
//        }

        binding.loginSignInButton.setOnClickListener {
            onLoginClicked()
        }

        binding.loginSignUpButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onGoogleLoginClicked() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    (activity as StartActivity).onSignedSuccessful(null, null)
                } else {
                    Toast.makeText(activity, "Firebase authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onLoginClicked() {
        if (TextUtils.isEmpty(binding.loginEmail.text.toString())) {
            Toast.makeText(activity, "Wprowadź adres email!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(binding.loginPassword.text.toString())) {
            Toast.makeText(activity, "Wprowadź hasło!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    (activity as StartActivity).onSignedSuccessful(null, null)
                } else {
                    if (binding.loginPassword.text.toString().length < 6) {
                        binding.loginPassword.error = getString(R.string.minimum_password_text)
                    } else {
                        Toast.makeText(activity, "Autentyfikacja nie powiodła się", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}