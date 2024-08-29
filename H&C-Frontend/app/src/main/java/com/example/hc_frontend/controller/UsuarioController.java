package com.example.hc_frontend.controller;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioController {

    private static final String TAG = "UsuarioController";
    private UsuarioRepository usuarioApi;

    public UsuarioController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.usuarioApi = retrofit.create(UsuarioRepository.class);
    }

    public MutableLiveData<Usuario> login(String email, String senha) {
        MutableLiveData<Usuario> userData = new MutableLiveData<>();

        usuarioApi.login(email, senha).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Usuário autenticado: " + response.body().toString());
                    userData.postValue(response.body());
                } else {
                    Log.d(TAG, "Falha na autenticação: " + response.code());
                    userData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API: " + t.getMessage());
                userData.postValue(null);
            }
        });

        return userData;
    }
}