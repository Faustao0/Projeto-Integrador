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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.adapters.ConsultaAdapter;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.UsuarioRepository;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MARCAR_CONSULTA = 1;

    private Usuario usuario;
    private ConsultaAdapter consultaAdapter;
    private RecyclerView recyclerViewConsultas;
    private ConsultaRepository consultaRepository;
    private UsuarioRepository usuarioRepository;
    private TextView tvNenhumaConsulta;
    private Button btnMarcarConsulta;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        consultaRepository = retrofit.create(ConsultaRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        recyclerViewConsultas = findViewById(R.id.recyclerViewConsultas);
        recyclerViewConsultas.setLayoutManager(new LinearLayoutManager(this));
        tvNenhumaConsulta = findViewById(R.id.tvNenhumaConsulta);
        btnMarcarConsulta = findViewById(R.id.btnMarcarConsulta);

        btnMarcarConsulta.setOnClickListener(v -> {
            if (usuario != null && usuario.getPacientes() != null && !usuario.getPacientes().isEmpty()) {
                Intent intent = new Intent(ConsultaActivity.this, MarcarConsultaActivity.class);
                intent.putExtra("usuario", usuario);
                startActivityForResult(intent, REQUEST_CODE_MARCAR_CONSULTA);
            } else {
                Toast.makeText(ConsultaActivity.this, "Não é possível marcar uma consulta pois o usuário não possui pacientes cadastrados.", Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        NavigationView navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);

        TextView welcomeMessage = headerView.findViewById(R.id.welcomeMessage);
        if (usuario != null && usuario.getNome() != null) {
            String mensagem = "Bem-vindo, " + usuario.getNome() + "!";
            welcomeMessage.setText(mensagem);
        }

        ImageView logoMenu = headerView.findViewById(R.id.LogoMenu);
        logoMenu.setOnClickListener(v -> {
            Intent intentLogo = new Intent(ConsultaActivity.this, MenuActivity.class);
            intentLogo.putExtra("usuario", (Serializable) usuario);
            startActivity(intentLogo);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Intent intentProfile = new Intent(ConsultaActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    Intent intentPaciente = new Intent(ConsultaActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    Intent intentConsultas = new Intent(ConsultaActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    Intent intentMedicamentos = new Intent(ConsultaActivity.this, ListaMedicamentosActivity.class);
                    intentMedicamentos.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentMedicamentos);
                    return true;

                case R.id.nav_sair:
                    Intent intentSair = new Intent(ConsultaActivity.this, MainActivity.class);
                    startActivity(intentSair);
                    return true;

                default:
                    return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosUsuarioAtualizado();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void carregarDadosUsuarioAtualizado() {
        if (usuario != null && usuario.getId() != null) {
            usuarioRepository.getUsuarioById(usuario.getId()).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        usuario = response.body();
                        carregarDadosConsulta();
                    } else {
                        Toast.makeText(ConsultaActivity.this, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(ConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void carregarDadosConsulta() {
        if (usuario != null && usuario.getConsultas() != null && !usuario.getConsultas().isEmpty()) {
            List<Consulta> consultas = usuario.getConsultas();

            consultaAdapter = new ConsultaAdapter(consultas, new ConsultaAdapter.OnConsultaClickListener() {
                @Override
                public void onCancelarClick(Consulta consulta) {
                    mostrarConfirmacaoDesvincularConsulta(consulta);
                }

                @Override
                public void onReagendarClick(Consulta consulta) {
                    Intent intent = new Intent(ConsultaActivity.this, MarcarConsultaActivity.class);
                    intent.putExtra("consultaId", consulta.getId());
                    intent.putExtra("usuario", usuario);
                    startActivityForResult(intent, REQUEST_CODE_MARCAR_CONSULTA);
                }
            });

            // Vincula o Adapter ao RecyclerView
            recyclerViewConsultas.setAdapter(consultaAdapter);

            // Ocultar mensagem e botão
            tvNenhumaConsulta.setVisibility(View.GONE);
            btnMarcarConsulta.setVisibility(View.GONE);
        } else {
            // Mostrar mensagem e botão se não houver consultas
            tvNenhumaConsulta.setVisibility(View.VISIBLE);
            btnMarcarConsulta.setVisibility(View.VISIBLE);
        }
    }

    // Mostrar confirmação para desvincular a consulta
    private void mostrarConfirmacaoDesvincularConsulta(Consulta consulta) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar a Consulta")
                .setMessage("Tem certeza que deseja cancelar esta consulta?")
                .setPositiveButton("Sim", (dialog, which) -> desvincularConsulta(consulta))
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void desvincularConsulta(Consulta consulta) {
        usuarioRepository.desvincularConsulta(usuario.getId(), consulta.getId()).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    usuario = response.body();
                    Toast.makeText(ConsultaActivity.this, "Consulta cancelada com sucesso", Toast.LENGTH_SHORT).show();
                    carregarDadosConsulta();

                    // Se não houver mais consultas, exibe a mensagem e o botão de marcar consulta
                    if (usuario.getConsultas().isEmpty()) {
                        tvNenhumaConsulta.setVisibility(View.VISIBLE);
                        btnMarcarConsulta.setVisibility(View.VISIBLE);
                        recyclerViewConsultas.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ConsultaActivity.this, "Erro ao cancelar a consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MARCAR_CONSULTA && resultCode == RESULT_OK) {
            carregarDadosUsuarioAtualizado();
        }
    }
}
