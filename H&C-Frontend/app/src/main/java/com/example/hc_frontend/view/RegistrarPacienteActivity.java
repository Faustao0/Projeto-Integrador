package com.example.hc_frontend.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.PacienteRepository;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;

public class RegistrarPacienteActivity extends AppCompatActivity {

    private TextInputEditText etNome, etTelefone, etEmail, etCpf, etIdade;
    private TextInputEditText etRua, etBairro, etNumero, etCidade, etEstado, etCep;
    private Button btnSalvar;
    private PacienteRepository pacienteRepository;
    private Long pacienteId;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paciente);

        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etCpf = findViewById(R.id.etCpf);
        etIdade = findViewById(R.id.etIdade);

        etRua = findViewById(R.id.etRua);
        etBairro = findViewById(R.id.etBairro);
        etNumero = findViewById(R.id.etNumero);
        etCidade = findViewById(R.id.etCidade);
        etEstado = findViewById(R.id.etEstado);
        etCep = findViewById(R.id.etCep);
        btnSalvar = findViewById(R.id.btnSalvar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pacienteRepository = retrofit.create(PacienteRepository.class);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuario != null && usuario.getPacientes() != null && !usuario.getPacientes().isEmpty()) {
            carregarDadosPaciente(usuario.getPacientes().get(0).getId());
        } else {
            Toast.makeText(this, "Nenhum paciente vinculado ao usuário. Por favor, adicione um novo paciente.", Toast.LENGTH_SHORT).show();
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarSalvacaoDados();
            }
        });
    }

    private void confirmarSalvacaoDados() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar salvação de dados");
        builder.setMessage("Você realmente deseja salvar os dados inseridos?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (usuario.getPacientes() == null || usuario.getPacientes().isEmpty()) {
                    salvarNovoPaciente(); // Chama o método para criar novo paciente
                } else {
                    salvarDadosPaciente(); // Chama o método para atualizar paciente
                }
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void carregarDadosPaciente(Long pacienteIdAtual) {
        Call<Paciente> call = pacienteRepository.getPacienteById(pacienteIdAtual);
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (response.isSuccessful()) {
                    Paciente paciente = response.body();
                    if (paciente != null) {
                        pacienteId = paciente.getId();
                        preencherDadosPaciente(paciente);
                    }
                } else {
                    Toast.makeText(RegistrarPacienteActivity.this, "Falha ao carregar dados do paciente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                Toast.makeText(RegistrarPacienteActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherDadosPaciente(Paciente paciente) {
        etNome.setText(paciente.getNome());
        etIdade.setText(String.valueOf(paciente.getIdade()));
        etTelefone.setText(paciente.getTelefone());
        etEmail.setText(paciente.getEmail());
        etCpf.setText(paciente.getCpf());

        if (paciente.getEnderecos() != null && !paciente.getEnderecos().isEmpty()) {
            Endereco endereco = paciente.getEnderecos().get(0);
            etRua.setText(endereco.getRua());
            etBairro.setText(endereco.getBairro());
            etNumero.setText(endereco.getNumero());
            etCidade.setText(endereco.getCidade());
            etEstado.setText(endereco.getEstado());
            etCep.setText(endereco.getCep());
        }
    }

    private void salvarNovoPaciente() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String idadeStr = etIdade.getText().toString().trim();

        String rua = etRua.getText().toString().trim();
        String bairro = etBairro.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String cidade = etCidade.getText().toString().trim();
        String estado = etEstado.getText().toString().trim();
        String cep = etCep.getText().toString().trim();

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || cpf.isEmpty() || idadeStr.isEmpty() ||
                rua.isEmpty() || bairro.isEmpty() || numero.isEmpty() || cidade.isEmpty() || estado.isEmpty() || cep.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Idade inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Endereco endereco = new Endereco();
        endereco.setRua(rua);
        endereco.setBairro(bairro);
        endereco.setNumero(numero);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setCep(cep);

        ArrayList<Endereco> enderecos = new ArrayList<>();
        enderecos.add(endereco);

        Paciente paciente = new Paciente();
        paciente.setNome(nome);
        paciente.setIdade(idade);
        paciente.setTelefone(telefone);
        paciente.setEmail(email);
        paciente.setCpf(cpf);
        paciente.setEnderecos(enderecos);
        paciente.setUsuario(usuario); // Vincula o paciente ao usuário

        Call<Paciente> call = pacienteRepository.createPaciente(paciente);
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrarPacienteActivity.this, "Paciente criado com sucesso!", Toast.LENGTH_LONG).show();

                    Paciente novoPaciente = response.body();
                    usuario.getPacientes().add(novoPaciente);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("usuario_atualizado", usuario);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(RegistrarPacienteActivity.this, "Erro ao criar paciente", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                Toast.makeText(RegistrarPacienteActivity.this, "Erro de conexão ao criar paciente", Toast.LENGTH_LONG).show();
            }
        });
    }

        private void salvarDadosPaciente() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String idadeStr = etIdade.getText().toString().trim();

        String rua = etRua.getText().toString().trim();
        String bairro = etBairro.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String cidade = etCidade.getText().toString().trim();
        String estado = etEstado.getText().toString().trim();
        String cep = etCep.getText().toString().trim();

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || cpf.isEmpty() || idadeStr.isEmpty() ||
                rua.isEmpty() || bairro.isEmpty() || numero.isEmpty() || cidade.isEmpty() || estado.isEmpty() || cep.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Idade inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Endereco endereco = new Endereco();
        endereco.setRua(rua);
        endereco.setBairro(bairro);
        endereco.setNumero(numero);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setCep(cep);

        ArrayList<Endereco> enderecos = new ArrayList<>();
        enderecos.add(endereco);

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);
        paciente.setNome(nome);
        paciente.setIdade(idade);
        paciente.setTelefone(telefone);
        paciente.setEmail(email);
        paciente.setCpf(cpf);
        paciente.setEnderecos(enderecos);
        paciente.setUsuario(usuario);

            Call<Paciente> call = pacienteRepository.updatePaciente(pacienteId, paciente);
            call.enqueue(new Callback<Paciente>() {
                @Override
                public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistrarPacienteActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();

                        Paciente pacienteAtualizado = response.body();
                        for (int i = 0; i < usuario.getPacientes().size(); i++) {
                            if (usuario.getPacientes().get(i).getId().equals(pacienteAtualizado.getId())) {
                                usuario.getPacientes().set(i, pacienteAtualizado);
                                break;
                            }
                        }

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("usuario_atualizado", usuario);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(RegistrarPacienteActivity.this, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Paciente> call, Throwable t) {
                    Toast.makeText(RegistrarPacienteActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        }
}