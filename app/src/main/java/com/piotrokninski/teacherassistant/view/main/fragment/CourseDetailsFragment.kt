package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentCourseDetailsBinding
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.NotesAdapter
import com.piotrokninski.teacherassistant.view.main.dialog.NewLessonDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.CourseDetailsViewModel

class CourseDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCourseDetailsBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesAdapter

    private lateinit var courseDetailsViewModel: CourseDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCourseDetailsBinding.inflate(inflater, container, false)

        recyclerView = binding.courseDetailsLessonsRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).isBottomNavVisible(false)

        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_course, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        })

        binding.courseDetailsAddLessonButton.setOnClickListener { onAddNoteClicked() }

        arguments?.let {
            val safeArgs = CourseDetailsFragmentArgs.fromBundle(it)
            setupViewModel(safeArgs.course)
        }
    }

    private fun onAddNoteClicked() {
        val subject = courseDetailsViewModel.course.value!!.subject!!
        val dialog = NewLessonDialogFragment({ name: String, date: String, note: String ->
            addNote(
                name,
                date,
                note
            )
        }, subject)
        dialog.show(childFragmentManager, "newNote")
    }

    private fun addNote(name: String, date: String, note: String) {

        val course = courseDetailsViewModel.course.value!!

        val newNote = Lesson(
            course.courseId!!,
            course.studentId!!,
            course.tutorId,
            course.studentFullName!!,
            course.tutorFullName!!,
            name,
            course.subject!!,
            date,
            note
        )

        courseDetailsViewModel.addLesson(newNote)
    }

    private fun initRecyclerView(viewType: String) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter =
            NotesAdapter({ lesson: Lesson -> noteClicked(lesson) }, viewType, requireActivity())
        recyclerView.adapter = adapter
    }

    private fun noteClicked(lesson: Lesson) {

    }

    private fun setupViewModel(course: Course) {
        val factory = CourseDetailsViewModel.Factory(course)
        courseDetailsViewModel =
            ViewModelProvider(this, factory).get(CourseDetailsViewModel::class.java)

        initRecyclerView(courseDetailsViewModel.viewType)

        observeCourse()

        observeNotes()
    }

    private fun observeCourse() {
        courseDetailsViewModel.course.observe(viewLifecycleOwner) { course ->

            binding.course = course

            when (courseDetailsViewModel.viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {
                    binding.courseDetailsProfession.text = getString(
                        R.string.course_item_profession_text, getString(
                            R.string.tutor_title_text
                        )
                    )
                    binding.courseDetailsFullName.text = course.tutorFullName
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.courseDetailsProfession.text = getString(
                        R.string.course_item_profession_text, getString(
                            R.string.student_title_text
                        )
                    )
                    binding.courseDetailsFullName.text = course.studentFullName
                }
            }

            course.weekDates!!.forEach { date ->
                val chip = Chip(context)
                chip.text = date.toString()

                binding.courseDetailsChipGroup.addView(chip)
            }
        }
    }

    private fun observeNotes() {
        courseDetailsViewModel.lessons.observe(viewLifecycleOwner) { notes ->
            if (notes.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                binding.courseDetailsLessonsNotFound.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.courseDetailsLessonsNotFound.visibility = View.GONE

                adapter.setNotes(notes)
            }
        }
    }

    class Factory: ViewModelProvider.Factory {

    }
}