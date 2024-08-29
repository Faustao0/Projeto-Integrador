package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Paciente;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PacienteRepository {

    @GET("/pacientes/nome/{name}")
    Call<Paciente> getPacienteByName(@Path("name") String name);

    @GET("/pacientes/{id}")
    Call<Paciente> getPacienteById(@Path("id") Long id);
}