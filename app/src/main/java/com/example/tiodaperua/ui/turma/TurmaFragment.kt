package com.example.tiodaperua.ui.turma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiodaperua.data.Turma // Adicionando a importação que faltava
import com.example.tiodaperua.databinding.FragmentTurmaBinding


class TurmaFragment : Fragment() {

    private var _binding: FragmentTurmaBinding? = null
    private val binding get() = _binding!!

    private lateinit var turmaViewModel: TurmaViewModel
    private var turmaParaEditar: Turma? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTurmaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        turmaViewModel = ViewModelProvider(this).get(TurmaViewModel::class.java)

        val turmaAdapter = TurmaAdapter(
            onEditClicked = { turma -> setupEditMode(turma) },
            onDeleteClicked = { turma -> showDeleteConfirmation(turma) }
        )

        binding.recyclerViewTurmas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = turmaAdapter
        }

        turmaViewModel.allTurmas.observe(viewLifecycleOwner) { turmas ->
            turmas?.let { turmaAdapter.submitList(it) }
        }

        binding.buttonSalvarTurma.setOnClickListener {
            saveOrUpdateTurma()
        }
    }

    private fun setupEditMode(turma: Turma) {
        turmaParaEditar = turma
        binding.inputEditTextNomeTurma.setText(turma.nome)
        binding.inputEditTextPeriodo.setText(turma.periodo)
        binding.inputEditTextEscolaAssociada.setText(turma.escola)
        binding.buttonSalvarTurma.text = "Atualizar Turma"
    }

    private fun showDeleteConfirmation(turma: Turma) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir a turma '${turma.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                turmaViewModel.delete(turma)
                Toast.makeText(context, "Turma excluída!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveOrUpdateTurma() {
        val nome = binding.inputEditTextNomeTurma.text.toString()
        val periodo = binding.inputEditTextPeriodo.text.toString()
        val escola = binding.inputEditTextEscolaAssociada.text.toString()

        if (nome.isNotBlank() && periodo.isNotBlank() && escola.isNotBlank()) {
            if (turmaParaEditar == null) {
                val novaTurma = Turma(id = 0, nome = nome, periodo = periodo, escola = escola)
                turmaViewModel.insert(novaTurma)
                Toast.makeText(context, "Turma salva com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                val turmaAtualizada = turmaParaEditar!!.copy(nome = nome, periodo = periodo, escola = escola)
                turmaViewModel.update(turmaAtualizada)
                Toast.makeText(context, "Turma atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            resetForm()
        } else {
            Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetForm() {
        turmaParaEditar = null
        binding.inputEditTextNomeTurma.text?.clear()
        binding.inputEditTextPeriodo.text?.clear()
        binding.inputEditTextEscolaAssociada.text?.clear()
        binding.buttonSalvarTurma.text = "Salvar Turma"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}