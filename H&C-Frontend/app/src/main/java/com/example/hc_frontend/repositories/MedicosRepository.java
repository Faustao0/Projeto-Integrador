package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Medico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MedicosRepository {

    @GET("medicos")
    Call<List<Medico>> getMedicos();
}
