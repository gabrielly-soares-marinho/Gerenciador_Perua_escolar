package com.example.tiodaperua.ui.escola

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Escola
import com.example.tiodaperua.databinding.FragmentEscolaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        binding.buttonSalvarEscola.setOnClickListener {
            saveOrUpdateEscola()
        }
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
        binding.inputEditTextEnderecoEscola.setText(escola.endereco)
        binding.inputEditTextTelefoneEscola.setText(escola.telefone)
        binding.buttonSalvarEscola.text = "Atualizar Escola"
    }

    private fun showDeleteConfirmation(escola: Escola) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir a escola '${escola.nome}'?")
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
        val endereco = binding.inputEditTextEnderecoEscola.text.toString().trim()
        val telefone = binding.inputEditTextTelefoneEscola.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome da escola é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (escolaParaEditar == null) {
                val novaEscola = Escola(nome = nome, endereco = endereco, telefone = telefone)
                db.escolaDao().insert(novaEscola)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Escola salva com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val escolaAtualizada = escolaParaEditar!!.copy(nome = nome, endereco = endereco, telefone = telefone)
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
        binding.inputEditTextEnderecoEscola.text = null
        binding.inputEditTextTelefoneEscola.text = null
        binding.inputEditTextNomeEscola.requestFocus()
        binding.buttonSalvarEscola.text = "Salvar Escola"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}