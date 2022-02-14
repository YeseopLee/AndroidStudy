package com.seoplee.androidstudy.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seoplee.androidstudy.data.entity.passenger.Data
import com.seoplee.androidstudy.databinding.EpoxyItemviewholderBinding

class PagingAdapter : PagingDataAdapter<Data, PagingViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PagingViewHolder(
            EpoxyItemviewholderBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }
        } }
}

class PagingViewHolder(
    private val binding: EpoxyItemviewholderBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(value: Data) {
        binding.idTextView.text = value._id
        binding.nameTextView.text = value.name
    }
}
