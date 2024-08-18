package repositories;

import domain.Medicamento;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MedicamentoInterface {

    @GET("/medicamentos/nome/{name}")
    Call<Medicamento> getMedicamentoByName(@Path("name") String name);
}
