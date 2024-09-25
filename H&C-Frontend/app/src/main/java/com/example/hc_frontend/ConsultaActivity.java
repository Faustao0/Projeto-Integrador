package com.example.hc_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medico;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.UsuarioRepository;

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
    private Button buttonCancelarConsulta, buttonReagendarConsulta;
    private ConsultaRepository consultaRepository;
    private UsuarioRepository usuarioRepository;
    private Long consultaId;  // ID da consulta atual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        consultaRepository = retrofit.create(ConsultaRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);
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
        buttonCancelarConsulta = findViewById(R.id.btnCancelarConsulta);
        buttonReagendarConsulta = findViewById(R.id.btnReagendarConsulta);

        carregarDadosConsulta();

        // Cancelar consulta (desvincular do usuário)
        buttonCancelarConsulta.setOnClickListener(v -> desvincularConsulta());

        // Reagendar consulta
        buttonReagendarConsulta.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultaActivity.this, MarcarConsultaActivity.class);
            intent.putExtra("consultaId", consultaId);
            intent.putExtra("usuario", usuario);
            startActivityForResult(intent, REQUEST_CODE_MARCAR_CONSULTA);
        });
    }

    // Carregar dados da consulta (caso exista)
    private void carregarDadosConsulta() {
        if (usuario != null && !usuario.getConsultas().isEmpty()) {
            Consulta consulta = usuario.getConsultas().get(0);
            consultaId = consulta.getId(); // Guardar o ID da consulta

            consultaRepository.getConsultaById(consultaId).enqueue(new Callback<Consulta>() {
                @Override
                public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                    if (response.isSuccessful()) {
                        Consulta consultaAtualizada = response.body();
                        atualizarUI(consultaAtualizada);
                    }
                }

                @Override
                public void onFailure(Call<Consulta> call, Throwable t) {
                }
            });
        } else {
            limparUI();
        }
    }

    private void limparUI() {
        tvDate.setText("Data: ");
        tvTime.setText("Hora: ");
        tvLocation.setText("Local: ");
        tvValor.setText("Valor: ");
        tvMedicoNome.setText("Nome: ");
        tvMedicoTelefone.setText("Telefone: ");
        tvMedicoEmail.setText("Email: ");
        tvMedicoCpf.setText("CPF: ");
        tvMedicoCrm.setText("CRM: ");
        tvMedicoEspecialidade.setText("Especialidade do médico: ");
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

    // Método para desvincular a consulta do usuário
    private void desvincularConsulta() {
        if (usuario.getConsultas() != null && !usuario.getConsultas().isEmpty()) {
            usuario.getConsultas().clear();  // Remover a consulta vinculada ao usuário
        }

        // Atualizar o usuário na API
        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConsultaActivity.this, "Consulta desvinculada com sucesso", Toast.LENGTH_SHORT).show();
                    usuario = response.body();

                    carregarDadosConsulta();
                } else {
                    Toast.makeText(ConsultaActivity.this, "Erro ao desvincular consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ConsultaActivity.this, "Erro de conexão ao desvincular consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Receber resultado do reagendamento
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MARCAR_CONSULTA && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("usuario_atualizado")) {
                usuario = (Usuario) data.getSerializableExtra("usuario_atualizado");
                carregarDadosConsulta();  // Atualizar com os dados reagendados
            }
        }
    }
}