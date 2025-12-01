package com.example.tiodaperua.ui.alunos

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
import com.example.tiodaperua.data.Aluno
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Escola
import com.example.tiodaperua.data.Responsavel
import com.example.tiodaperua.data.Turma
import com.example.tiodaperua.databinding.FragmentAlunosBinding
import com.example.tiodaperua.model.CepResponse
import com.example.tiodaperua.network.RetrofitInstance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlunosFragment : Fragment() {

    private var _binding: FragmentAlunosBinding? = null
    private val binding get() = _binding!!

    private lateinit var alunoAdapter: AlunoAdapter
    private lateinit var db: AppDatabase
    private var alunoParaEditar: Aluno? = null
    private var responsaveis: List<Responsavel> = emptyList()
    private var escolas: List<Escola> = emptyList()
    private var turmas: List<Turma> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlunosBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeAlunos()
        setupCepListener()
        observeResponsaveis()
        observeEscolas()
        observeTurmas()

        binding.buttonSalvarAluno.setOnClickListener {
            saveOrUpdateAluno()
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
        alunoAdapter = AlunoAdapter(
            onEditClicked = { aluno -> setupEditMode(aluno) },
            onDeleteClicked = { aluno -> showDeleteConfirmation(aluno) }
        )
        binding.recyclerViewAlunos.adapter = alunoAdapter
    }

    private fun observeAlunos() {
        lifecycleScope.launch {
            db.alunoDao().getAllAlunos().collectLatest { alunos ->
                alunoAdapter.submitList(alunos)
            }
        }
    }

    private fun observeResponsaveis() {
        lifecycleScope.launch {
            db.responsavelDao().getAllResponsaveis().collectLatest { listaResponsaveis ->
                responsaveis = listaResponsaveis
                val nomesResponsaveis = responsaveis.map { it.nome }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomesResponsaveis)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerResponsaveis.adapter = adapter

                binding.spinnerResponsaveis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (responsaveis.isNotEmpty()) {
                            val selectedResponsavel = responsaveis[position]
                            binding.inputEditTextCep.setText(selectedResponsavel.cep)
                            binding.inputEditTextLogradouro.setText(selectedResponsavel.logradouro)
                            binding.inputEditTextNumero.setText(selectedResponsavel.numero)
                            binding.inputEditTextComplemento.setText(selectedResponsavel.complemento)
                            binding.inputEditTextBairro.setText(selectedResponsavel.bairro)
                            binding.inputEditTextCidade.setText(selectedResponsavel.cidade)
                            binding.inputEditTextEstado.setText(selectedResponsavel.estado)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
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
            }
        }
    }

    private fun observeTurmas() {
        lifecycleScope.launch {
            db.turmaDao().getAllTurmas().collectLatest { listaTurmas ->
                turmas = listaTurmas
                val nomesTurmas = turmas.map { it.nome }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomesTurmas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTurmas.adapter = adapter
            }
        }
    }

    private fun setupEditMode(aluno: Aluno) {
        alunoParaEditar = aluno
        binding.inputEditTextNomeAluno.setText(aluno.nome)
        binding.inputEditTextCep.setText(aluno.cep)
        binding.inputEditTextLogradouro.setText(aluno.logradouro)
        binding.inputEditTextNumero.setText(aluno.numero)
        binding.inputEditTextComplemento.setText(aluno.complemento)
        binding.inputEditTextBairro.setText(aluno.bairro)
        binding.inputEditTextCidade.setText(aluno.cidade)
        binding.inputEditTextEstado.setText(aluno.estado)
        binding.buttonSalvarAluno.text = "Atualizar Aluno"

        val responsavelPosition = responsaveis.indexOfFirst { it.nome == aluno.responsavel }
        if (responsavelPosition != -1) {
            binding.spinnerResponsaveis.setSelection(responsavelPosition)
        }

        val escolaPosition = escolas.indexOfFirst { it.nome == aluno.escola }
        if (escolaPosition != -1) {
            binding.spinnerEscolas.setSelection(escolaPosition)
        }

        val turmaPosition = turmas.indexOfFirst { it.nome == aluno.turma }
        if (turmaPosition != -1) {
            binding.spinnerTurmas.setSelection(turmaPosition)
        }
    }

    private fun showDeleteConfirmation(aluno: Aluno) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir o aluno '${aluno.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                lifecycleScope.launch {
                    db.alunoDao().delete(aluno)
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Aluno excluído!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveOrUpdateAluno() {
        val nome = binding.inputEditTextNomeAluno.text.toString().trim()
        val responsavelSelecionado = binding.spinnerResponsaveis.selectedItem as? String ?: ""
        val escolaSelecionada = binding.spinnerEscolas.selectedItem as? String ?: ""
        val turmaSelecionada = binding.spinnerTurmas.selectedItem as? String ?: ""
        val cep = binding.inputEditTextCep.text.toString().trim()
        val logradouro = binding.inputEditTextLogradouro.text.toString().trim()
        val numero = binding.inputEditTextNumero.text.toString().trim()
        val complemento = binding.inputEditTextComplemento.text.toString().trim()
        val bairro = binding.inputEditTextBairro.text.toString().trim()
        val cidade = binding.inputEditTextCidade.text.toString().trim()
        val estado = binding.inputEditTextEstado.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome do aluno é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (alunoParaEditar == null) {
                val novoAluno = Aluno(0, nome, "", escolaSelecionada, turmaSelecionada, cep, logradouro, numero, complemento, bairro, cidade, estado, responsavelSelecionado)
                db.alunoDao().insert(novoAluno)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Aluno salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val alunoAtualizado = alunoParaEditar!!.copy(
                    nome = nome,
                    escola = escolaSelecionada,
                    turma = turmaSelecionada,
                    cep = cep,
                    logradouro = logradouro,
                    numero = numero,
                    complemento = complemento,
                    bairro = bairro,
                    cidade = cidade,
                    estado = estado,
                    responsavel = responsavelSelecionado
                )
                db.alunoDao().update(alunoAtualizado)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
            requireActivity().runOnUiThread {
                resetForm()
            }
        }
    }

    private fun resetForm() {
        alunoParaEditar = null
        binding.inputEditTextNomeAluno.text = null
        binding.inputEditTextCep.text = null
        binding.inputEditTextLogradouro.text = null
        binding.inputEditTextNumero.text = null
        binding.inputEditTextComplemento.text = null
        binding.inputEditTextBairro.text = null
        binding.inputEditTextCidade.text = null
        binding.inputEditTextEstado.text = null
        if (binding.spinnerResponsaveis.adapter.count > 0) {
            binding.spinnerResponsaveis.setSelection(0)
        }
        if (binding.spinnerEscolas.adapter.count > 0) {
            binding.spinnerEscolas.setSelection(0)
        }
        if (binding.spinnerTurmas.adapter.count > 0) {
            binding.spinnerTurmas.setSelection(0)
        }
        binding.inputEditTextNomeAluno.requestFocus()
        binding.buttonSalvarAluno.text = "Salvar Aluno"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
