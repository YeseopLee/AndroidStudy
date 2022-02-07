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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeData()

        val adapter = PagingAdapter()
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.getPagingData().collectLatest {
                adapter.submitData(it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // refresh
            adapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }


//        binding.recyclerView.withModels {
//            val list = arrayOf(
//                "고양이1" to "https://cdn2.thecatapi.com/images/YQKJJcqNZ.jpg",
//                "고양이2" to "https://cdn2.thecatapi.com/images/p46ys1bGF.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//                "고양이3" to "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
//            )
//            list.forEachIndexed { index, (title, utl) ->
//                itemviewholder {
//                    id("item$index")
//                    name(title)
//                    epoxyid(utl)
//                }
//            }
//        }
    }


    private fun observeData() {}

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}