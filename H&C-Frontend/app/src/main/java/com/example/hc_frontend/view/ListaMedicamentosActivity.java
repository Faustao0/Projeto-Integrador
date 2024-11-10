package com.example.hc_frontend.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.adapters.MedicamentoAdapter;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.domainviewmodel.MedicamentoViewModel;
import com.example.hc_frontend.notifications.AlarmHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaMedicamentosActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ATUALIZAR = 1;
    private RecyclerView recyclerView;
    private FloatingActionButton btnCadastroMedicamento;
    private MedicamentoAdapter adapter;
    private MedicamentoViewModel medicamentoViewModel;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private Usuario usuario;
    private Paciente paciente;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_medicamentos);

        // Verificar permissões para notificações (Android 13+)
        checkNotificationPermission();

        // Recebe o objeto Usuario vindo de qualquer atividade anterior
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("usuario")) {
            usuario = (Usuario) intent.getSerializableExtra("usuario");
        }

        // Verificação para evitar o redirecionamento em caso de usuário null
        if (usuario == null) {
            Toast.makeText(this, "Usuário não encontrado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Verifique se o usuário possui pacientes vinculados
        if (usuario.getPacientes() != null && !usuario.getPacientes().isEmpty()) {
            // Se o usuário possui pelo menos um paciente, obtém o primeiro da lista
            paciente = usuario.getPacientes().get(0);
        }

        if (paciente == null) {
            Toast.makeText(this, "Paciente não encontrado. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycler_view_medicamentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnCadastroMedicamento = findViewById(R.id.btnCadastroMedicamento);

        // Configurando o botão para cadastro de medicamento
        btnCadastroMedicamento.setOnClickListener(view -> {
            // Redireciona para a tela de cadastro de medicamentos e passa o usuário e paciente
            Intent intentCadastroMedicamento = new Intent(ListaMedicamentosActivity.this, TelaMedicamentosActivity.class);
            intentCadastroMedicamento.putExtra("usuario", usuario);
            intentCadastroMedicamento.putExtra("paciente", paciente);
            startActivityForResult(intentCadastroMedicamento, REQUEST_CODE_ATUALIZAR);
        });

        // Inicializa o adapter com uma lista vazia, passando Paciente e Usuario
        adapter = new MedicamentoAdapter(new ArrayList<>(), paciente, usuario, this);
        recyclerView.setAdapter(adapter);

        // Inicializa o ViewModel
        medicamentoViewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        carregarMedicamentosDoUsuario();

        // Observa as mudanças na lista de medicamentos e atualiza o RecyclerView
        medicamentoViewModel.getListaMedicamentos().observe(this, newMedicamentos -> {
            if (newMedicamentos != null) {
                Log.d("ListaMedicamentos", "Medicamentos recebidos: " + newMedicamentos.size());
                adapter.setMedicamentos(newMedicamentos);
                // Agende notificações para cada medicamento
                for (Medicamento medicamento : newMedicamentos) {
                    AlarmHelper.setAlarm(this, medicamento, usuario);
                }
            } else {
                Log.d("ListaMedicamentos", "Nenhum medicamento recebido.");
            }
        });

        // Carrega os medicamentos para o usuário atual
        medicamentoViewModel.listarMedicamentosPorUsuario(usuario.getId());

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
            Intent intentLogo = new Intent(ListaMedicamentosActivity.this, MenuActivity.class);
            intentLogo.putExtra("usuario", (Serializable) usuario);
            startActivity(intentLogo);
        });

        // Configura o listener para os itens do NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Intent intentProfile = new Intent(ListaMedicamentosActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    Intent intentPaciente = new Intent(ListaMedicamentosActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    Intent intentConsultas = new Intent(ListaMedicamentosActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    Intent intentMedicamentos = new Intent(ListaMedicamentosActivity.this, ListaMedicamentosActivity.class);
                    intentMedicamentos.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentMedicamentos);
                    return true;

                case R.id.nav_sair:
                    Intent intentSair = new Intent(ListaMedicamentosActivity.this, MainActivity.class);
                    startActivity(intentSair);
                    return true;

                default:
                    return false;
            }
        });
    }

    // Método para verificar a permissão de notificação (Android 13+)
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void carregarMedicamentosDoUsuario() {
        if (usuario != null && usuario.getId() != null) {
            medicamentoViewModel.listarMedicamentosPorUsuario(usuario.getId());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
