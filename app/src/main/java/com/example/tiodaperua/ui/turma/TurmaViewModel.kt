package com.example.tiodaperua.ui.turma

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiodaperua.data.Turma // Importa a classe correta

class TurmaViewModel : ViewModel() {

    private val _allTurmas = MutableLiveData<List<Turma>>(emptyList())
    val allTurmas: LiveData<List<Turma>> = _allTurmas

    fun insert(turma: Turma) {
        val listaAtual = _allTurmas.value?.toMutableList() ?: mutableListOf()
        listaAtual.add(turma)
        _allTurmas.value = listaAtual
    }

    fun update(turma: Turma) {
        val listaAtual = _allTurmas.value?.toMutableList() ?: return
        val index = listaAtual.indexOfFirst { it.id == turma.id }
        if (index != -1) {
            listaAtual[index] = turma
            _allTurmas.value = listaAtual
        }
    }

    fun delete(turma: Turma) {
        val listaAtual = _allTurmas.value?.toMutableList() ?: return
        listaAtual.remove(turma)
        _allTurmas.value = listaAtual
    }
}
