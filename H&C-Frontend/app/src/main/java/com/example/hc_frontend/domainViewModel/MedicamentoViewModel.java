package com.example.hc_frontend.domainviewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hc_frontend.controller.MedicamentoController;
import com.example.hc_frontend.domain.Medicamento;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MedicamentoViewModel extends ViewModel {

    private MutableLiveData<Medicamento> medicamento;
    private MutableLiveData<List<Medicamento>> listaMedicamentos;
    private MutableLiveData<Boolean> medicamentoCadastrado;
    private MedicamentoController repository;
    private Set<Long> medicamentosExcluidosIds = new HashSet<>();

    public MedicamentoViewModel() {
        medicamento = new MutableLiveData<>();
        listaMedicamentos = new MutableLiveData<>();
        medicamentoCadastrado = new MutableLiveData<>();
        repository = new MedicamentoController();
    }


    public LiveData<Medicamento> getMedicamento() {
        return medicamento;
    }

    public void searchMedicamentoByName(String name) {
        repository.getMedicamentoByName(name).observeForever(result -> {
            if (result != null) {
                medicamento.setValue(result);
            } else {
                // Log de erro para debugging
                Log.d("MedicamentoViewModel", "Medicamento não encontrado com o nome: " + name);
            }
        });
    }

    // Método para cadastrar um medicamento
    public LiveData<Boolean> getMedicamentoCadastrado() {
        return medicamentoCadastrado;
    }

    public void cadastrarMedicamento(Medicamento medicamento) {
        repository.addMedicamento(medicamento).observeForever(isCadastrado -> {
            if (isCadastrado != null && isCadastrado) {
                medicamentoCadastrado.setValue(true);
                Log.d("MedicamentoViewModel", "Medicamento cadastrado com sucesso.");
            } else {
                medicamentoCadastrado.setValue(false);
                Log.d("MedicamentoViewModel", "Falha ao cadastrar o medicamento.");
            }
        });
    }

    // Método para listar todos os medicamentos
    public LiveData<List<Medicamento>> getListaMedicamentos() {
        return listaMedicamentos;
    }

    public void listarMedicamentos() {
        // Esta chamada agora observa o LiveData no repository
        repository.getAllMedicamentos().observeForever(medicamentos -> {
            if (medicamentos != null) {
                listaMedicamentos.setValue(medicamentos);
                Log.d("MedicamentoViewModel", "Medicamentos retornados: " + medicamentos.size());
            } else {
                Log.d("MedicamentoViewModel", "Nenhum medicamento retornado.");
            }
        });
    }

    // Método para atualizar um medicamento
    public LiveData<Medicamento> updateMedicamento(Long id, Medicamento medicamento) {
        MutableLiveData<Medicamento> updatedMedicamentoData = repository.updateMedicamento(id, medicamento);

        updatedMedicamentoData.observeForever(updatedMedicamento -> {
            if (updatedMedicamento != null) {
                this.medicamento.setValue(updatedMedicamento); // Atualiza o LiveData local
                Log.d("MedicamentoViewModel", "Medicamento atualizado com sucesso.");
            } else {
                Log.d("MedicamentoViewModel", "Falha ao atualizar o medicamento.");
            }
        });

        return updatedMedicamentoData;
    }

    // Método para excluir um medicamento
    public LiveData<Boolean> deleteMedicamento(Long id) {
        MutableLiveData<Boolean> resultData = repository.deleteMedicamento(id);
        resultData.observeForever(success -> {
            if (success != null && success) {
                Log.d("MedicamentoViewModel", "Medicamento excluído com sucesso.");
            } else {
                Log.d("MedicamentoViewModel", "Falha ao excluir o medicamento.");
            }
        });
        return resultData;
    }

    public void listarMedicamentosPorUsuario(Long usuarioId) {
        repository.getMedicamentosByUsuario(usuarioId).observeForever(medicamentos -> {
            if (medicamentos != null) {
                listaMedicamentos.setValue(medicamentos);
                Log.d("MedicamentoViewModel", "Medicamentos ativos retornados: " + medicamentos.size());
            } else {
                listaMedicamentos.setValue(new ArrayList<>());
                Log.d("MedicamentoViewModel", "Nenhum medicamento retornado.");
            }
        });
    }
}
