package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Consulta;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConsultaRepository {

    @GET("consultas")
    Call<List<Consulta>> getConsultas();

    @GET("consultas/{id}")
    Call<Consulta> getConsultaById(@Path("id") Long id);

    @PUT("consultas/{id}")
    Call<Consulta> atualizarConsulta(@Path("id") Long id, @Body Consulta consulta);

    @POST("consultas")
    Call<Consulta> marcarConsulta(@Body Consulta consulta);

    @GET("/consultas/usuario/{usuarioId}")
    Call<List<Consulta>> getConsultasByUsuario(@Path("usuarioId") Long usuarioId);

    @GET("consultas/recentes/{usuarioId}")
    Call<Consulta> ConsultaMaisRecente(@Path("usuarioId") Long usuarioId);
}