package com.example.tiodaperua.ui.escola

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
import com.example.tiodaperua.data.Escola
import com.example.tiodaperua.databinding.FragmentEscolaBinding
import com.example.tiodaperua.model.CepResponse
import com.example.tiodaperua.network.RetrofitInstance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EscolaFragment : Fragment() {

    private var _binding: FragmentEscolaBinding? = null
    private val binding get() = _binding!!

    private lateinit var escolaAdapter: EscolaAdapter
    private lateinit var db: AppDatabase
    private var escolaParaEditar: Escola? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscolaBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeEscolas()
        setupCepListener()

        binding.buttonSalvarEscola.setOnClickListener {
            saveOrUpdateEscola()
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
        escolaAdapter = EscolaAdapter(
            onEditClicked = { escola -> setupEditMode(escola) },
            onDeleteClicked = { escola -> showDeleteConfirmation(escola) }
        )
        binding.recyclerViewEscolas.adapter = escolaAdapter
    }

    private fun observeEscolas() {
        lifecycleScope.launch {
            db.escolaDao().getAllEscolas().collectLatest { escolas ->
                escolaAdapter.submitList(escolas)
            }
        }
    }

    private fun setupEditMode(escola: Escola) {
        escolaParaEditar = escola
        binding.inputEditTextNomeEscola.setText(escola.nome)
        binding.inputEditTextCep.setText(escola.cep)
        binding.inputEditTextLogradouro.setText(escola.logradouro)
        binding.inputEditTextNumero.setText(escola.numero)
        binding.inputEditTextComplemento.setText(escola.complemento)
        binding.inputEditTextBairro.setText(escola.bairro)
        binding.inputEditTextCidade.setText(escola.cidade)
        binding.inputEditTextEstado.setText(escola.estado)
        binding.inputEditTextTelefoneEscola.setText(escola.telefone)
        binding.buttonSalvarEscola.text = "Atualizar Escola"
    }

    private fun showDeleteConfirmation(escola: Escola) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir la escola '${escola.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                lifecycleScope.launch {
                    db.escolaDao().delete(escola)
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Escola excluída!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveOrUpdateEscola() {
        val nome = binding.inputEditTextNomeEscola.text.toString().trim()
        val cep = binding.inputEditTextCep.text.toString().trim()
        val logradouro = binding.inputEditTextLogradouro.text.toString().trim()
        val numero = binding.inputEditTextNumero.text.toString().trim()
        val complemento = binding.inputEditTextComplemento.text.toString().trim()
        val bairro = binding.inputEditTextBairro.text.toString().trim()
        val cidade = binding.inputEditTextCidade.text.toString().trim()
        val estado = binding.inputEditTextEstado.text.toString().trim()
        val telefone = binding.inputEditTextTelefoneEscola.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome da escola é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (escolaParaEditar == null) {
                val novaEscola = Escola(0, nome, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone)
                db.escolaDao().insert(novaEscola)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Escola salva com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val escolaAtualizada = escolaParaEditar!!.copy(
                    nome = nome,
                    cep = cep,
                    logradouro = logradouro,
                    numero = numero,
                    complemento = complemento,
                    bairro = bairro,
                    cidade = cidade,
                    estado = estado,
                    telefone = telefone
                )
                db.escolaDao().update(escolaAtualizada)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Escola atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
            requireActivity().runOnUiThread {
                resetForm()
            }
        }
    }

    private fun resetForm() {
        escolaParaEditar = null
        binding.inputEditTextNomeEscola.text = null
        binding.inputEditTextCep.text = null
        binding.inputEditTextLogradouro.text = null
        binding.inputEditTextNumero.text = null
        binding.inputEditTextComplemento.text = null
        binding.inputEditTextBairro.text = null
        binding.inputEditTextCidade.text = null
        binding.inputEditTextEstado.text = null
        binding.inputEditTextTelefoneEscola.text = null
        binding.inputEditTextNomeEscola.requestFocus()
        binding.buttonSalvarEscola.text = "Salvar Escola"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}