package com.wd.woodong2.presentation.chat.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.ChatFragmentBinding
import com.wd.woodong2.presentation.chat.detail.ChatDetailActivity


class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            requireContext(),
        )
    }

    private val chatItemListAdapter by lazy {
        ChatItemListAdapter { item ->
            onClickChatItem(item)
        }
    }

    private fun onClickChatItem(item: ChatItem) {
        when (item) {
            is ChatItem.GroupChatItem, is ChatItem.PrivateChatItem -> {
                startActivity(
                    ChatDetailActivity.newIntentForDetail(
                        requireContext(),
                        item
                    )
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initView() = with(binding) {
        recyclerViewChat.apply {
            adapter = chatItemListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initModel() {
        chatViewModel.chatList.observe(viewLifecycleOwner) { itemList ->
            chatItemListAdapter.submitList(itemList.toMutableList())
        }
        chatViewModel.isLoading.observe(viewLifecycleOwner) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }
    }

    override fun onResume() {
        super.onResume()
        chatViewModel.reloadChatItems()
    }
}