package com.piotrokninski.teacherassistant.view.start

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.piotrokninski.teacherassistant.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerSignUp.setOnClickListener { onRegisterClicked(it) }
    }

    private fun onRegisterClicked(view: View) {

        val fullName = binding.registerFullName.text.toString()
        val username = binding.registerUsername.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(activity, "Wprowadź imię i nazwisko!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(activity, "Wprowadź nazwę użytkownika", Toast.LENGTH_SHORT).show()
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(activity, "Wprowadź email!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(activity, "Wprowadź hasło!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(activity, "Hasło zbyt słabe, wprowadź minimum 6 znaków", Toast.LENGTH_SHORT).show()
            return
        }

        val action = RegisterFragmentDirections.actionRegisterFragmentToDetailFragment(fullName, username, email, password)
        view.findNavController().navigate(action)
    }
}