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
import com.piotrokninski.teacherassistant.viewmodel.main.NewHomeworkFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.NewHomeworkFragmentViewModelFactory
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class NewHomeworkFragment : Fragment() {
    private val TAG = "NewHomeworkFragment"

    private lateinit var binding: FragmentNewHomeworkBinding

    private lateinit var newHomeworkFragmentViewModel: NewHomeworkFragmentViewModel

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

        binding.newHomeworkLessonToggleButton.addOnButtonCheckedListener { _, _, isChecked ->
            onLessonButtonToggled(
                isChecked
            )
        }

        binding.newHomeworkCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.newHomeworkConfirmButton.setOnClickListener {
            if (newHomeworkFragmentViewModel.checkHomework()) {

                newHomeworkFragmentViewModel.addHomework()

                (activity as MainActivity).onBackPressed()
            } else {
                Toast.makeText(activity, "Uzupełnij szczegóły pracy domowej", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.newHomeworkCourseTextView.setOnItemClickListener { _, _, position, _ ->
            onCourseSelected(
                position
            )
        }

        binding.newHomeworkLessonTextView.setOnItemClickListener { _, _, position, _ ->
            onLessonSelected(
                position
            )
        }

        binding.newHomeworkDateButton.setOnClickListener {
            val dialog = HomeworkDateDialogFragment { date -> onDatePicked(date) }
            dialog.show(childFragmentManager, "tag")
        }
    }

    private fun setupViewModel() {
        val factory = NewHomeworkFragmentViewModelFactory()
        newHomeworkFragmentViewModel = ViewModelProvider(this, factory)[NewHomeworkFragmentViewModel::class.java]

        binding.homeworkViewModel = newHomeworkFragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeCourseSnapshots()

        observeLessonTopic()

        observeHomework()

        observeLesson()
    }

    private fun onCourseSelected(position: Int) {
        newHomeworkFragmentViewModel.onCourseSelected(position)
    }

    private fun onLessonSelected(position: Int) {
        newHomeworkFragmentViewModel.onLessonSelected(position)
    }

    private fun observeCourseSnapshots() {
        newHomeworkFragmentViewModel.courseSnapshots.observe(viewLifecycleOwner) { snapshots ->
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, snapshots)
            binding.newHomeworkCourseTextView.setAdapter(arrayAdapter)
        }
    }

    private fun observeLessonTopic() {
        newHomeworkFragmentViewModel.lessonTopics.observe(viewLifecycleOwner) { topics ->
            binding.newHomeworkLessonButton.isEnabled = !topics.isNullOrEmpty()

            if (!topics.isNullOrEmpty()) {
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, topics)
                binding.newHomeworkLessonTextView.setAdapter(arrayAdapter)
            }
        }
    }

    private fun observeHomework() {
        newHomeworkFragmentViewModel.homework.observe(viewLifecycleOwner) { homework ->

            val enabled = homework != null

            binding.newHomeworkLessonButton.isEnabled = enabled
            binding.newHomeworkDateButton.isEnabled = enabled
            binding.newHomeworkDescription.isEnabled = enabled
            binding.newHomeworkTopic.isEnabled = enabled

            Log.d(TAG, "observeHomework: updated")

            if (homework.reminderDate != null) {
                binding.newHomeworkReminderDateTitle.visibility = View.VISIBLE
                binding.newHomeworkReminderDateButton.visibility = View.VISIBLE

                binding.newHomeworkReminderDateButton.text = homework.reminderDate.toInstant().atZone(
                    ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
            }
        }
    }

    private fun observeLesson() {
        newHomeworkFragmentViewModel.selectedLesson.observe(viewLifecycleOwner) { lesson ->
            if (lesson != null) {
                binding.newHomeworkTopic.setText(lesson.topic)

                binding.newHomeworkTopic.isEnabled = false
            }
        }
    }

    private fun onDatePicked(date: Date) {

        newHomeworkFragmentViewModel.dueDateSelected(date)

        binding.newHomeworkDueDate.visibility = View.VISIBLE

        val simpleDate = SimpleDateFormat.getDateInstance()

        binding.newHomeworkDueDate.text = simpleDate.format(date)
    }

    private fun onLessonButtonToggled(isChecked: Boolean) {
        binding.newHomeworkLessonMenu.visibility = if (isChecked) View.VISIBLE else View.GONE
        binding.newHomeworkLessonButton.text =
            if (isChecked) "" else getString(R.string.new_homework_lesson_button_description)
        binding.newHomeworkLessonTextView.text = null

        if (newHomeworkFragmentViewModel.selectedLesson.value != null) {
            binding.newHomeworkTopic.isEnabled = true

            newHomeworkFragmentViewModel.deleteSelectedLesson()
        }
    }
}