package com.example.tiodaperua.ui.turma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Escola
import com.example.tiodaperua.data.Turma
import com.example.tiodaperua.databinding.FragmentTurmaBinding
import com.example.tiodaperua.model.CepResponse
import com.example.tiodaperua.network.RetrofitInstance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TurmaFragment : Fragment() {

    private var _binding: FragmentTurmaBinding? = null
    private val binding get() = _binding!!

    private lateinit var turmaAdapter: TurmaAdapter
    private lateinit var db: AppDatabase
    private var turmaEmEdicao: Turma? = null
    private var escolas: List<Escola> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTurmaBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTurmas()
        setupCepListener()
        observeEscolas()

        binding.buttonSalvarTurma.setOnClickListener {
            salvarTurma()
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
        turmaAdapter = TurmaAdapter(
            onEditClicked = { turma ->
                turmaEmEdicao = turma
                binding.inputEditTextNomeTurma.setText(turma.nome)
                binding.inputEditTextPeriodo.setText(turma.periodo)
                binding.inputEditTextCep.setText(turma.cep)
                binding.inputEditTextLogradouro.setText(turma.logradouro)
                binding.inputEditTextNumero.setText(turma.numero)
                binding.inputEditTextComplemento.setText(turma.complemento)
                binding.inputEditTextBairro.setText(turma.bairro)
                binding.inputEditTextCidade.setText(turma.cidade)
                binding.inputEditTextEstado.setText(turma.estado)
                binding.buttonSalvarTurma.text = "Atualizar"
                binding.inputEditTextNomeTurma.requestFocus()

                val escolaPosition = escolas.indexOfFirst { it.nome == turma.escola }
                if (escolaPosition != -1) {
                    binding.spinnerEscolas.setSelection(escolaPosition)
                }
            },
            onDeleteClicked = { turma ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Excluir Turma")
                    .setMessage("Tem certeza de que deseja excluir esta turma?")
                    .setPositiveButton("Excluir") { _, _ ->
                        lifecycleScope.launch {
                            db.turmaDao().delete(turma)
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        binding.recyclerViewTurmas.adapter = turmaAdapter
    }

    private fun observeTurmas() {
        lifecycleScope.launch {
            db.turmaDao().getAllTurmas().collectLatest { turmas ->
                turmaAdapter.submitList(turmas)
            }
        }
    }

    private fun observeEscolas() {
        lifecycleScope.launch {
            db.escolaDao().getAllEscolas().collectLatest { listaEscolas ->
                escolas = listaEscolas
                val nomesEscolas = escolas.map { it.nome }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomesEscolas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerEscolas.adapter = adapter

                binding.spinnerEscolas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (escolas.isNotEmpty()) {
                            val selectedEscola = escolas[position]
                            binding.inputEditTextCep.setText(selectedEscola.cep)
                            binding.inputEditTextLogradouro.setText(selectedEscola.logradouro)
                            binding.inputEditTextNumero.setText(selectedEscola.numero)
                            binding.inputEditTextComplemento.setText(selectedEscola.complemento)
                            binding.inputEditTextBairro.setText(selectedEscola.bairro)
                            binding.inputEditTextCidade.setText(selectedEscola.cidade)
                            binding.inputEditTextEstado.setText(selectedEscola.estado)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun salvarTurma() {
        val nome = binding.inputEditTextNomeTurma.text.toString().trim()
        val escolaSelecionada = binding.spinnerEscolas.selectedItem as? String ?: ""
        val periodo = binding.inputEditTextPeriodo.text.toString().trim()
        val cep = binding.inputEditTextCep.text.toString().trim()
        val logradouro = binding.inputEditTextLogradouro.text.toString().trim()
        val numero = binding.inputEditTextNumero.text.toString().trim()
        val complemento = binding.inputEditTextComplemento.text.toString().trim()
        val bairro = binding.inputEditTextBairro.text.toString().trim()
        val cidade = binding.inputEditTextCidade.text.toString().trim()
        val estado = binding.inputEditTextEstado.text.toString().trim()

        if (nome.isEmpty() || escolaSelecionada.isEmpty() || periodo.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (turmaEmEdicao == null) {
                // Criar nova turma
                val turma = Turma(0, nome, escolaSelecionada, periodo, cep, logradouro, numero, complemento, bairro, cidade, estado)
                db.turmaDao().insert(turma)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Turma salva com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                }
            } else {
                // Atualizar turma existente
                val turmaAtualizada = turmaEmEdicao!!.copy(
                    nome = nome,
                    escola = escolaSelecionada,
                    periodo = periodo,
                    cep = cep,
                    logradouro = logradouro,
                    numero = numero,
                    complemento = complemento,
                    bairro = bairro,
                    cidade = cidade,
                    estado = estado
                )
                db.turmaDao().update(turmaAtualizada)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Turma atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                }
            }
        }
    }

    private fun limparCampos() {
        binding.inputEditTextNomeTurma.text = null
        binding.inputEditTextPeriodo.text = null
        binding.inputEditTextCep.text = null
        binding.inputEditTextLogradouro.text = null
        binding.inputEditTextNumero.text = null
        binding.inputEditTextComplemento.text = null
        binding.inputEditTextBairro.text = null
        binding.inputEditTextCidade.text = null
        binding.inputEditTextEstado.text = null
        if (binding.spinnerEscolas.adapter.count > 0) {
            binding.spinnerEscolas.setSelection(0)
        }
        binding.inputEditTextNomeTurma.requestFocus()
        binding.buttonSalvarTurma.text = "Salvar"
        turmaEmEdicao = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}