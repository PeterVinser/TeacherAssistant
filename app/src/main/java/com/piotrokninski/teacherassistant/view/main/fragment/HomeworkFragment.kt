package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentHomeworkBinding
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.HomeworkAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.HomeworkViewModel

class HomeworkFragment : Fragment() {

    private lateinit var binding: FragmentHomeworkBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeworkAdapter

    private lateinit var homeworkViewModel: HomeworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeworkBinding.inflate(inflater, container, false)

        recyclerView = binding.homeworkRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).isBottomNavVisible(true)

        binding.homeworkAddButton.setOnClickListener { onAddHomeworkClicked() }

        setupViewModel()
    }

    private fun onAddHomeworkClicked() {
        this.findNavController().navigate(R.id.action_homework_to_newHomework)
    }

    private fun initRecyclerView(viewType: String) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = HomeworkAdapter(
            { homework: Homework -> homeworkClicked(homework) },
            viewType,
            requireActivity()
        )
        recyclerView.adapter = adapter
    }

    private fun homeworkClicked(homework: Homework) {
    }

    private fun setupViewModel() {
        val factory = HomeworkViewModel.Factory()
        homeworkViewModel = ViewModelProvider(this, factory)[HomeworkViewModel::class.java]

        initRecyclerView(homeworkViewModel.viewType)

        if (homeworkViewModel.viewType == AppConstants.VIEW_TYPE_STUDENT) {
            binding.homeworkAddButton.visibility = View.GONE
        }

        observeHomeworks()
    }

    private fun observeHomeworks() {
        homeworkViewModel.homeworkItems.observe(viewLifecycleOwner) { homework ->
            if (homework.isNullOrEmpty()) {
                binding.homeworkEmptyText.visibility = View.VISIBLE
                binding.homeworkRecyclerView.visibility = View.GONE

                binding.homeworkLayout.gravity = Gravity.CENTER
            } else {
                binding.homeworkEmptyText.visibility = View.GONE
                binding.homeworkRecyclerView.visibility = View.VISIBLE

                binding.homeworkLayout.gravity = Gravity.TOP

                adapter.setItems(homework)
            }
        }
    }
}