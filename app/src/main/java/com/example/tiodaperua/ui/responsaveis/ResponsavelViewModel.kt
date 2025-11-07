package com.example.tiodaperua.ui.responsaveis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiodaperua.data.Responsavel // Importa a classe Responsavel correta

class ResponsavelViewModel : ViewModel() {

    private val _allResponsaveis = MutableLiveData<List<Responsavel>>(emptyList())
    val allResponsaveis: LiveData<List<Responsavel>> = _allResponsaveis

    private var proximoId = 1L

    fun insert(responsavel: Responsavel) {
        val listaAtual = _allResponsaveis.value?.toMutableList() ?: mutableListOf()
        // A linha abaixo pode precisar de ajuste se o seu ID não for Long ou se for gerado de outra forma
        // listaAtual.add(responsavel.copy(id = proximoId++))
        listaAtual.add(responsavel)
        _allResponsaveis.value = listaAtual
    }

    fun update(responsavel: Responsavel) {
        val listaAtual = _allResponsaveis.value?.toMutableList() ?: return
        val index = listaAtual.indexOfFirst { it.id == responsavel.id }
        if (index != -1) {
            listaAtual[index] = responsavel
            _allResponsaveis.value = listaAtual
        }
    }

    fun delete(responsavel: Responsavel) {
        val listaAtual = _allResponsaveis.value?.toMutableList() ?: return
        listaAtual.remove(responsavel)
        _allResponsaveis.value = listaAtual
    }
}
