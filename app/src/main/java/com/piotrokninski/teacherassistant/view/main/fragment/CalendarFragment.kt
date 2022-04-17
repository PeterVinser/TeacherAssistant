package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentCalendarBinding
import com.piotrokninski.teacherassistant.repository.calendar.CalendarProvider
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.CalendarAdapter
import com.piotrokninski.teacherassistant.view.main.dialog.DatePickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.CalendarFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.CalendarFragmentViewModelFactory
import java.time.ZoneId
import java.util.*

class CalendarFragment : Fragment() {
    private val TAG = "CalendarFragment"

    private lateinit var binding: FragmentCalendarBinding

    private var calendarPermissionAsked = false

    private lateinit var calendarViewModel: CalendarFragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter

    private lateinit var displayedDate: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).isBottomNavVisible(false)

        binding.calendarMeetingDate.setOnClickListener {
            val dialog = DatePickerDialogFragment(
                displayedDate,
                null,
                null,
                DatePickerDialogFragment.DATE_MODE
            ) { date, _, _ -> adapter.moveToDate(date) }
            dialog.show(childFragmentManager, "tag")
        }

        binding.calendarAddMeetingsFab.setOnClickListener {
            this.findNavController().navigate(R.id.destination_new_meeting)
        }

        recyclerView = binding.calendarMeetingsRecyclerView

        initRecyclerView()

        setupViewModel()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        adapter = CalendarAdapter(requireContext(), linearLayoutManager) { date ->
            updateButtonMeetingDate(date)
        }
        recyclerView.adapter = adapter
    }


    private fun updateButtonMeetingDate(date: Date) {
        displayedDate = date
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        binding.calendarMeetingDate.text = localDate.toString()

    }

    private fun setupViewModel() {
        val factory = CalendarFragmentViewModelFactory()
        calendarViewModel = ViewModelProvider(this, factory)[CalendarFragmentViewModel::class.java]

        observeMeetings()
    }

    private fun observeMeetings() {
        calendarViewModel.meetings.observe(viewLifecycleOwner) { meetings ->
            if (meetings.isNullOrEmpty()) {
                binding.calendarMeetingDate.visibility = View.GONE
                binding.calendarMeetingsRecyclerView.visibility = View.GONE
                binding.calendarNoMeetings.visibility = View.VISIBLE
            } else {
                binding.calendarMeetingDate.visibility = View.VISIBLE
                binding.calendarMeetingsRecyclerView.visibility = View.VISIBLE
                binding.calendarNoMeetings.visibility = View.GONE

                adapter.setCalendarItems(meetings)
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