package com.example.hc_frontend.controller;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import com.example.hc_frontend.adapters.LocalDateAdapter;
import com.example.hc_frontend.adapters.LocalTimeAdapter;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.time.LocalTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioController {

    private static final String TAG = "UsuarioController";
    private UsuarioRepository usuarioApi;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UsuarioController() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.usuarioApi = retrofit.create(UsuarioRepository.class);
    }

    public MutableLiveData<Usuario> login(String email, String senha) {
        MutableLiveData<Usuario> userData = new MutableLiveData<>();
        usuarioApi.login(email, senha).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData.postValue(response.body());
                } else {
                    userData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                userData.postValue(null);
            }
        });
        return userData;
    }
}