package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.proyectoapp.databinding.ActivityLoginBinding
import java.util.concurrent.Executor
import com.example.proyectoapp.util.SesionManager

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBiometria()

        binding.btnAutenticar.setOnClickListener {
            verificarDisponibilidadYAutenticar()
        }
    }

    private fun configurarBiometria() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    SesionManager.autenticado = true
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.login_acceso_concedido),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding.tvEstadoLogin.text = getString(R.string.login_huella_no_reconocida)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    binding.tvEstadoLogin.text = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                            getString(R.string.login_cancelado)
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            getString(R.string.login_bloqueo)
                        else ->
                            getString(R.string.login_error_generico, errString)
                    }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.login_titulo_prompt))
            .setSubtitle(getString(R.string.login_subtitulo_prompt))
            .setNegativeButtonText(getString(R.string.cancelar))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    private fun verificarDisponibilidadYAutenticar() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                mostrarMensaje(getString(R.string.login_sin_sensor))
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                mostrarMensaje(getString(R.string.login_sensor_no_disponible))
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.login_sin_huella_titulo)
                    .setMessage(R.string.login_sin_huella_mensaje)
                    .setPositiveButton(R.string.configurar) { _, _ ->
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    }
                    .setNegativeButton(R.string.cancelar, null)
                    .show()
            }
            else -> {
                mostrarMensaje(getString(R.string.login_no_soportada))
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        binding.tvEstadoLogin.text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}