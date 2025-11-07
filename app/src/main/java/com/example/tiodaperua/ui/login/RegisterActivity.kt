package com.example.tiodaperua.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.User
import com.example.tiodaperua.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.buttonRegisterUser.setOnClickListener {
            val name = binding.inputEditTextNameRegister.text.toString().trim()
            val email = binding.inputEditTextEmailRegister.text.toString().trim()
            val password = binding.inputEditTextPasswordRegister.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name = name, email = email, password = password)

            lifecycleScope.launch {
                db.userDao().insert(user)
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}