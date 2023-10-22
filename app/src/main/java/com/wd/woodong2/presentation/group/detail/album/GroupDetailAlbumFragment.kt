package com.wd.woodong2.presentation.group.detail.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wd.woodong2.databinding.GroupDetailAlbumFragmentBinding

class GroupDetailAlbumFragment: Fragment() {
    companion object {
        fun newInstance() = GroupDetailAlbumFragment()
    }

    private var _binding: GroupDetailAlbumFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailAlbumFragmentBinding.inflate(inflater, container, false)
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