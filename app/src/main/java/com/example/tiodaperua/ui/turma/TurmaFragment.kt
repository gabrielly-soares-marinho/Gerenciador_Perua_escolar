package com.example.tiodaperua.ui.turma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Turma
import com.example.tiodaperua.databinding.FragmentTurmaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TurmaFragment : Fragment() {

    private var _binding: FragmentTurmaBinding? = null
    private val binding get() = _binding!!

    private lateinit var turmaAdapter: TurmaAdapter
    private lateinit var db: AppDatabase
    private var turmaEmEdicao: Turma? = null

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

        binding.buttonSalvarTurma.setOnClickListener {
            salvarTurma()
        }
    }

    private fun setupRecyclerView() {
        turmaAdapter = TurmaAdapter(
            onEditClicked = { turma ->
                turmaEmEdicao = turma
                binding.inputEditTextNomeTurma.setText(turma.nome)
                binding.inputEditTextEscolaTurma.setText(turma.escola)
                binding.inputEditTextPeriodo.setText(turma.periodo)
                binding.buttonSalvarTurma.text = "Atualizar"
                binding.inputEditTextNomeTurma.requestFocus()
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

    private fun salvarTurma() {
        val nome = binding.inputEditTextNomeTurma.text.toString().trim()
        val escola = binding.inputEditTextEscolaTurma.text.toString().trim()
        val periodo = binding.inputEditTextPeriodo.text.toString().trim()

        if (nome.isEmpty() || escola.isEmpty() || periodo.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (turmaEmEdicao == null) {
                // Criar nova turma
                val turma = Turma(nome = nome, escola = escola, periodo = periodo)
                db.turmaDao().insert(turma)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Turma salva com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                }
            } else {
                // Atualizar turma existente
                val turmaAtualizada = turmaEmEdicao!!.copy(nome = nome, escola = escola, periodo = periodo)
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
        binding.inputEditTextEscolaTurma.text = null
        binding.inputEditTextPeriodo.text = null
        binding.inputEditTextNomeTurma.requestFocus()
        binding.buttonSalvarTurma.text = "Salvar"
        turmaEmEdicao = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}