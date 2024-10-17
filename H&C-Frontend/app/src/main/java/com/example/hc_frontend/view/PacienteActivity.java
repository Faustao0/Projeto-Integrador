package com.example.hc_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.PacienteRepository;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PacienteActivity extends AppCompatActivity {

    private TextView tvNome, tvIdade, tvTelefone, tvEmail, tvCpf, tvRua, tvBairro, tvNumero, tvCidade, tvEstado, tvCep;
    private Button btnAtualizar;
    private static final int REQUEST_CODE_ATUALIZAR = 1;
    private PacienteRepository pacienteRepository;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        // Inicializando as views
        tvNome = findViewById(R.id.tvNome);
        tvIdade = findViewById(R.id.tvIdade);
        tvTelefone = findViewById(R.id.tvTelefone);
        tvEmail = findViewById(R.id.tvEmail);
        tvCpf = findViewById(R.id.tvCpf);
        tvRua = findViewById(R.id.tvRua);
        tvBairro = findViewById(R.id.tvBairro);
        tvNumero = findViewById(R.id.tvNumero);
        tvCidade = findViewById(R.id.tvCidade);
        tvEstado = findViewById(R.id.tvEstado);
        tvCep = findViewById(R.id.tvCep);
        btnAtualizar = findViewById(R.id.btnAtualizar);

        // Inicializando Retrofit para API de pacientes
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base de API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pacienteRepository = retrofit.create(PacienteRepository.class);

        // Recebendo o objeto Usuario passado via Intent
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        // Carregando os pacientes vinculados ao usuário
        carregarPacientesVinculados();

        // Abrindo a tela de registro de paciente
        btnAtualizar.setOnClickListener(view -> {
            Intent intent = new Intent(PacienteActivity.this, RegistrarPacienteActivity.class);
            intent.putExtra("usuario", usuario);
            startActivityForResult(intent, REQUEST_CODE_ATUALIZAR);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recarregar os dados diretamente da API para garantir que estão atualizados
        carregarPacientesAtualizados();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ATUALIZAR && resultCode == RESULT_OK && data != null) {
            usuario = (Usuario) data.getSerializableExtra("usuario_atualizado");
            if (usuario != null) {
                carregarPacientesVinculados();
            }
        }
    }

    // Recarregar os pacientes da API
    private void carregarPacientesAtualizados() {
        pacienteRepository.buscarPacientes(usuario.getId()).enqueue(new Callback<List<Paciente>>() {
            @Override
            public void onResponse(Call<List<Paciente>> call, Response<List<Paciente>> response) {
                if (response.isSuccessful()) {
                    List<Paciente> pacientes = response.body();
                    if (pacientes != null && !pacientes.isEmpty()) {
                        usuario.setPacientes(pacientes);
                        carregarPacientesVinculados();
                    } else {
                        Toast.makeText(PacienteActivity.this, "Nenhum paciente encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PacienteActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Paciente>> call, Throwable t) {
                Toast.makeText(PacienteActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarPacientesVinculados() {
        if (usuario != null) {
            Call<List<Paciente>> call = pacienteRepository.buscarPacientes(usuario.getId());
            call.enqueue(new Callback<List<Paciente>>() {
                @Override
                public void onResponse(Call<List<Paciente>> call, Response<List<Paciente>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Paciente> pacientes = response.body();
                        for (Paciente paciente : pacientes) {
                            preencherDadosPaciente(paciente);
                        }
                    } else {
                        Toast.makeText(PacienteActivity.this, "Nenhum paciente encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Paciente>> call, Throwable t) {
                    Toast.makeText(PacienteActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void preencherDadosPaciente(Paciente paciente) {
        tvNome.setText("Nome: " + paciente.getNome());
        tvIdade.setText("Idade: " + paciente.getIdade());
        tvTelefone.setText("Telefone: " + paciente.getTelefone());
        tvEmail.setText("Email: " + paciente.getEmail());
        tvCpf.setText("CPF: " + paciente.getCpf());

        if (paciente.getEnderecos() != null && !paciente.getEnderecos().isEmpty()) {
            Endereco endereco = paciente.getEnderecos().get(0);
            tvRua.setText("Rua: " + endereco.getRua());
            tvBairro.setText("Bairro: " + endereco.getBairro());
            tvNumero.setText("Número: " + endereco.getNumero());
            tvCidade.setText("Cidade: " + endereco.getCidade());
            tvEstado.setText("Estado: " + endereco.getEstado());
            tvCep.setText("CEP: " + endereco.getCep());
        }
    }
}
