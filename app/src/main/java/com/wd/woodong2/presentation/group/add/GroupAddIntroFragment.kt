package com.wd.woodong2.presentation.group.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddIntroFragmentBinding

class GroupAddIntroFragment: Fragment() {
    companion object {
        fun newInstance() = GroupAddIntroFragment()
    }

    private var _binding: GroupAddIntroFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupAddSharedViewModel by activityViewModels {
        GroupAddSharedViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupAddIntroFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val groupTag = when (checkedId) {
                R.id.chip_exercise -> getString(R.string.group_add_chip_exercise)
                R.id.chip_neighbor_friend -> getString(R.string.group_add_chip_neighbor_friend)
                R.id.chip_study -> getString(R.string.group_add_chip_study)
                R.id.chip_family -> getString(R.string.group_add_chip_family)
                R.id.chip_pet -> getString(R.string.group_add_chip_pet)
                R.id.chip_volunteer -> getString(R.string.group_add_chip_volunteer)
                R.id.chip_food -> getString(R.string.group_add_chip_food)
                R.id.chip_finance -> getString(R.string.group_add_chip_finance)
                R.id.chip_arts -> getString(R.string.group_add_chip_arts)
                R.id.chip_game -> getString(R.string.group_add_chip_game)
                R.id.chip_music -> getString(R.string.group_add_chip_music)
                R.id.chip_making -> getString(R.string.group_add_chip_making)
                R.id.chip_etc -> getString(R.string.group_add_chip_etc)
                else -> null
            }
        }
    }

    private fun initViewModel() = with(sharedViewModel) {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}