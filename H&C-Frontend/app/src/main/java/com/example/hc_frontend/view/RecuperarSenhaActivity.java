package com.example.hc_frontend.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.repositories.UsuarioRepository;
import com.example.hc_frontend.domain.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private static final String TAG = "RecuperarSenhaActivity";
    private EditText etEmail;
    private Button btnBuscarSenha;
    private FloatingActionButton btnVoltarMenu;
    private TextView tv_senha;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        etEmail = findViewById(R.id.etEmail);
        tv_senha = findViewById(R.id.tv_senha);
        btnBuscarSenha = findViewById(R.id.btn_buscar_senha);
        btnVoltarMenu = findViewById(R.id.btn_voltar_menu);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioRepository = retrofit.create(UsuarioRepository.class);

        btnBuscarSenha.setOnClickListener(v -> buscarSenha());

        btnVoltarMenu.setOnClickListener(v -> finish());
    }

    private void buscarSenha() {
        String email = etEmail.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um e-mail.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Buscando senha para o e-mail: " + email);

        Call<Usuario> call = usuarioRepository.buscarSenhaPorEmail(email);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String senha = response.body().getSenha();
                    tv_senha.setText("Senha: " + senha);
                    tv_senha.setTextColor(getResources().getColor(R.color.black));
                    tv_senha.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Senha recuperada com sucesso: " + senha);
                } else {
                    tv_senha.setText("Email não existente no sistema");
                    tv_senha.setTextColor(getResources().getColor(R.color.red));
                    tv_senha.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Erro: Usuário não encontrado ou corpo de resposta vazio.");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RecuperarSenhaActivity.this, "Erro ao buscar senha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro ao chamar API: " + t.getMessage());
            }
        });
    }
}