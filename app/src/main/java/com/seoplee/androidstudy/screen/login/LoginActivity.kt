package com.seoplee.androidstudy.screen.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.seoplee.androidstudy.R
import com.seoplee.androidstudy.databinding.ActivityLoginBinding
import com.seoplee.androidstudy.screen.main.MainActivity
import com.seoplee.androidstudy.util.clicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeData()

        viewModel.suspendExample()

        lifecycleScope.launch {
            binding.loginButton.clicks()
                .onEach { viewModel.signIn() }
                .collect()
        }
    }

    private fun observeData() {
        viewModel.loginState.observe(this) {
            when (it) {
                is LoginState.NoId -> handleNoId()
                is LoginState.NoPassword -> handleNoPassword()
                is LoginState.AlreadyExist -> handleAlready()
                is LoginState.PasswordError -> handlePasswordError()
                is LoginState.Success -> handleSuccess(it)
                else -> Unit
            }
        }
    }

    private fun handleNoId() {
        Toast.makeText(this, R.string.noId, Toast.LENGTH_SHORT).show()
    }

    private fun handleNoPassword() {
        Toast.makeText(this, R.string.noPw, Toast.LENGTH_SHORT).show()
    }

    private fun handleAlready() {
        Toast.makeText(this, R.string.already, Toast.LENGTH_SHORT).show()
    }

    private fun handlePasswordError() {
        Toast.makeText(this, R.string.pwError, Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccess(state: LoginState.Success) {
        Toast.makeText(this, R.string.welcome, Toast.LENGTH_SHORT).show()
        startActivity(MainActivity.newIntent(this))
    }
}