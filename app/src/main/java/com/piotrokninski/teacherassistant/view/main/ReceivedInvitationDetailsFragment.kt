package com.piotrokninski.teacherassistant.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.piotrokninski.teacherassistant.databinding.FragmentReceivedInvitationDetailsBinding

class ReceivedInvitationDetailsFragment : Fragment() {

    private lateinit var binding: FragmentReceivedInvitationDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentReceivedInvitationDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

}