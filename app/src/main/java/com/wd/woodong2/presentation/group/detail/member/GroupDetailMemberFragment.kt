package com.wd.woodong2.presentation.group.detail.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wd.woodong2.databinding.GroupDetailMemberFragmentBinding

class GroupDetailMemberFragment: Fragment() {
    companion object {
        fun newInstance() = GroupDetailMemberFragment()
    }

    private var _binding: GroupDetailMemberFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailMemberFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}