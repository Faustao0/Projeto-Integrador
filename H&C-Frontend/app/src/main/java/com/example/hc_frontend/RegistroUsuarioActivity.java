package com.example.hc_frontend;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText etNome, etTelefone, etEmail, etCpf, etSenha, etConfirmarSenha;
    private Button btnRegistrar;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        // Inicializar os campos de entrada
        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etCpf = findViewById(R.id.etCpf);
        etSenha = findViewById(R.id.etSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        usuarioRepository = retrofit.create(UsuarioRepository.class);

        // Lógica do botão de registrar
        btnRegistrar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String telefone = etTelefone.getText().toString();
            String email = etEmail.getText().toString();
            String cpf = etCpf.getText().toString();
            String senha = etSenha.getText().toString();
            String confirmarSenha = etConfirmarSenha.getText().toString();

            // Verificar se os campos estão preenchidos
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

            // Criar um novo usuário
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setTelefone(telefone);
            novoUsuario.setEmail(email);
            novoUsuario.setCpf(cpf);
            novoUsuario.setSenha(senha);

            // Desabilitar o botão enquanto a solicitação está sendo processada
            btnRegistrar.setEnabled(false);

            // Enviar os dados para a API
            usuarioRepository.registrarUsuario(novoUsuario).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistroUsuarioActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();  // Fechar a tela de registro
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
                    }
                    btnRegistrar.setEnabled(true);  // Reativar o botão
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
