package com.example.hc_frontend.controller;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.repositories.PacienteRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PacienteController {

    private static final String TAG = "PacienteController";
    private PacienteRepository pacienteApi;

    public PacienteController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.pacienteApi = retrofit.create(PacienteRepository.class);
    }

    public MutableLiveData<Paciente> getPatientByName(String name) {
        MutableLiveData<Paciente> patientData = new MutableLiveData<>();

        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Nome do paciente não pode ser nulo ou vazio.");
            patientData.setValue(null);
            return patientData;
        }

        Log.d(TAG, "Iniciando chamada à API para buscar o paciente com nome: " + name);

        pacienteApi.getPacienteByName(name).enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Paciente encontrado: " + response.body().toString());
                    patientData.postValue(response.body());
                } else {
                    Log.d(TAG, "Nenhum paciente encontrado ou resposta inválida. Código: " + response.code());
                    patientData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API: " + t.getMessage());
                patientData.postValue(null);
            }
        });

        return patientData;
    }

    public MutableLiveData<Paciente> getPatientById(Long id) {
        MutableLiveData<Paciente> patientData = new MutableLiveData<>();
        pacienteApi.getPacienteById(id).enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientData.setValue(response.body());
                } else {
                    patientData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                patientData.setValue(null);
            }
        });
        return patientData;
    }
}
