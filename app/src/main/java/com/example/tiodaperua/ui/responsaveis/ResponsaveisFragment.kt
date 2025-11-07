package com.example.tiodaperua.ui.responsaveis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Responsavel
import com.example.tiodaperua.databinding.FragmentResponsaveisBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        binding.buttonSalvarResponsavel.setOnClickListener {
            saveOrUpdateResponsavel()
        }
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
        binding.inputEditTextEnderecoResponsavel.setText(responsavel.endereco)
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
        val endereco = binding.inputEditTextEnderecoResponsavel.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome do responsável é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (responsavelParaEditar == null) {
                val novoResponsavel = Responsavel(nome = nome, telefone = telefone, email = email, endereco = endereco)
                db.responsavelDao().insert(novoResponsavel)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Responsável salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val responsavelAtualizado = responsavelParaEditar!!.copy(nome = nome, telefone = telefone, email = email, endereco = endereco)
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
        binding.inputEditTextEnderecoResponsavel.text = null
        binding.inputEditTextNomeResponsavel.requestFocus()
        binding.buttonSalvarResponsavel.text = "Salvar Responsável"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}