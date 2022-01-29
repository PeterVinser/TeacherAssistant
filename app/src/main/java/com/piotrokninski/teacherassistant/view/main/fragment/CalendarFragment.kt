package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import com.piotrokninski.teacherassistant.databinding.FragmentCalendarBinding
import com.piotrokninski.teacherassistant.repository.calendar.CalendarProvider
import com.piotrokninski.teacherassistant.view.main.MainActivity
import java.util.*
import java.util.Calendar.MONDAY

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    private var calendarPermissionAsked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).isBottomNavVisible(false)

        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarAddEvent.setOnClickListener {

            if (!calendarPermissionAsked) {
                calendarPermissionAsked = true
                (activity as MainActivity).checkCalendarPermissions()
            }
        }
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            CalendarProvider.insertEvent(requireContext())
        }
        calendarPermissionAsked = false
    }
}