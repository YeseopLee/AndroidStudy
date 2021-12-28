package com.seoplee.androidstudy.screen.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.seoplee.androidstudy.R
import com.seoplee.androidstudy.databinding.ActivityLoginBinding
import com.seoplee.androidstudy.screen.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeData()

    }

    private fun observeData() {
        viewModel.loginState.observe(this) {
            when (it) {
                is LoginState.NoId -> handleNoId()
                is LoginState.NoPassword -> handleNoPassword()
                is LoginState.AlreadyExist -> handleAlready()
                is LoginState.PasswordError -> handlePasswordError()
                is LoginState.Success -> handleSuccess()
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

    private fun handleSuccess() {
        Toast.makeText(this, R.string.welcome, Toast.LENGTH_SHORT).show()
        startActivity(MainActivity.newIntent(this))
    }
}