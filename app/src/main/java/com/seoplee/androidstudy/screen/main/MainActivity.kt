package com.seoplee.androidstudy.screen.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.seoplee.androidstudy.R
import com.seoplee.androidstudy.databinding.ActivityLoginBinding
import com.seoplee.androidstudy.databinding.ActivityMainBinding
import com.seoplee.androidstudy.screen.login.LoginViewModel
import com.seoplee.androidstudy.util.PagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val adapter = PagingAdapter()

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

        lifecycleScope.launch {
            viewModel.getPagingData().collectLatest {
                adapter.submitData(it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.testCoroutine()
    }

    private fun observeData()  {
        viewModel.mainStateLiveData.observe(this) {
            when(it) {
                is MainState.Uninitialized -> Unit
                is MainState.Success -> handleSuccess(it)
                is MainState.Error -> handleError(it)
            }
        }
    }

    private fun handleSuccess(state: MainState.Success) {
        Log.i("SUCCESS",state.passengerInfo.toString())
    }

    private fun handleError(state: MainState.Error) {
        Log.e("FAIL",state.code.toString())
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}