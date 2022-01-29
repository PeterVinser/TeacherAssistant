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
import com.piotrokninski.teacherassistant.databinding.FragmentCoursesBinding
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.CoursesAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.CoursesFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.CoursesFragmentViewModelFactory

class CoursesFragment : Fragment() {

    private lateinit var binding: FragmentCoursesBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoursesAdapter

    private lateinit var coursesViewModel: CoursesFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCoursesBinding.inflate(inflater, container, false)

        recyclerView = binding.coursesRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).isBottomNavVisible(true)

        binding.coursesAddButton.setOnClickListener { onAddCourseClicked(null) }

        setupViewModel()
    }

    private fun onAddCourseClicked(course: Course?) {
        val action = CoursesFragmentDirections.actionCoursesToNewCourse(course)
        this.findNavController().navigate(action)
    }

    private fun initRecyclerView(viewType: String) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = CoursesAdapter( { course: Course -> courseClicked(course) }, { id: Int, course: Course -> courseButtonClicked(id, course) }, viewType, requireActivity())
        recyclerView.adapter = adapter
    }

    private fun courseClicked(course: Course) {
        val action = CoursesFragmentDirections.actionCoursesToCourseDetails(course)
        this.findNavController().navigate(action)
    }

    private fun courseButtonClicked(id: Int, course: Course) {
        when (id) {
            CoursesAdapter.CANCEL_BUTTON_ID -> {
                coursesViewModel.deleteCourse(course.courseId!!)
            }

            CoursesAdapter.REJECT_BUTTON_ID -> {
                coursesViewModel.deleteCourse(course.courseId!!)
            }

            CoursesAdapter.EDIT_BUTTON_ID -> {
                onAddCourseClicked(course)
            }

            CoursesAdapter.CONFIRM_BUTTON_ID -> {
                coursesViewModel.confirmCourse(course)
            }
        }
    }

    private fun setupViewModel() {
        val factory = CoursesFragmentViewModelFactory()
        coursesViewModel = ViewModelProvider(this, factory).get(CoursesFragmentViewModel::class.java)

        initRecyclerView(coursesViewModel.viewType)

        if (coursesViewModel.viewType == AppConstants.VIEW_TYPE_STUDENT) {
            binding.coursesAddButton.visibility = View.GONE
        }

        coursesViewModel.courses.observe(viewLifecycleOwner) { courses ->
            if (courses.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                binding.coursesNotFound.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.coursesNotFound.visibility = View.GONE

                adapter.setCourses(courses)
            }
        }
    }
}