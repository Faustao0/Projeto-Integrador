package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Medicamento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MedicamentoRepository {

    @GET("/medicamentos/nome/{name}")
    Call<Medicamento> getMedicamentoByName(@Path("name") String name);

    @GET("/medicamentos/usuario/{usuarioId}")
    Call<List<Medicamento>> getMedicamentosByUsuario(@Path("usuarioId") Long usuarioId);

    @GET("/medicamentos")
    Call<List<Medicamento>> getAllMedicamentos();

    @POST("/medicamentos")
    Call<Void> addMedicamento(@Body Medicamento medicamento);

    @PUT("/medicamentos/{id}")
    Call<Medicamento> updateMedicamento(@Path("id") Long id, @Body Medicamento medicamento);

    @DELETE("/medicamentos/{id}")
    Call<Void> deleteMedicamento(@Path("id") Long id);
}