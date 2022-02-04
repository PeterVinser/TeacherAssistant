package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentNewHomeworkBinding
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.HomeworkDateDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class NewHomeworkFragment : Fragment() {

    private lateinit var binding: FragmentNewHomeworkBinding

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

        binding.newHomeworkLessonToggleButton.addOnButtonCheckedListener { _, _, isChecked -> onLessonButtonToggled(isChecked) }

        binding.newHomeworkCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.newHomeworkDateButton.setOnClickListener {
            val dialog = HomeworkDateDialogFragment{ date -> onDatePicked(date)}
            dialog.show(childFragmentManager, "tag")
        }
    }

    private fun onDatePicked(date: Date) {
        binding.newHomeworkDueDate.visibility = View.VISIBLE

        val simpleDate = SimpleDateFormat.getDateInstance()

        binding.newHomeworkDueDate.text = simpleDate.format(date)
    }

    private fun onLessonButtonToggled(isChecked: Boolean) {
        binding.newHomeworkLessonMenu.visibility = if (isChecked) View.VISIBLE else View.GONE
        binding.newHomeworkLessonButton.text = if (isChecked) "" else getString(R.string.new_homework_lesson_button_description)
    }
}