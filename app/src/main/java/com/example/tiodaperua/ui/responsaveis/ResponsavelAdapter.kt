package com.example.tiodaperua.ui.responsaveis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiodaperua.data.Responsavel
import com.example.tiodaperua.databinding.ItemResponsavelBinding

class ResponsavelAdapter(
    private val onEditClicked: (Responsavel) -> Unit,
    private val onDeleteClicked: (Responsavel) -> Unit
) : ListAdapter<Responsavel, ResponsavelAdapter.ResponsavelViewHolder>(ResponsavelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponsavelViewHolder {
        val binding = ItemResponsavelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResponsavelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResponsavelViewHolder, position: Int) {
        val responsavel = getItem(position)
        holder.bind(responsavel, onEditClicked, onDeleteClicked)
    }

    class ResponsavelViewHolder(private val binding: ItemResponsavelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(responsavel: Responsavel, onEditClicked: (Responsavel) -> Unit, onDeleteClicked: (Responsavel) -> Unit) {
            binding.textViewNomeResponsavelItem.text = responsavel.nome
            binding.textViewTelefoneResponsavelItem.text = responsavel.telefone
            binding.textViewEmailResponsavelItem.text = responsavel.email
            binding.textViewEnderecoResponsavelItem.text = "${responsavel.logradouro ?: ""}, ${responsavel.numero ?: ""}"

            binding.buttonEditResponsavel.setOnClickListener { onEditClicked(responsavel) }
            binding.buttonDeleteResponsavel.setOnClickListener { onDeleteClicked(responsavel) }
        }
    }
}

class ResponsavelDiffCallback : DiffUtil.ItemCallback<Responsavel>() {
    override fun areItemsTheSame(oldItem: Responsavel, newItem: Responsavel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Responsavel, newItem: Responsavel): Boolean {
        return oldItem == newItem
    }
}
