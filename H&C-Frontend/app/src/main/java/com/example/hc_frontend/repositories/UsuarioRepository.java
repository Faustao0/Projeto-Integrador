package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioRepository {

    @GET("/usuarios/login")
    Call<Usuario> login(@Query("email") String email, @Query("senha") String senha);

    @GET("usuarios/{id}")
    Call<Usuario> getUsuarioById(@Path("id") Long id);

    @PUT("/usuarios/{id}")
    Call<Usuario> atualizarUsuario(@Path("id") Long id, @Body Usuario usuario);

    @POST("/usuarios")
    Call<Usuario> registrarUsuario(@Body Usuario usuario);
}