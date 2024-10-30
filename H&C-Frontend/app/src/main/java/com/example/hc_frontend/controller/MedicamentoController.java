package com.example.hc_frontend.controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.repositories.MedicamentoRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicamentoController {

    private static final String TAG = "MedicamentoController";
    private MedicamentoRepository medicamentoApi;

    public MedicamentoController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Backend local no emulador
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.medicamentoApi = retrofit.create(MedicamentoRepository.class);
    }

    // Busca um medicamento pelo nome
    public MutableLiveData<Medicamento> getMedicamentoByName(String name) {
        MutableLiveData<Medicamento> medicamentoData = new MutableLiveData<>();

        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Nome do medicamento não pode ser nulo ou vazio.");
            medicamentoData.setValue(null);
            return medicamentoData;
        }

        Log.d(TAG, "Iniciando chamada à API para buscar o medicamento com nome: " + name);

        medicamentoApi.getMedicamentoByName(name).enqueue(new Callback<Medicamento>() {
            @Override
            public void onResponse(Call<Medicamento> call, Response<Medicamento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Medicamento encontrado: " + response.body().toString());
                    medicamentoData.postValue(response.body());
                } else {
                    Log.d(TAG, "Nenhum medicamento encontrado ou resposta inválida. Código: " + response.code());
                    medicamentoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Medicamento> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API: " + t.getMessage());
                medicamentoData.postValue(null);
            }
        });

        return medicamentoData;
    }

    // Cadastrar um novo medicamento
    public MutableLiveData<Boolean> addMedicamento(Medicamento medicamento) {
        MutableLiveData<Boolean> resultData = new MutableLiveData<>();

        medicamentoApi.addMedicamento(medicamento).enqueue(new Callback<Void>() { // Mudei aqui de Medicamento para Void
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Medicamento cadastrado com sucesso.");
                    resultData.postValue(true);
                } else {
                    Log.e(TAG, "Falha ao cadastrar medicamento. Código: " + response.code());
                    resultData.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API ao cadastrar medicamento: " + t.getMessage());
                resultData.postValue(false);
            }
        });

        return resultData;
    }


    // Listar todos os medicamentos
    public MutableLiveData<List<Medicamento>> getAllMedicamentos() {
        MutableLiveData<List<Medicamento>> listaMedicamentos = new MutableLiveData<>();

        medicamentoApi.getAllMedicamentos().enqueue(new Callback<List<Medicamento>>() {
            @Override
            public void onResponse(Call<List<Medicamento>> call, Response<List<Medicamento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Lista de medicamentos recebida com sucesso.");
                    listaMedicamentos.postValue(response.body());
                } else {
                    Log.e(TAG, "Falha ao recuperar a lista de medicamentos. Código: " + response.code());
                    listaMedicamentos.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Medicamento>> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API ao recuperar medicamentos: " + t.getMessage());
                listaMedicamentos.postValue(null);
            }
        });

        return listaMedicamentos;
    }

    public MutableLiveData<Medicamento> updateMedicamento(Long id, Medicamento medicamento) {
        MutableLiveData<Medicamento> updatedMedicamentoData = new MutableLiveData<>();

        medicamentoApi.updateMedicamento(id, medicamento).enqueue(new Callback<Medicamento>() {
            @Override
            public void onResponse(Call<Medicamento> call, Response<Medicamento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatedMedicamentoData.postValue(response.body());
                } else {
                    updatedMedicamentoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Medicamento> call, Throwable t) {
                updatedMedicamentoData.postValue(null);
            }
        });

        return updatedMedicamentoData;
    }

    public MutableLiveData<Boolean> deleteMedicamento(Long id) {
        MutableLiveData<Boolean> resultData = new MutableLiveData<>();

        medicamentoApi.deleteMedicamento(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                resultData.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultData.postValue(false);
            }
        });

        return resultData;
    }

    public LiveData<List<Medicamento>> getMedicamentosByUsuario(Long usuarioId) {
        MutableLiveData<List<Medicamento>> medicamentos = new MutableLiveData<>();

        medicamentoApi.getMedicamentosByUsuario(usuarioId).enqueue(new Callback<List<Medicamento>>() {
            @Override
            public void onResponse(Call<List<Medicamento>> call, Response<List<Medicamento>> response) {
                if (response.isSuccessful()) {
                    medicamentos.setValue(response.body());
                } else {
                    medicamentos.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Medicamento>> call, Throwable t) {
                medicamentos.setValue(null);
            }
        });

        return medicamentos;
    }
}
