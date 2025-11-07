package com.example.tiodaperua.ui.alunos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.Aluno
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.databinding.FragmentAlunosBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlunosFragment : Fragment() {

    private var _binding: FragmentAlunosBinding? = null
    private val binding get() = _binding!!

    private lateinit var alunoAdapter: AlunoAdapter
    private lateinit var db: AppDatabase
    private var alunoParaEditar: Aluno? = null

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

        binding.buttonSalvarAluno.setOnClickListener {
            saveOrUpdateAluno()
        }
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

    private fun setupEditMode(aluno: Aluno) {
        alunoParaEditar = aluno
        binding.inputEditTextNomeAluno.setText(aluno.nome)
        binding.inputEditTextEscola.setText(aluno.escola)
        binding.inputEditTextTurma.setText(aluno.turma)
        binding.inputEditTextEndereco.setText(aluno.endereco)
        binding.buttonSalvarAluno.text = "Atualizar Aluno"
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
        val escola = binding.inputEditTextEscola.text.toString().trim()
        val turma = binding.inputEditTextTurma.text.toString().trim()
        val endereco = binding.inputEditTextEndereco.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome do aluno é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (alunoParaEditar == null) {
                val novoAluno = Aluno(nome = nome, escola = escola, turma = turma, endereco = endereco)
                db.alunoDao().insert(novoAluno)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Aluno salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val alunoAtualizado = alunoParaEditar!!.copy(nome = nome, escola = escola, turma = turma, endereco = endereco)
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
        binding.inputEditTextEscola.text = null
        binding.inputEditTextTurma.text = null
        binding.inputEditTextEndereco.text = null
        binding.inputEditTextNomeAluno.requestFocus()
        binding.buttonSalvarAluno.text = "Salvar Aluno"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}