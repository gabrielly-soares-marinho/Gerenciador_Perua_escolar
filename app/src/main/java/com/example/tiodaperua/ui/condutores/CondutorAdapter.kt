package com.example.tiodaperua.ui.condutores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiodaperua.data.Condutor // Importa a classe Condutor correta
import com.example.tiodaperua.databinding.ItemCondutorBinding

class CondutorAdapter(
    private val onEditClicked: (Condutor) -> Unit,
    private val onDeleteClicked: (Condutor) -> Unit
) : ListAdapter<Condutor, CondutorAdapter.CondutorViewHolder>(CondutorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CondutorViewHolder {
        val binding = ItemCondutorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CondutorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CondutorViewHolder, position: Int) {
        val condutor = getItem(position)
        holder.bind(condutor, onEditClicked, onDeleteClicked)
    }

    class CondutorViewHolder(private val binding: ItemCondutorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(condutor: Condutor, onEditClicked: (Condutor) -> Unit, onDeleteClicked: (Condutor) -> Unit) {
            binding.textViewNomeCondutorItem.text = condutor.nome
            binding.textViewTelefoneCondutorItem.text = condutor.telefone
            binding.textViewPlacaVeiculoItem.text = condutor.placaVeiculo

            binding.buttonEditCondutor.setOnClickListener { onEditClicked(condutor) }
            binding.buttonDeleteCondutor.setOnClickListener { onDeleteClicked(condutor) }
        }
    }
}

class CondutorDiffCallback : DiffUtil.ItemCallback<Condutor>() {
    override fun areItemsTheSame(oldItem: Condutor, newItem: Condutor): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Condutor, newItem: Condutor): Boolean {
        return oldItem == newItem
    }
}