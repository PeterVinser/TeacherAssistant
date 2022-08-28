package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.newStringBuilder
import com.piotrokninski.teacherassistant.databinding.FragmentCoursesBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.CoursesAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.CoursesViewModel

class CoursesFragment : Fragment() {

    private lateinit var binding: FragmentCoursesBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoursesAdapter

    private lateinit var coursesViewModel: CoursesViewModel

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

        setupViewModel()
    }

    private fun onAddCourseClicked() {
        val action = CoursesFragmentDirections.actionCourseToInvitation(
            editable = true,
            type = Invitation.Contract.TYPE_COURSE
        )
        this.findNavController().navigate(action)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = CoursesAdapter(
            { course: Course -> courseClicked(course) },
            requireActivity()
        )
        recyclerView.adapter = adapter
    }

    private fun courseClicked(course: Course) {
        val action = CoursesFragmentDirections.actionCoursesToCourseDetails(course)
        this.findNavController().navigate(action)
    }

    private fun setupViewModel() {
        val factory = CoursesViewModel.Factory(DataStoreRepository(requireContext()))
        coursesViewModel =
            ViewModelProvider(this, factory)[CoursesViewModel::class.java]

        initRecyclerView()

        if (coursesViewModel.viewType == AppConstants.VIEW_TYPE_STUDENT) {
            binding.coursesAddButton.visibility = View.GONE
        }

        coursesViewModel.courses.observe(viewLifecycleOwner) { courses ->
            if (courses.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                binding.coursesNotFound.visibility = View.VISIBLE

                binding.coursesAddButton.visibility = View.VISIBLE
                binding.coursesAddButton.setOnClickListener { onAddCourseClicked() }

                (activity as MainActivity).isFABVisible(false)

                binding.coursesLayout.gravity = Gravity.CENTER
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.coursesNotFound.visibility = View.GONE

                binding.coursesAddButton.visibility = View.GONE

                (activity as MainActivity).isFABVisible(true)
                (activity as MainActivity).setFABListener { onAddCourseClicked() }

                binding.coursesLayout.gravity = Gravity.TOP

                adapter.setCourses(courses)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).isFABVisible(false)
    }
}