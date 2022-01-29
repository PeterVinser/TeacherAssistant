package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentNewCourseBinding
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.util.Util
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.MeetingPickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.NewCourseFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.NewCourseFragmentViewModelFactory

class NewCourseFragment : Fragment(){

    private lateinit var binding: FragmentNewCourseBinding

    private lateinit var newCourseViewModel: NewCourseFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewCourseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).isBottomNavVisible(false)

        initCourse()

        setupTypesMenu()

        setupListeners()
    }

    private fun initCourse() {
        arguments?.let {
            val safeArgs = NewCourseFragmentArgs.fromBundle(it)
            setupViewModel(safeArgs.course)
        }
    }

    private fun setupTypesMenu() {
        val types = resources.getStringArray(R.array.lesson_types)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.newCourseTypeTextView.setAdapter(arrayAdapter)
    }

    private fun setupListeners() {
        binding.newCourseStudentTextView.setOnItemClickListener { _, _, position, _ -> onStudentSelected(position) }

        binding.newCourseTypeTextView.setOnItemClickListener { adapterView, _, position, _ -> onTypeSelected(adapterView, position) }

        binding.newCourseAddMeetingButton.setOnClickListener { onAddMeetingButtonClicked() }

        binding.newCourseConfirmButton.setOnClickListener { onConfirmClicked() }

        binding.newCourseCancelButton.setOnClickListener { onCancelClicked() }
    }

    private fun onAddMeetingButtonClicked() {
        val dialog = MeetingPickerDialogFragment { weekDay: String, hour: Int, minute: Int ->
            saveMeetingTime(
                weekDay,
                hour,
                minute
            )
        }
        dialog.show(childFragmentManager, "meetingPicker")
    }

    private fun saveMeetingTime(weekDay: String, hour: Int, minute: Int) {
        val chip = Chip(activity)
        val meetingDate = Util.formatMeetingTime(weekDay, hour, minute)
        chip.text = meetingDate
        binding.newCourseChipGroup.addView(chip)

        newCourseViewModel.addMeetingDate(meetingDate!!)
    }

    private fun onStudentSelected(position: Int) {
        newCourseViewModel.onStudentSelected(position)
    }

    private fun onTypeSelected(adapterView: AdapterView<*>, position: Int) {
        newCourseViewModel.setCourseType(adapterView.getItemAtPosition(position).toString())
    }

    private fun onConfirmClicked() {
        if (newCourseViewModel.checkCourse()) {
            newCourseViewModel.addCourse()
            (activity as MainActivity).onBackPressed()
        } else {
            Toast.makeText(activity, "Uzupełnij szczegóły zajęć", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCancelClicked() {
        (activity as MainActivity).onBackPressed()
    }

    private fun setupViewModel(course: Course?) {
        val factory = NewCourseFragmentViewModelFactory(course)
        newCourseViewModel = ViewModelProvider(this, factory).get(NewCourseFragmentViewModel::class.java)

        binding.courseViewModel = newCourseViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeStudentFullNames()

        observeCourse()
    }

    private fun observeStudentFullNames() {
        newCourseViewModel.studentFullNames.observe(viewLifecycleOwner) { fullNames ->
            if (fullNames != null) {
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fullNames)
                binding.newCourseStudentTextView.setAdapter(arrayAdapter)

                if (newCourseViewModel.editing) {
                    binding.newCourseStudentTextView.setText(arrayAdapter.getItem(0).toString(), false)
                    binding.newCourseStudentMenu.isEnabled = false
                }
            }
        }
    }

    private fun observeCourse() {
        newCourseViewModel.course.observe(viewLifecycleOwner) { course ->
            if (newCourseViewModel.editing) {
                binding.newCourseTypeTextView.setText(course.type, false)

                course.meetingsDates?.forEach { meetingDate ->
                    val chip = Chip(activity)
                    chip.text = meetingDate
                    binding.newCourseChipGroup.addView(chip)
                }
            }
        }
    }
}