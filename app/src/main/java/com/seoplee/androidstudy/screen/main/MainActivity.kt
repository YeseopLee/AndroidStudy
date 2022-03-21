package com.seoplee.androidstudy.screen.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seoplee.androidstudy.R
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.databinding.ActivityMainBinding
import com.seoplee.androidstudy.util.TodoAdapterListener
import com.seoplee.androidstudy.util.TodoListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val adapter by lazy {
        TodoListAdapter(
            adapterListener = object : TodoAdapterListener {
                override fun onDeleteItem(item: Todo) {
                    deleteTodo(item)
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        initViews()
        observeData()
    }

    private fun initViews() {

        binding.recyclerView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.getAllTodosWithCollect()
//        lifecycleScope.launch {
//            viewModel.getAllTodosWithoutLifeCycle().collect {
//                adapter.submitList(it)
//            }
//        }
    }

    fun deleteTodo(todo: Todo) {
        viewModel.deleteTodo(todo)
    }

    private fun observeData()  {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                when(it) {
                    is MainState.Uninitialized -> handleElse()
                    is MainState.Loading -> handleLoading()
                    is MainState.GetSuccess -> handleSuccess(it)
                    is MainState.AddSuccess -> handleElse()
                    is MainState.DeleteSuccess -> handleElse()
                    is MainState.Error -> handleError(it)
                }
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun handleSuccess(state: MainState.GetSuccess) {
        binding.progressBar.visibility = View.GONE
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                state.todoInfo.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun handleError(state: MainState.Error) {
        binding.progressBar.visibility = View.GONE
        Log.e("FAIL",state.message.toString())
    }

    private fun handleElse() {
        binding.progressBar.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}