package com.example.tiodaperua.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.MainActivity
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.buttonLogin.setOnClickListener {
            val email = binding.inputEditTextEmailLogin.text.toString()
            val password = binding.inputEditTextPasswordLogin.text.toString()

            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)

                runOnUiThread {
                    if (user != null && user.password == password) {
                        // Se o login for bem-sucedido, abre a tela principal
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USER_NAME", user.name)
                        intent.putExtra("USER_EMAIL", user.email)
                        startActivity(intent)
                        finish() // Fecha a tela de login para que o usuário não volte para ela
                    } else {
                        // Se o login falhar, mostra uma mensagem de erro
                        Toast.makeText(this@LoginActivity, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
