package com.example.tiodaperua.ui.alunos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiodaperua.data.Aluno // Importa a classe Aluno correta

class AlunoViewModel : ViewModel() {

    private val _allAlunos = MutableLiveData<List<Aluno>>(emptyList())
    val allAlunos: LiveData<List<Aluno>> = _allAlunos

    private var proximoId = 1L

    fun insert(aluno: Aluno) {
        val listaAtual = _allAlunos.value?.toMutableList() ?: mutableListOf()
        listaAtual.add(aluno.copy(id = proximoId++))
        _allAlunos.value = listaAtual
    }

    fun update(aluno: Aluno) {
        val listaAtual = _allAlunos.value?.toMutableList() ?: return
        val index = listaAtual.indexOfFirst { it.id == aluno.id }
        if (index != -1) {
            listaAtual[index] = aluno
            _allAlunos.value = listaAtual
        }
    }

    fun delete(aluno: Aluno) {
        val listaAtual = _allAlunos.value?.toMutableList() ?: return
        listaAtual.remove(aluno)
        _allAlunos.value = listaAtual
    }
}
