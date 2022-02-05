package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentNewHomeworkBinding
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.HomeworkDateDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.NewHomeworkViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.NewHomeworkViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class NewHomeworkFragment : Fragment() {
    private val TAG = "NewHomeworkFragment"

    private lateinit var binding: FragmentNewHomeworkBinding

    private lateinit var newHomeworkViewModel: NewHomeworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewHomeworkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        setupViewModel()

        binding.newHomeworkLessonButton.isEnabled = false
        binding.newHomeworkDateButton.isEnabled = false
        binding.newHomeworkDescription.isEnabled = false

        binding.newHomeworkLessonToggleButton.addOnButtonCheckedListener { _, _, isChecked ->
            onLessonButtonToggled(
                isChecked
            )
        }

        binding.newHomeworkCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.newHomeworkConfirmButton.setOnClickListener {
            if (newHomeworkViewModel.checkHomework()) {

                newHomeworkViewModel.addHomework()

                (activity as MainActivity).onBackPressed()
            } else {
                Toast.makeText(activity, "Uzupełnij szczegóły pracy domowej", Toast.LENGTH_SHORT).show()
            }
        }

        binding.newHomeworkCourseTextView.setOnItemClickListener { _, _, position, _ -> onCourseSelected(position) }

        binding.newHomeworkDateButton.setOnClickListener {
            val dialog = HomeworkDateDialogFragment { date -> onDatePicked(date) }
            dialog.show(childFragmentManager, "tag")
        }
    }

    private fun setupViewModel() {
        val factory = NewHomeworkViewModelFactory()
        newHomeworkViewModel = ViewModelProvider(this, factory)[NewHomeworkViewModel::class.java]

        binding.homeworkViewModel = newHomeworkViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeCourseSnapshots()

        observeHomework()
    }

    private fun onCourseSelected(position: Int) {
        newHomeworkViewModel.onCourseSelected(position)
    }

    private fun observeCourseSnapshots() {
        newHomeworkViewModel.courseSnapshots.observe(viewLifecycleOwner) { snapshots ->
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, snapshots)
            binding.newHomeworkCourseTextView.setAdapter(arrayAdapter)
        }
    }

    private fun observeHomework() {
        newHomeworkViewModel.homework.observe(viewLifecycleOwner) { homework ->

            val enabled = homework != null

            binding.newHomeworkLessonButton.isEnabled = enabled
            binding.newHomeworkDateButton.isEnabled = enabled
            binding.newHomeworkDescription.isEnabled = enabled

            Log.d(TAG, "observeHomework: ${homework.description}")
        }
    }

    private fun onDatePicked(date: Date) {

        newHomeworkViewModel.dueDateSelected(date)

        binding.newHomeworkDueDate.visibility = View.VISIBLE

        val simpleDate = SimpleDateFormat.getDateInstance()

        binding.newHomeworkDueDate.text = simpleDate.format(date)
    }

    private fun onLessonButtonToggled(isChecked: Boolean) {
        binding.newHomeworkLessonMenu.visibility = if (isChecked) View.VISIBLE else View.GONE
        binding.newHomeworkLessonButton.text =
            if (isChecked) "" else getString(R.string.new_homework_lesson_button_description)
    }
}