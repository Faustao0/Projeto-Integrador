package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Paciente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PacienteRepository {

    @GET("/pacientes/nome/{name}")
    Call<Paciente> getPacienteByName(@Path("name") String name);

    @GET("pacientes/usuario/{id}")
    Call<List<Paciente>> buscarPacientes(@Path("id") Long idUsuario);

    @GET("/pacientes/{id}")
    Call<Paciente> getPacienteById(@Path("id") Long id);

    @POST("/pacientes")
    Call<Paciente> createPaciente(@Body Paciente paciente);

    @PUT("/pacientes/{id}")
    Call<Paciente> updatePaciente(@Path("id") Long id, @Body Paciente pacienteDetails);
}