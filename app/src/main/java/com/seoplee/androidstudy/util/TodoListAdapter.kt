package com.seoplee.androidstudy.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.databinding.ViewholderTodolistBinding

class TodoListAdapter(
    private val adapterListener : TodoAdapterListener
) : ListAdapter<Todo, TodoListAdapter.TodoListViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        return TodoListViewHolder(
            ViewholderTodolistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
        holder.bindAdapter(todo, adapterListener)
    }

    class TodoListViewHolder(
        private val binding: ViewholderTodolistBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Todo) {
            binding.apply {
                todo = item
                executePendingBindings()
            }
        }
        fun bindAdapter(item: Todo, adapterListener: TodoAdapterListener) {
            binding.closeButton.setOnClickListener {
                adapterListener.onDeleteItem(item)
            }
        }
    }
}

private class PlantDiffCallback : DiffUtil.ItemCallback<Todo>() {

    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }
}