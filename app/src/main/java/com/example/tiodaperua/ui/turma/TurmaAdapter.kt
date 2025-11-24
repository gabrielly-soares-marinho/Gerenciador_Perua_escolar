package com.example.tiodaperua.ui.turma

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiodaperua.data.Turma
import com.example.tiodaperua.databinding.ItemTurmaBinding

class TurmaAdapter(
    private val onEditClicked: (Turma) -> Unit,
    private val onDeleteClicked: (Turma) -> Unit
) : ListAdapter<Turma, TurmaAdapter.TurmaViewHolder>(TurmaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurmaViewHolder {
        val binding = ItemTurmaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TurmaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TurmaViewHolder, position: Int) {
        val turma = getItem(position)
        holder.bind(turma, onEditClicked, onDeleteClicked)
    }

    class TurmaViewHolder(private val binding: ItemTurmaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(turma: Turma, onEditClicked: (Turma) -> Unit, onDeleteClicked: (Turma) -> Unit) {
            binding.textViewNomeTurmaItem.text = turma.nome
            binding.textViewEscolaTurmaItem.text = turma.escola
            binding.textViewEnderecoTurmaItem.text = "${turma.logradouro ?: ""}, ${turma.numero ?: ""}"

            binding.buttonEditTurma.setOnClickListener { onEditClicked(turma) }
            binding.buttonDeleteTurma.setOnClickListener { onDeleteClicked(turma) }
        }
    }
}

class TurmaDiffCallback : DiffUtil.ItemCallback<Turma>() {
    override fun areItemsTheSame(oldItem: Turma, newItem: Turma): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Turma, newItem: Turma): Boolean {
        return oldItem == newItem
    }
}
