package com.piotrokninski.teacherassistant.view.start

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.databinding.FragmentRegisterDetailsBinding
import com.piotrokninski.teacherassistant.model.user.User

class RegisterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRegisterDetailsBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var fullName: String
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterDetailsBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerDetailSignUpButton.setOnClickListener { onRegisterClicked() }

        arguments?.let {
            val safeArgs = RegisterDetailsFragmentArgs.fromBundle(it)

            fullName = safeArgs.fullName
            username = safeArgs.username
            email = safeArgs.email
            password = safeArgs.password
        }
    }

    private fun onRegisterClicked() {
        val isStudent = binding.registerDetailStudentSwitch.isChecked
        val isTutor = binding.registerDetailTutorSwitch.isChecked

        val subjects = binding.registerDetailSubjects.text.toString()
        val localization = binding.registerDetailLocalization.text.toString()
        val summary = binding.registerDetailSummary.text.toString()

        if (TextUtils.isEmpty(summary)) {
            Toast.makeText(activity, "Wprowadź krótki opis", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    onUserRegistered(isStudent, isTutor, subjects, localization, summary)
                    Toast.makeText(activity, "Rejestracja udana!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Autentyfikacja nie powiodła się", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onUserRegistered(isStudent: Boolean, isTutor: Boolean, subjects: String?, localization: String?, summary: String) {
        val userId = auth.currentUser!!.uid

        val user = User(userId, fullName, username, email, isStudent, isTutor, subjects, localization, null, null, summary)

        (activity as StartActivity).onSignedSuccessful(user)
    }
}