package com.example.dogapp.view.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.databinding.FragmentLoginBinding
import com.example.dogapp.viewmodel.LoginUiEvent
import com.example.dogapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel: LoginViewModel by viewModels()

    private var biometricPromptManager: BiometricPromptManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        biometricPromptManager = BiometricPromptManager(
            fragment = this,
            onAuthenticated = viewModel::onBiometricAuthenticated,
            onUnavailable = viewModel::showStatusMessage,
            onError = viewModel::showStatusMessage
        )

        binding.fingerprintButton.setOnClickListener {
            viewModel.onFingerprintTapped()
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            binding.tvBiometricMessage.text = message.orEmpty()
            binding.tvBiometricMessage.isVisible = !message.isNullOrBlank()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect(::handleEvent)
            }
        }
    }

    private fun handleEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.RequestBiometricPrompt -> biometricPromptManager?.authenticate()
            LoginUiEvent.NavigateToHome -> {
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    navController.navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.fingerprintAnimation.cancelAnimation()
        biometricPromptManager = null
        _binding = null
        super.onDestroyView()
    }
}
