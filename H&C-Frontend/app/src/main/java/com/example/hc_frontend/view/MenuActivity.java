package com.example.hc_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Usuario;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class MenuActivity extends AppCompatActivity {

    private Usuario usuario;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Configura a Toolbar e o DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Recebe o objeto Usuario da MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("usuario")) {
            usuario = (Usuario) intent.getSerializableExtra("usuario");
        }

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

        // Botões principais no layout
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnPaciente = findViewById(R.id.btnPaciente);
        ImageButton btnAppointments = findViewById(R.id.btnAppointments);
        ImageButton btnMedications = findViewById(R.id.btnMedications);

        // Configura os botões com suas respectivas funcionalidades
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TelaUsuarioActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
            }
        });

        btnPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PacienteActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        btnAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consultaIntent = new Intent(MenuActivity.this, ConsultaActivity.class);
                consultaIntent.putExtra("usuario", (Serializable) usuario);
                startActivity(consultaIntent);
            }
        });

        btnMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de Medicamentos
                Intent intent = new Intent(MenuActivity.this, TelaMedicamentosActivity.class);
                startActivity(intent);
            }
        });

        // Listener para os itens do NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    // Abrir tela de perfil
                    Intent intentProfile = new Intent(MenuActivity.this, TelaUsuarioActivity.class);
                    intentProfile.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentProfile);
                    return true;

                case R.id.nav_paciente:
                    // Abrir tela de paciente
                    Intent intentPaciente = new Intent(MenuActivity.this, PacienteActivity.class);
                    intentPaciente.putExtra("usuario", usuario);
                    startActivity(intentPaciente);
                    return true;

                case R.id.nav_consultas:
                    // Abrir tela de consultas
                    Intent intentConsultas = new Intent(MenuActivity.this, ConsultaActivity.class);
                    intentConsultas.putExtra("usuario", (Serializable) usuario);
                    startActivity(intentConsultas);
                    return true;

                case R.id.nav_medicamentos:
                    // Abrir tela de medicamentos
                    Intent intentMedicamentos = new Intent(MenuActivity.this, TelaMedicamentosActivity.class);
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
        // Fecha o Drawer sempre que a Activity for retomada
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}