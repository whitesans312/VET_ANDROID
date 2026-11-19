package com.example.dogapp.view.login

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dogapp.R

/** Encapsula el diálogo biométrico del sistema y deja el Fragment libre de su API. */
class BiometricPromptManager(
    fragment: Fragment,
    private val onAuthenticated: () -> Unit,
    private val onUnavailable: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val context = fragment.requireContext()
    private val biometricManager = BiometricManager.from(context)
    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(context.getString(R.string.login_biometric_title))
        .setSubtitle(context.getString(R.string.login_biometric_subtitle))
        .setNegativeButtonText(context.getString(R.string.login_biometric_cancel))
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

    private val prompt = BiometricPrompt(
        fragment,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthenticated()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    return
                }
                onError(errString.toString())
            }
            // onAuthenticationFailed se deja al diálogo del sistema, que presenta su propio aviso.
        }
    )

    fun authenticate() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> prompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                onUnavailable(context.getString(R.string.login_biometric_not_enrolled))

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                onUnavailable(context.getString(R.string.login_biometric_no_hardware))

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                onUnavailable(context.getString(R.string.login_biometric_hardware_unavailable))

            else -> onUnavailable(context.getString(R.string.login_biometric_unavailable))
        }
    }
}
