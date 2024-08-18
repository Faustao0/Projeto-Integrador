package com.example.hc_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

public class MenuActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnAppointments = findViewById(R.id.btnAppointments);
        ImageButton btnExams = findViewById(R.id.btnExams);
        ImageButton btnMedications = findViewById(R.id.btnMedications);
        ImageButton btnMessages = findViewById(R.id.btnMessages);

//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Perfil
//                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnAppointments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Consultas
//                Intent intent = new Intent(MenuActivity.this, AppointmentsActivity.class);
//                startActivity(intent);
//            }
//        });

//        btnExams.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Exames
//                Intent intent = new Intent(MenuActivity.this, ExamsActivity.class);
//                startActivity(intent);
//            }
//        });

        btnMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de Medicamentos
                Intent intent = new Intent(MenuActivity.this, TelaMedicamentosActivity.class);
                startActivity(intent);
            }
        });

//        btnMessages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Mensagens
//                Intent intent = new Intent(MenuActivity.this, MessagesActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}