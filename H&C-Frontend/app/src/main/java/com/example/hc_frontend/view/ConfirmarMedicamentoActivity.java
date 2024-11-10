package com.example.hc_frontend.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.notifications.NotificationHelper;
import com.example.hc_frontend.repositories.MedicamentoRepository;
import com.example.hc_frontend.repositories.PacienteRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmarMedicamentoActivity extends AppCompatActivity {

    private TextView tvPerguntaMedicamento;
    private CheckBox cbConfirmarMedicamento;
    private Button btnVoltar;

    private PacienteRepository pacienteRepository;
    private MedicamentoRepository medicamentoRepository;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_medicamento);

        // Inicializando os componentes do layout
        tvPerguntaMedicamento = findViewById(R.id.tvPerguntaMedicamento);
        cbConfirmarMedicamento = findViewById(R.id.cbConfirmarMedicamento);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Configuração do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pacienteRepository = retrofit.create(PacienteRepository.class);
        medicamentoRepository = retrofit.create(MedicamentoRepository.class);

        Usuario usuario = (Usuario) getIntent().getSerializableExtra("usuario");
        String nomeMedicamento = getIntent().getStringExtra("nomeMedicamento");

        if (usuario != null && nomeMedicamento != null) {
            carregarDadosPacienteEMedicamento(usuario.getId(), nomeMedicamento);
        }

        cbConfirmarMedicamento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                NotificationHelper.cancelReNotification(ConfirmarMedicamentoActivity.this);
                Toast.makeText(this, "Medicamento confirmado como tomado", Toast.LENGTH_SHORT).show();
            }
        });

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void carregarDadosPacienteEMedicamento(Long usuarioId, String nomeMedicamento) {
        // Busca o primeiro paciente associado ao usuário
        pacienteRepository.buscarPacientes(usuarioId).enqueue(new Callback<List<Paciente>>() {
            @Override
            public void onResponse(Call<List<Paciente>> call, Response<List<Paciente>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Paciente paciente = response.body().get(0);
                    String nomePaciente = paciente.getNome();

                    medicamentoRepository.getMedicamentosByUsuario(usuarioId).enqueue(new Callback<List<Medicamento>>() {
                        @Override
                        public void onResponse(Call<List<Medicamento>> call, Response<List<Medicamento>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Medicamento medicamento = response.body().stream()
                                        .filter(m -> m.getNome().equals(nomeMedicamento))
                                        .findFirst()
                                        .orElse(null);

                                if (medicamento != null) {
                                    tvPerguntaMedicamento.setText("O paciente " + nomePaciente + " tomou o medicamento " + medicamento.getNome() + "?");
                                    cbConfirmarMedicamento.setText("Sim, o paciente " + nomePaciente + " tomou o medicamento " + medicamento.getNome());
                                } else {
                                    Toast.makeText(ConfirmarMedicamentoActivity.this, "Medicamento específico não encontrado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Medicamento>> call, Throwable t) {
                            Toast.makeText(ConfirmarMedicamentoActivity.this, "Erro ao carregar medicamentos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Paciente>> call, Throwable t) {
                Toast.makeText(ConfirmarMedicamentoActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}