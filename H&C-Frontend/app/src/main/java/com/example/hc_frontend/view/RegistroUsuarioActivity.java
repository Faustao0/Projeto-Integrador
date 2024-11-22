package com.example.hc_frontend.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText etNome, etTelefone, etEmail, etCpf, etSenha, etConfirmarSenha;
    private EditText etRua, etNumero, etBairro, etCidade, etEstado, etCep;
    private Button btnRegistrar;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_registro_usuario);

        // Inicializar os campos de usuário
        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etCpf = findViewById(R.id.etCpf);
        etSenha = findViewById(R.id.etSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);

        // Inicializar os campos de endereço
        etRua = findViewById(R.id.etRua);
        etNumero = findViewById(R.id.etNumero);
        etBairro = findViewById(R.id.etBairro);
        etCidade = findViewById(R.id.etCidade);
        etEstado = findViewById(R.id.etEstado);
        etCep = findViewById(R.id.etCep);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        usuarioRepository = retrofit.create(UsuarioRepository.class);

        btnRegistrar.setOnClickListener(v -> {
            // Capturar os dados do usuário
            String nome = etNome.getText().toString();
            String telefone = etTelefone.getText().toString();
            String email = etEmail.getText().toString();
            String cpf = etCpf.getText().toString();
            String senha = etSenha.getText().toString();
            String confirmarSenha = etConfirmarSenha.getText().toString();

            // Capturar os dados de endereço
            String rua = etRua.getText().toString();
            String numero = etNumero.getText().toString();
            String bairro = etBairro.getText().toString();
            String cidade = etCidade.getText().toString();
            String estado = etEstado.getText().toString().trim().toUpperCase();
            String cep = etCep.getText().toString();

            // Validação de dados
            if (TextUtils.isEmpty(nome)) {
                etNome.setError("Nome é obrigatório");
                return;
            }

            if (TextUtils.isEmpty(telefone)) {
                etTelefone.setError("Telefone é obrigatório");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email é obrigatório");
                return;
            }

            if (TextUtils.isEmpty(cpf)) {
                etCpf.setError("CPF é obrigatório");
                return;
            }

            if (TextUtils.isEmpty(senha)) {
                etSenha.setError("Senha é obrigatória");
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                etConfirmarSenha.setError("As senhas não coincidem");
                return;
            }

            if (TextUtils.isEmpty(rua)) {
                etRua.setError("A rua é obrigatória!");
                return;
            }

            if (TextUtils.isEmpty(numero)) {
                etNumero.setError("O número da casa é obrigatório!");
                return;
            }

            if (TextUtils.isEmpty(bairro)) {
                etBairro.setError("O bairro é obrigatório!");
                return;
            }

            if (TextUtils.isEmpty(cidade)) {
                etCidade.setError("A cidade é obrigatória!");
                return;
            }

            if (TextUtils.isEmpty(estado)) {
                etEstado.setError("O estado não pode estar vazio");
                return;
            }

            if (!estado.matches("^[A-Z]{2}$")) {
                etEstado.setError("Insira uma UF válida (Ex.: SP, RJ, etc.).");
                return;
            }

            if (TextUtils.isEmpty(cep)) {
                etCep.setError("O CEP é obrigatório!");
                return;
            }

            // Criar o objeto de endereço
            Endereco endereco = new Endereco();
            endereco.setRua(rua);
            endereco.setNumero(numero);
            endereco.setBairro(bairro);
            endereco.setCidade(cidade);
            endereco.setEstado(estado);
            endereco.setCep(cep);

            // Criar o objeto de usuário
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setTelefone(telefone);
            novoUsuario.setEmail(email);
            novoUsuario.setCpf(cpf);
            novoUsuario.setSenha(senha);

            // Associar o endereço ao usuário
            List<Endereco> enderecos = new ArrayList<>();
            enderecos.add(endereco);
            novoUsuario.setEndereco(enderecos);

            btnRegistrar.setEnabled(false);

            // Enviar os dados para a API
            usuarioRepository.registrarUsuario(novoUsuario).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistroUsuarioActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
                    }
                    btnRegistrar.setEnabled(true);
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(RegistroUsuarioActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                    btnRegistrar.setEnabled(true);  // Reativar o botão
                }
            });
        });
    }
}