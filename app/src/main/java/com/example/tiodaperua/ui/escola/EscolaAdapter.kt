package com.example.tiodaperua.ui.escola

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiodaperua.data.Escola // Importa a classe Escola correta
import com.example.tiodaperua.databinding.ItemEscolaBinding

class EscolaAdapter(
    private val onEditClicked: (Escola) -> Unit,
    private val onDeleteClicked: (Escola) -> Unit
) : ListAdapter<Escola, EscolaAdapter.EscolaViewHolder>(EscolaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscolaViewHolder {
        val binding = ItemEscolaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EscolaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EscolaViewHolder, position: Int) {
        val escola = getItem(position)
        holder.bind(escola, onEditClicked, onDeleteClicked)
    }

    class EscolaViewHolder(private val binding: ItemEscolaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(escola: Escola, onEditClicked: (Escola) -> Unit, onDeleteClicked: (Escola) -> Unit) {
            binding.textViewNomeEscolaItem.text = escola.nome
            binding.textViewEnderecoEscolaItem.text = escola.endereco
            binding.textViewTelefoneEscolaItem.text = escola.telefone

            binding.buttonEditEscola.setOnClickListener { onEditClicked(escola) }
            binding.buttonDeleteEscola.setOnClickListener { onDeleteClicked(escola) }
        }
    }
}

class EscolaDiffCallback : DiffUtil.ItemCallback<Escola>() {
    override fun areItemsTheSame(oldItem: Escola, newItem: Escola): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Escola, newItem: Escola): Boolean {
        return oldItem == newItem
    }
}