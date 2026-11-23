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
                    Toast.makeText(applicationContext, "Acceso concedido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding.tvEstadoLogin.text = "Huella no reconocida. Intenta de nuevo."
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            binding.tvEstadoLogin.text = "Autenticación cancelada por el usuario."
                        }
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            binding.tvEstadoLogin.text = "Sensor bloqueado por demasiados intentos fallidos. Espera unos segundos."
                        }
                        else -> {
                            binding.tvEstadoLogin.text = "Error de autenticación: $errString"
                        }
                    }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso a Papelería PuntoEscolar")
            .setSubtitle("Coloca tu huella dactilar para ingresar")
            .setNegativeButtonText("Cancelar")
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
                mostrarMensaje("Este dispositivo no cuenta con sensor de huella dactilar.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                mostrarMensaje("El sensor biométrico no está disponible en este momento.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                AlertDialog.Builder(this)
                    .setTitle("Sin huella registrada")
                    .setMessage("No tienes huellas registradas en este dispositivo. ¿Deseas ir a Configuración para agregar una?")
                    .setPositiveButton("Configurar") { _, _ ->
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            else -> {
                mostrarMensaje("Autenticación biométrica no soportada.")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        binding.tvEstadoLogin.text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}