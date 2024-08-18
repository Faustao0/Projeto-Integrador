package repositories;

import domain.Paciente;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PacienteInterface {

    @GET("/pacientes/nome/{name}")
    Call<Paciente> getPacienteByName(@Path("name") String name);

    @GET("/pacientes/{id}")
    Call<Paciente> getPacienteById(@Path("id") Long id);
}
