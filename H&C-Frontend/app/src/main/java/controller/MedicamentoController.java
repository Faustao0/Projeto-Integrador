package controller;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import domain.Medicamento;
import repositories.MedicamentoInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicamentoController {

    private static final String TAG = "MedicamentoController";
    private MedicamentoInterface medicamentoApi;

    public MedicamentoController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.medicamentoApi = retrofit.create(MedicamentoInterface.class);
    }

    public MutableLiveData<Medicamento> getMedicamentoByName(String name) {
        MutableLiveData<Medicamento> medicamentoData = new MutableLiveData<>();

        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Nome do medicamento não pode ser nulo ou vazio.");
            medicamentoData.setValue(null);
            return medicamentoData;
        }

        Log.d(TAG, "Iniciando chamada à API para buscar o medicamento com nome: " + name);

        medicamentoApi.getMedicamentoByName(name).enqueue(new Callback<Medicamento>() {
            @Override
            public void onResponse(Call<Medicamento> call, Response<Medicamento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Medicamento encontrado: " + response.body().toString());
                    medicamentoData.postValue(response.body());
                } else {
                    Log.d(TAG, "Nenhum medicamento encontrado ou resposta inválida. Código: " + response.code());
                    medicamentoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Medicamento> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API: " + t.getMessage());
                medicamentoData.postValue(null);
            }
        });

        return medicamentoData;
    }
}
