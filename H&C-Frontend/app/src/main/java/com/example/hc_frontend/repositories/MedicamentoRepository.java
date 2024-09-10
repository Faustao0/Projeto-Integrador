package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Medicamento;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MedicamentoRepository {

    @GET("/medicamentos/nome/{name}")
    Call<Medicamento> getMedicamentoByName(@Path("name") String name);
}