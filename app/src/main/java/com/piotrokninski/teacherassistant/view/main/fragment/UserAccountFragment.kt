package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentUserAccountBinding
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.viewmodel.main.UserAccountViewModel
import java.lang.IllegalArgumentException

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding

    private lateinit var userAccountViewModel: UserAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_user_account, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_user_account_edit -> {
                        userAccountViewModel.editing.value = !userAccountViewModel.editing.value!!
                        val editing = userAccountViewModel.editing.value!!
                        if (editing) {
                            menuItem.setIcon(R.drawable.ic_accept_icon)

                            Toast.makeText(activity, "Możesz edytować swój profil", Toast.LENGTH_SHORT).show()
                        } else {
                            menuItem.setIcon(R.drawable.ic_edit_icon)

                            userAccountViewModel.updateUserData()

                            Toast.makeText(activity, "Zmiany zaakceptowane", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.menu_user_account_settings -> {
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_userAccount_to_settings)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.userAccountToggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onToggleButtonClicked(checkedId, isChecked)
        }

        setupViewModel()
    }

    private fun onToggleButtonClicked(checkedId: Int, isChecked: Boolean) {

        if (isChecked) {
            val viewType = when (checkedId) {
                R.id.user_account_student_toggle_button -> AppConstants.VIEW_TYPE_STUDENT

                R.id.user_account_tutor_toggle_button -> AppConstants.VIEW_TYPE_TUTOR

                else -> {
                    throw IllegalArgumentException("No such id is legal: $checkedId")
                }
            }

            userAccountViewModel.updateViewType(viewType)
        }
    }

    private fun setupViewModel() {
        val factory = UserAccountViewModel.Factory(DataStoreRepository(requireContext()))
        userAccountViewModel = ViewModelProvider(this, factory)[UserAccountViewModel::class.java]

        binding.userViewModel = userAccountViewModel
        binding.lifecycleOwner = this

        initViewType()

        observeEditing()

        observeUser()
    }

    private fun observeEditing() {
        userAccountViewModel.editing.observe(viewLifecycleOwner) { editing ->
            binding.userAccountFullName.isFocusableInTouchMode = editing
            binding.userAccountFullName.isEnabled = editing

            binding.userAccountUsername.isFocusableInTouchMode = editing
            binding.userAccountUsername.isEnabled = editing

            binding.userAccountStudentSwitch.isEnabled = editing
            binding.userAccountTutorSwitch.isEnabled = editing

            binding.userAccountSubjects.isFocusableInTouchMode = editing
            binding.userAccountSubjects.isEnabled = editing
        }
    }

    private fun observeUser() {
        userAccountViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user.student && user.tutor) {
                binding.userAccountToggleButton.visibility = View.VISIBLE
            } else {
                binding.userAccountToggleButton.visibility = View.GONE
            }
        }
    }

    private fun initViewType() {
        when (userAccountViewModel.viewType.value) {

            AppConstants.VIEW_TYPE_STUDENT -> binding.userAccountStudentToggleButton.isChecked = true

            AppConstants.VIEW_TYPE_TUTOR -> binding.userAccountTutorToggleButton.isChecked = true
        }
    }
}