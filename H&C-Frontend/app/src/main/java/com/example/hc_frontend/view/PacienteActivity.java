package com.example.hc_frontend.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.PacienteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PacienteActivity extends AppCompatActivity {

    private TextView tvNome, tvIdade, tvTelefone, tvEmail, tvCpf, tvRua, tvBairro, tvNumero, tvCidade, tvEstado, tvCep;
    private FloatingActionButton btnAtualizar;
    private static final int REQUEST_CODE_ATUALIZAR = 1;
    private PacienteRepository pacienteRepository;
    private Usuario usuario;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("MissingInflatedId")
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

        // Configura a Toolbar e o DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Recebendo o objeto Usuario passado via Intent
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

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

        // Tornar o Logo clicável e redirecionar para o Menu
        ImageView logoMenu = headerView.findViewById(R.id.LogoMenu);
        logoMenu.setOnClickListener(v -> {
            Intent intentLogo = new Intent(PacienteActivity.this, MenuActivity.class);
            intentLogo.putExtra("usuario", (Serializable) usuario);
            startActivity(intentLogo);
        });

        // Configura o listener para os itens do NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Intent intentProfile = new Intent(PacienteActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    Intent intentPaciente = new Intent(PacienteActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    Intent intentConsultas = new Intent(PacienteActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    Intent intentMedicamentos = new Intent(PacienteActivity.this, ListaMedicamentosActivity.class);
                    intentMedicamentos.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentMedicamentos);
                    return true;

                case R.id.nav_sair:
                    Intent intentSair = new Intent(PacienteActivity.this, MainActivity.class);
                    startActivity(intentSair);
                    return true;

                default:
                    return false;
            }
        });

        carregarPacientesVinculados();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recarregar os dados diretamente da API para garantir que estão atualizados
        carregarPacientesAtualizados();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

                        // Ajuste do ícone do FloatingActionButton de acordo com a existência de pacientes
                        if (pacientes.isEmpty()) {
                            btnAtualizar.setImageResource(R.drawable.baseline_add_24);
                            Toast.makeText(PacienteActivity.this, "Nenhum paciente encontrado", Toast.LENGTH_SHORT).show();
                        } else {
                            btnAtualizar.setImageResource(R.drawable.baseline_edit_24);
                            preencherDadosPaciente(pacientes.get(0));  // Exibir dados do primeiro paciente, por exemplo
                        }
                    } else {
                        Toast.makeText(PacienteActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
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
