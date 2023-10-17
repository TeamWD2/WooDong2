package com.wd.woodong2.presentation.home.map

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wd.woodong2.databinding.HomeMapSearchListItemBinding

class HomeMapSearchListAdapter
    (private val onClickItem: (Int, HomeMapSearchItem) -> Unit,
     private val itemList: ArrayList<HomeMapSearchItem>,
    ): RecyclerView.Adapter<HomeMapSearchListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return itemList.size
    }
    class ViewHolder(
        private val binding: HomeMapSearchListItemBinding,
        private val onClickItem: (Int, HomeMapSearchItem) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeMapSearchItem) = with(binding){
            homeMapSearchAddressItem.text = item.address
            container.setOnClickListener{
                Log.d("itemSetWhat", item.toString())
                onClickItem(
                    adapterPosition,
                    item
                )
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeMapSearchListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            onClickItem
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

}