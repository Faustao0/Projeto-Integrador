package com.example.hc_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelaUsuarioActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ATUALIZAR = 1;

    private TextView tvNome, tvTelefone, tvEmail, tvCpf, tvRua, tvNumero, tvCidade, tvEstado, tvCep, tvBairro;
    private Button btnAtualizar;
    private UsuarioRepository usuarioRepository;
    private Usuario usuario;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_usuario);

        // Configura a Toolbar e o DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configura o NavigationView para o menu lateral
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Obtém o header do NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Referencia o TextView no header e configura a mensagem de boas-vindas
        TextView welcomeMessage = headerView.findViewById(R.id.welcomeMessage);
        if (usuario != null && usuario.getNome() != null) {
            String mensagem = "Bem-vindo, " + usuario.getNome() + "!";
            welcomeMessage.setText(mensagem);
        }

        // Inicializar as views
        tvNome = findViewById(R.id.tvNome);
        tvTelefone = findViewById(R.id.tvTelefone);
        tvEmail = findViewById(R.id.tvEmail);
        tvCpf = findViewById(R.id.tvCpf);
        tvRua = findViewById(R.id.tvRua);
        tvNumero = findViewById(R.id.tvNumero);
        tvCidade = findViewById(R.id.tvCidade);
        tvEstado = findViewById(R.id.tvEstado);
        tvCep = findViewById(R.id.tvCep);
        tvBairro = findViewById(R.id.tvBairro);
        btnAtualizar = findViewById(R.id.btnAtualizar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioRepository = retrofit.create(UsuarioRepository.class);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        btnAtualizar.setOnClickListener(view -> {
            Intent intent = new Intent(TelaUsuarioActivity.this, AtualizarUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivityForResult(intent, REQUEST_CODE_ATUALIZAR);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    // Abrir tela de perfil
                    Intent intentProfile = new Intent(TelaUsuarioActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    // Abrir tela de paciente
                    Intent intentPaciente = new Intent(TelaUsuarioActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    // Abrir tela de consultas
                    Intent intentConsultas = new Intent(TelaUsuarioActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    // Abrir tela de medicamentos
                    Intent intentMedicamentos = new Intent(TelaUsuarioActivity.this, TelaMedicamentosActivity.class);
                    startActivity(intentMedicamentos);
                    return true;

                default:
                    return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (usuario != null) {
            atualizarInformacoesUsuario(usuario.getId());
        } else {
            Toast.makeText(this, "Erro ao receber os dados do usuário", Toast.LENGTH_SHORT).show();
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void atualizarInformacoesUsuario(Long usuarioId) {
        Call<Usuario> call = usuarioRepository.getUsuarioById(usuarioId);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    usuario = response.body();
                    populateUserInfo(usuario);
                } else {
                    Toast.makeText(TelaUsuarioActivity.this, "Falha ao carregar informações do usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(TelaUsuarioActivity.this, "Erro ao comunicar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUserInfo(Usuario usuario) {
        tvNome.setText("Nome: " + usuario.getNome());
        tvTelefone.setText("Telefone: " + usuario.getTelefone());
        tvEmail.setText("Email: " + usuario.getEmail());
        tvCpf.setText("CPF: " + usuario.getCpf());

        if (usuario.getEndereco() != null && !usuario.getEndereco().isEmpty()) {
            Endereco endereco = usuario.getEndereco().get(0);
            tvRua.setText("Rua: " + endereco.getRua());
            tvNumero.setText("Número: " + endereco.getNumero());
            tvCidade.setText("Cidade: " + endereco.getCidade());
            tvEstado.setText("Estado: " + endereco.getEstado());
            tvCep.setText("CEP: " + endereco.getCep());
            tvBairro.setText("Bairro: " + endereco.getBairro());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ATUALIZAR && resultCode == RESULT_OK) {
            Usuario usuarioAtualizado = (Usuario) data.getSerializableExtra("usuario_atualizado");
            if (usuarioAtualizado != null) {
                usuario = usuarioAtualizado;
                populateUserInfo(usuario);
                Toast.makeText(this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}