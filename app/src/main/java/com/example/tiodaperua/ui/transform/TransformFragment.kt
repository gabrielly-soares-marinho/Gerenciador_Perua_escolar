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
        return inflater.inflate(R.layout.fragment_transform, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alunosCountTextView = view.findViewById<TextView>(R.id.text_view_alunos_count)
        val condutoresCountTextView = view.findViewById<TextView>(R.id.text_view_condutores_count)

        viewLifecycleOwner.lifecycleScope.launch {
            db.alunoDao().getAllAlunos().collectLatest { alunos ->
                requireActivity().runOnUiThread {
                    alunosCountTextView.text = alunos.size.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.condutorDao().getAllCondutores().collectLatest { condutores ->
                requireActivity().runOnUiThread {
                    condutoresCountTextView.text = condutores.size.toString()
                }
            }
        }
    }
}
