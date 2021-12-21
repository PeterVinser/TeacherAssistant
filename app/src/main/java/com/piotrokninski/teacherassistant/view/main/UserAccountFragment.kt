package com.piotrokninski.teacherassistant.view.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentUserAccountBinding
import com.piotrokninski.teacherassistant.viewmodel.UserAccountFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.factory.UserAccountFragmentViewModelFactory

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding

    private lateinit var mUserAccountViewModel: UserAccountFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        setupViewModel()
    }

    private fun setupViewModel() {
        val factory = UserAccountFragmentViewModelFactory()
        mUserAccountViewModel = ViewModelProvider(this, factory).get(UserAccountFragmentViewModel::class.java)

        binding.userViewModel = mUserAccountViewModel
        binding.lifecycleOwner = this

        observeEditing()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_account, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_user_account_edit -> {
                mUserAccountViewModel.editing.value = !mUserAccountViewModel.editing.value!!
                val editing = mUserAccountViewModel.editing.value!!
                if (editing) {
                    item.setIcon(R.drawable.ic_accept_icon)

                    Toast.makeText(activity, "Możesz edytować swój profil", Toast.LENGTH_SHORT).show()
                } else {
                    item.setIcon(R.drawable.ic_edit_icon)

                    mUserAccountViewModel.updateUserData()

                    Toast.makeText(activity, "Zmiany zaakceptowane", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.menu_user_account_settings -> {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_userAccount_to_settings)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun observeEditing() {
        mUserAccountViewModel.editing.observe(viewLifecycleOwner, { editing ->
            binding.userAccountFullName.isFocusableInTouchMode = editing
            binding.userAccountFullName.isEnabled = editing

            binding.userAccountUsername.isFocusableInTouchMode = editing
            binding.userAccountUsername.isEnabled = editing

            binding.userAccountStudentSwitch.isEnabled = editing
            binding.userAccountTutorSwitch.isEnabled = editing

            binding.userAccountSubjects.isFocusableInTouchMode = editing
            binding.userAccountSubjects.isEnabled = editing
        })
    }
}