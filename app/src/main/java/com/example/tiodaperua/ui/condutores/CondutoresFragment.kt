package com.example.tiodaperua.ui.condutores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.data.AppDatabase
import com.example.tiodaperua.data.Condutor
import com.example.tiodaperua.databinding.FragmentCondutoresBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CondutoresFragment : Fragment() {

    private var _binding: FragmentCondutoresBinding? = null
    private val binding get() = _binding!!

    private lateinit var condutorAdapter: CondutorAdapter
    private lateinit var db: AppDatabase
    private var condutorEmEdicao: Condutor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCondutoresBinding.inflate(inflater, container, false)
        db = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeCondutores()

        binding.buttonSalvarCondutor.setOnClickListener {
            salvarCondutor()
        }
    }

    private fun setupRecyclerView() {
        condutorAdapter = CondutorAdapter(
            onEditClicked = { condutor ->
                condutorEmEdicao = condutor
                binding.inputEditTextNomeCondutor.setText(condutor.nome)
                binding.inputEditTextTelefoneCondutor.setText(condutor.telefone)
                binding.inputEditTextPlacaVeiculo.setText(condutor.placaVeiculo)
                binding.buttonSalvarCondutor.text = "Atualizar"
                binding.inputEditTextNomeCondutor.requestFocus()
            },
            onDeleteClicked = { condutor ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Excluir Condutor")
                    .setMessage("Tem certeza de que deseja excluir este condutor?")
                    .setPositiveButton("Excluir") { _, _ ->
                        lifecycleScope.launch {
                            db.condutorDao().delete(condutor)
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        binding.recyclerViewCondutores.adapter = condutorAdapter
    }

    private fun observeCondutores() {
        lifecycleScope.launch {
            db.condutorDao().getAllCondutores().collectLatest { condutores ->
                condutorAdapter.submitList(condutores)
            }
        }
    }

    private fun salvarCondutor() {
        val nome = binding.inputEditTextNomeCondutor.text.toString().trim()
        val telefone = binding.inputEditTextTelefoneCondutor.text.toString().trim()
        val placa = binding.inputEditTextPlacaVeiculo.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(context, "O nome do condutor é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (condutorEmEdicao == null) {
                // Criar novo condutor
                val condutor = Condutor(nome = nome, telefone = telefone, placaVeiculo = placa)
                db.condutorDao().insert(condutor)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Condutor salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                }
            } else {
                // Atualizar condutor existente
                val condutorAtualizado = condutorEmEdicao!!.copy(nome = nome, telefone = telefone, placaVeiculo = placa)
                db.condutorDao().update(condutorAtualizado)
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Condutor atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                }
            }
        }
    }

    private fun limparCampos() {
        binding.inputEditTextNomeCondutor.text = null
        binding.inputEditTextTelefoneCondutor.text = null
        binding.inputEditTextPlacaVeiculo.text = null
        binding.inputEditTextNomeCondutor.requestFocus()
        binding.buttonSalvarCondutor.text = "Salvar"
        condutorEmEdicao = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}