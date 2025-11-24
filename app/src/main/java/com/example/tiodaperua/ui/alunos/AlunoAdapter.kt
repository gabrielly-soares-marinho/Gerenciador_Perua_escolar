package com.example.tiodaperua.ui.alunos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiodaperua.data.Aluno
import com.example.tiodaperua.databinding.ItemAlunoBinding

class AlunoAdapter(
    private val onEditClicked: (Aluno) -> Unit,
    private val onDeleteClicked: (Aluno) -> Unit
) : ListAdapter<Aluno, AlunoAdapter.AlunoViewHolder>(AlunoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunoViewHolder {
        val binding = ItemAlunoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlunoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlunoViewHolder, position: Int) {
        val aluno = getItem(position)
        holder.bind(aluno, onEditClicked, onDeleteClicked)
    }

    class AlunoViewHolder(private val binding: ItemAlunoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(aluno: Aluno, onEditClicked: (Aluno) -> Unit, onDeleteClicked: (Aluno) -> Unit) {
            binding.textViewNomeAlunoItem.text = aluno.nome
            binding.textViewEscolaItem.text = aluno.escola
            binding.textViewTurmaItem.text = aluno.turma
            binding.textViewEnderecoItem.text = "${aluno.logradouro ?: ""}, ${aluno.numero ?: ""}"

            binding.buttonEditAluno.setOnClickListener { onEditClicked(aluno) }
            binding.buttonDeleteAluno.setOnClickListener { onDeleteClicked(aluno) }
        }
    }
}

class AlunoDiffCallback : DiffUtil.ItemCallback<Aluno>() {
    override fun areItemsTheSame(oldItem: Aluno, newItem: Aluno): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Aluno, newItem: Aluno): Boolean {
        return oldItem == newItem
    }
}
