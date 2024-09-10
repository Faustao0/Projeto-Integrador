package com.example.hc_frontend.repositories;

import com.example.hc_frontend.domain.Usuario;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsuarioRepository {

    @GET("/usuarios/login")
    Call<Usuario> login(@Query("email") String email, @Query("senha") String senha);
}