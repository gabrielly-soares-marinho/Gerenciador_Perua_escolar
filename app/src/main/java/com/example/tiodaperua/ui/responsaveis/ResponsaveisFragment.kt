package com.example.tiodaperua.ui.responsaveis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Responsavel
import com.example.tiodaperua.databinding.FragmentResponsaveisBinding
import com.example.tiodaperua.model.CepResponse
import com.example.tiodaperua.network.RetrofitInstance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResponsaveisFragment : Fragment() {

    private var _binding: FragmentResponsaveisBinding? = null
    private val binding get() = _binding!!

    private lateinit var responsavelAdapter: ResponsavelAdapter
    private lateinit var db: AppDatabase
    private var responsavelParaEditar: Responsavel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResponsaveisBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeResponsaveis()
        setupCepListener()

        binding.buttonSalvarResponsavel.setOnClickListener {
            saveOrUpdateResponsavel()
        }
    }

    private fun setupCepListener() {
        binding.inputEditTextCep.addTextChangedListener {
            val cep = it.toString()
            if (cep.length == 8) {
                buscarEndereco(cep)
            }
        }
    }

    private fun buscarEndereco(cep: String) {
        RetrofitInstance.api.getAddress(cep).enqueue(object : Callback<CepResponse> {
            override fun onResponse(call: Call<CepResponse>, response: Response<CepResponse>) {
                if (response.isSuccessful) {
                    val cepResponse = response.body()
                    if (cepResponse != null) {
                        binding.inputEditTextLogradouro.setText(cepResponse.logradouro)
                        binding.inputEditTextBairro.setText(cepResponse.bairro)
                        binding.inputEditTextCidade.setText(cepResponse.localidade)
                        binding.inputEditTextEstado.setText(cepResponse.uf)
                    } else {
                        Toast.makeText(context, "CEP não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Erro ao buscar CEP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CepResponse>, t: Throwable) {
                Toast.makeText(context, "Falha na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        responsavelAdapter = ResponsavelAdapter(
            onEditClicked = { responsavel -> setupEditMode(responsavel) },
            onDeleteClicked = { responsavel -> showDeleteConfirmation(responsavel) }
        )
        binding.recyclerViewResponsaveis.adapter = responsavelAdapter
    }

    private fun observeResponsaveis() {
        lifecycleScope.launch {
            db.responsavelDao().getAllResponsaveis().collectLatest { responsaveis ->
                responsavelAdapter.submitList(responsaveis)
            }
        }
    }

    private fun setupEditMode(responsavel: Responsavel) {
        responsavelParaEditar = responsavel
        binding.inputEditTextNomeResponsavel.setText(responsavel.nome)
        binding.inputEditTextTelefone.setText(responsavel.telefone)
        binding.inputEditTextEmail.setText(responsavel.email)
        binding.inputEditTextCep.setText(responsavel.cep)
        binding.inputEditTextLogradouro.setText(responsavel.logradouro)
        binding.inputEditTextNumero.setText(responsavel.numero)
        binding.inputEditTextComplemento.setText(responsavel.complemento)
        binding.inputEditTextBairro.setText(responsavel.bairro)
        binding.inputEditTextCidade.setText(responsavel.cidade)
        binding.inputEditTextEstado.setText(responsavel.estado)
        binding.buttonSalvarResponsavel.text = "Atualizar Responsável"
    }

    private fun showDeleteConfirmation(responsavel: Responsavel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir o responsável '${responsavel.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                lifecycleScope.launch {
                    db.responsavelDao().delete(responsavel)
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Responsável excluído!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveOrUpdateResponsavel() {
        val nome = binding.inputEditTextNomeResponsavel.text.toString().trim()
        val telefone = binding.inputEditTextTelefone.text.toString().trim()
        val email = binding.inputEditTextEmail.text.toString().trim()
        val cep = binding.inputEditTextCep.text.toString().trim()
        val logradouro = binding.inputEditTextLogradouro.text.toString().trim()
        val numero = binding.inputEditTextNumero.text.toString().trim()
        val complemento = binding.inputEditTextComplemento.text.toString().trim()
        val bairro = binding.inputEditTextBairro.text.toString().trim()
        val cidade = binding.inputEditTextCidade.text.toString().trim()
        val estado = binding.inputEditTextEstado.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome do responsável é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (responsavelParaEditar == null) {
                val novoResponsavel = Responsavel(0, nome, telefone, email, cep, logradouro, numero, complemento, bairro, cidade, estado)
                db.responsavelDao().insert(novoResponsavel)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Responsável salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val responsavelAtualizado = responsavelParaEditar!!.copy(
                    nome = nome,
                    telefone = telefone,
                    email = email,
                    cep = cep,
                    logradouro = logradouro,
                    numero = numero,
                    complemento = complemento,
                    bairro = bairro,
                    cidade = cidade,
                    estado = estado
                )
                db.responsavelDao().update(responsavelAtualizado)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Responsável atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
            requireActivity().runOnUiThread {
                resetForm()
            }
        }
    }

    private fun resetForm() {
        responsavelParaEditar = null
        binding.inputEditTextNomeResponsavel.text = null
        binding.inputEditTextTelefone.text = null
        binding.inputEditTextEmail.text = null
        binding.inputEditTextCep.text = null
        binding.inputEditTextLogradouro.text = null
        binding.inputEditTextNumero.text = null
        binding.inputEditTextComplemento.text = null
        binding.inputEditTextBairro.text = null
        binding.inputEditTextCidade.text = null
        binding.inputEditTextEstado.text = null
        binding.inputEditTextNomeResponsavel.requestFocus()
        binding.buttonSalvarResponsavel.text = "Salvar Responsável"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}