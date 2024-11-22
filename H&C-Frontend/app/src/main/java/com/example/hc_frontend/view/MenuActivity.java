package com.example.hc_frontend.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.adapters.MenuAdapter;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.MedicamentoRepository;
import com.example.hc_frontend.utils.SpaceItemDecoration;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "consulta_channel";
    private Usuario usuario;
    private ConsultaRepository consultaRepository;
    private MedicamentoRepository medicamentoRepository;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private List<Object> items = new ArrayList<>();
    private TextView tvNenhumaInfo;
    private Paciente paciente;
    private Set<String> uniqueMedicamentos = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvNenhumaInfo = findViewById(R.id.tvNenhumaInfo);
        recyclerView = findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        consultaRepository = retrofit.create(ConsultaRepository.class);
        medicamentoRepository = retrofit.create(MedicamentoRepository.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("usuario")) {
            usuario = (Usuario) intent.getSerializableExtra("usuario");
        }

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        ImageView logoMenu = headerView.findViewById(R.id.LogoMenu);
        logoMenu.setOnClickListener(v -> {
            Intent intentLogo = new Intent(MenuActivity.this, MenuActivity.class);
            intent.putExtra("usuario", (Serializable) usuario);
            startActivity(intent);
        });

        TextView welcomeMessage = headerView.findViewById(R.id.welcomeMessage);
        if (usuario != null && usuario.getNome() != null) {
            String mensagem = "Bem-vindo, " + usuario.getNome() + "!";
            welcomeMessage.setText(mensagem);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Intent intentProfile = new Intent(MenuActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    Intent intentPaciente = new Intent(MenuActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    Intent intentConsultas = new Intent(MenuActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    Intent intentMedicamentos = new Intent(MenuActivity.this, ListaMedicamentosActivity.class);
                    intentMedicamentos.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentMedicamentos);
                    return true;

                case R.id.nav_sair:
                    Intent intentSair = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(intentSair);
                    return true;

                default:
                    return false;
            }
        });

        carregarDados();

        createNotificationChannel();
    }

    private void carregarDados() {
        if (usuario == null || usuario.getId() == null) {
            Log.e("USER_ERROR", "Usuário não carregado corretamente");
            return;
        }

        if (usuario.getPacientes() != null && !usuario.getPacientes().isEmpty()) {
            paciente = usuario.getPacientes().get(0);
        }

        if (menuAdapter == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                menuAdapter = new MenuAdapter(items, paciente, this);
            }
            recyclerView.setAdapter(menuAdapter);
        }

        items.clear();
        uniqueMedicamentos.clear();

        carregarConsultaMaisProxima();
        carregarMedicamentos();
    }

    private void carregarConsultaMaisProxima() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            consultaRepository.getConsultasByUsuario(usuario.getId()).enqueue(new Callback<List<Consulta>>() {
                @Override
                public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Consulta> consultas = response.body();
                        LocalDateTime now = LocalDateTime.now();
                        Consulta consultaMaisProxima = null;

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault());

                        for (Consulta consulta : consultas) {
                            try {
                                LocalDate dataConsulta = LocalDate.parse(consulta.getData(), formatter);
                                LocalDateTime dataHoraConsulta = dataConsulta.atTime(0, 0);

                                // Identifica a consulta mais próxima
                                if (dataHoraConsulta.isAfter(now) &&
                                        (consultaMaisProxima == null || dataHoraConsulta.isBefore(LocalDate.parse(consultaMaisProxima.getData(), formatter).atTime(0, 0)))) {
                                    consultaMaisProxima = consulta;
                                }
                            } catch (DateTimeParseException e) {
                                Log.e("MenuActivity", "Erro ao converter a data da consulta: " + e.getMessage());
                            }
                        }

                        items.removeIf(item -> item instanceof Consulta);

                        if (consultaMaisProxima != null) {
                            items.add(0, consultaMaisProxima);
                            menuAdapter.notifyDataSetChanged();
                        }

                        verificarItens();
                    } else {
                        Log.e("API_ERROR", "Erro ao carregar consultas");
                    }
                }

                @Override
                public void onFailure(Call<List<Consulta>> call, Throwable t) {
                    Toast.makeText(MenuActivity.this, "Erro de conexão com a API", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void carregarMedicamentos() {
        medicamentoRepository.getMedicamentosByUsuario(usuario.getId()).enqueue(new Callback<List<Medicamento>>() {
            @Override
            public void onResponse(Call<List<Medicamento>> call, Response<List<Medicamento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Medicamento> medicamentos = response.body();

                    for (Medicamento medicamento : medicamentos) {
                        if (uniqueMedicamentos.add(medicamento.getNome())) {
                            items.add(medicamento);
                        }
                    }

                    menuAdapter.notifyDataSetChanged();
                    verificarItens();
                } else {
                    Log.e("API_ERROR", "Erro ao carregar medicamentos");
                }
            }

            @Override
            public void onFailure(Call<List<Medicamento>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Erro de conexão com a API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarItens() {
        if (items.isEmpty()) {
            tvNenhumaInfo.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNenhumaInfo.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ConsultaChannel";
            String description = "Canal para notificações de consultas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        recyclerView = findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
    }
}