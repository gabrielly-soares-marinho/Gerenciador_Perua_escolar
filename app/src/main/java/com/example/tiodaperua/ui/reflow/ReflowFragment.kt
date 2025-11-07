package com.example.tiodaperua.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tiodaperua.R
import com.example.tiodaperua.databinding.FragmentReflowBinding

class ReflowFragment : Fragment() {

    private var _binding: FragmentReflowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardAlunos.setOnClickListener {
            findNavController().navigate(R.id.action_nav_reflow_to_alunosFragment)
        }

        binding.cardResponsaveis.setOnClickListener {
            findNavController().navigate(R.id.action_nav_reflow_to_responsaveisFragment)
        }

        binding.cardTurma.setOnClickListener {
            findNavController().navigate(R.id.action_nav_reflow_to_turmaFragment)
        }

        binding.cardEscola.setOnClickListener {
            findNavController().navigate(R.id.action_nav_reflow_to_escolaFragment)
        }

        binding.cardCondutores.setOnClickListener {
            findNavController().navigate(R.id.action_nav_reflow_to_condutoresFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}