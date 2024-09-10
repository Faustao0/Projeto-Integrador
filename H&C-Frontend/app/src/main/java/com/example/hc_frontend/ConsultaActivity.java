package com.example.hc_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medico;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MARCAR_CONSULTA = 1;

    private Usuario usuario;
    private TextView tvDate, tvTime, tvLocation, tvValor;
    private TextView tvMedicoNome, tvMedicoTelefone, tvMedicoEmail, tvMedicoCpf, tvMedicoCrm, tvMedicoEspecialidade;
    private Button button2;
    private ConsultaRepository consultaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        // Inicializar Retrofit para buscar consultas atualizadas
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        consultaRepository = retrofit.create(ConsultaRepository.class);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        tvDate = findViewById(R.id.tvDateLabel);
        tvTime = findViewById(R.id.tvTimeLabel);
        tvLocation = findViewById(R.id.tvLocationLabel);
        tvValor = findViewById(R.id.tvValor);
        tvMedicoNome = findViewById(R.id.tvMedicoNomeLabel);
        tvMedicoTelefone = findViewById(R.id.tvMedicoTelefoneLabel);
        tvMedicoEmail = findViewById(R.id.tvMedicoEmailLabel);
        tvMedicoCpf = findViewById(R.id.tvMedicoCpfLabel);
        tvMedicoCrm = findViewById(R.id.tvMedicoCrmLabel);
        tvMedicoEspecialidade = findViewById(R.id.tvMedicoEspecialidadeLabel);
        button2 = findViewById(R.id.button2);

        // Se o usuário não tiver consultas, permitir criar uma nova
        carregarDadosConsulta();

        button2.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultaActivity.this, MarcarConsultaActivity.class);
            // Verificar se o usuário tem consultas. Se não, enviar -1 para criar uma nova consulta
            Long consultaId = (usuario != null && !usuario.getConsultas().isEmpty())
                    ? usuario.getConsultas().get(0).getId()
                    : -1L;  // Para novos usuários sem consulta
            intent.putExtra("consultaId", consultaId);
            startActivityForResult(intent, REQUEST_CODE_MARCAR_CONSULTA);
        });
    }

    // Carregar dados da consulta (caso exista)
    private void carregarDadosConsulta() {
        if (usuario != null && !usuario.getConsultas().isEmpty()) {
            Consulta consulta = usuario.getConsultas().get(0);
            consultaRepository.getConsultaById(consulta.getId()).enqueue(new Callback<Consulta>() {
                @Override
                public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                    if (response.isSuccessful()) {
                        Consulta consultaAtualizada = response.body();
                        atualizarUI(consultaAtualizada);  // Atualiza a UI com a consulta mais recente
                    }
                }

                @Override
                public void onFailure(Call<Consulta> call, Throwable t) {
                    // Tratar falha de conexão ou erro
                }
            });
        }
    }

    // Atualizar a interface com os dados da consulta
    private void atualizarUI(Consulta consulta) {
        tvDate.setText("Data: " + consulta.getData());
        tvTime.setText("Hora: " + consulta.getHora());
        tvLocation.setText("Local: " + consulta.getLocal());
        tvValor.setText("Valor: " + consulta.getValor().toString());

        if (!consulta.getMedicos().isEmpty()) {
            Medico medico = consulta.getMedicos().get(0);
            tvMedicoNome.setText("Nome: " + medico.getNome());
            tvMedicoTelefone.setText("Telefone: " + medico.getTelefone());
            tvMedicoEmail.setText("Email: " + medico.getEmail());
            tvMedicoCpf.setText("CPF: " + medico.getCpf());
            tvMedicoCrm.setText("CRM: " + medico.getCrm());
            tvMedicoEspecialidade.setText("Especialidade: " + medico.getEspecialidade());
        }
    }

    // Receber dados de retorno da MarcarConsultaActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MARCAR_CONSULTA && resultCode == RESULT_OK) {
            carregarDadosConsulta();  // Recarregar dados da consulta atualizados
        }
    }
}