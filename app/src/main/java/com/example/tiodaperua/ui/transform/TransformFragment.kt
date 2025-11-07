package com.example.tiodaperua.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tiodaperua.R
import com.example.tiodaperua.data.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransformFragment : Fragment() {

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = AppDatabase.getDatabase(requireContext())
        // Infla o layout do dashboard que criamos
        return inflater.inflate(R.layout.fragment_transform, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alunosCountTextView = view.findViewById<TextView>(R.id.text_view_alunos_count)
        val condutoresCountTextView = view.findViewById<TextView>(R.id.text_view_condutores_count)

        // Busca e observa a contagem de alunos
        viewLifecycleOwner.lifecycleScope.launch {
            db.alunoDao().getAllAlunos().collectLatest { alunos ->
                // Garante que a atualização da UI seja na thread principal
                requireActivity().runOnUiThread {
                    alunosCountTextView.text = alunos.size.toString()
                }
            }
        }

        // Busca e observa a contagem de condutores
        viewLifecycleOwner.lifecycleScope.launch {
            db.condutorDao().getAllCondutores().collectLatest { condutores ->
                // Garante que a atualização da UI seja na thread principal
                requireActivity().runOnUiThread {
                    condutoresCountTextView.text = condutores.size.toString()
                }
            }
        }
    }
}
