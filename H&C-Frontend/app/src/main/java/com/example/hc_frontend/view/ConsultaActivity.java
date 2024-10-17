package com.example.hc_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.adapters.ConsultaAdapter;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.UsuarioRepository;

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

        // Recuperar dados do usuário passado na Intent
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        // Inicializar views
        recyclerViewConsultas = findViewById(R.id.recyclerViewConsultas);
        recyclerViewConsultas.setLayoutManager(new LinearLayoutManager(this));
        tvNenhumaConsulta = findViewById(R.id.tvNenhumaConsulta);
        btnMarcarConsulta = findViewById(R.id.btnMarcarConsulta);

        // Configura o botão para marcar consulta
        btnMarcarConsulta.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultaActivity.this, MarcarConsultaActivity.class);
            intent.putExtra("usuario", usuario);
            startActivityForResult(intent, REQUEST_CODE_MARCAR_CONSULTA); // Para capturar o resultado
        });

        // Carregar dados das consultas
        carregarDadosConsulta();
    }

    private void carregarDadosConsulta() {
        if (usuario != null && usuario.getConsultas() != null && !usuario.getConsultas().isEmpty()) {
            List<Consulta> consultas = usuario.getConsultas();  // Recupera a lista de consultas

            // Cria e configura o Adapter, passando as consultas e o Listener
            consultaAdapter = new ConsultaAdapter(consultas, new ConsultaAdapter.OnConsultaClickListener() {
                @Override
                public void onCancelarClick(Consulta consulta) {
                    mostrarConfirmacaoCancelarConsulta(consulta);
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

    private void mostrarConfirmacaoCancelarConsulta(Consulta consulta) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelamento de Consulta")
                .setMessage("Tem certeza que deseja cancelar a consulta?")
                .setPositiveButton("Sim", (dialog, which) -> cancelarConsulta(consulta))
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void cancelarConsulta(Consulta consulta) {
        usuario.getConsultas().remove(consulta);

        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConsultaActivity.this, "Consulta desvinculada com sucesso", Toast.LENGTH_SHORT).show();
                    consultaAdapter.notifyDataSetChanged();
                    carregarDadosConsulta();  // Atualiza a lista de consultas
                } else {
                    Toast.makeText(ConsultaActivity.this, "Erro ao atualizar usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ConsultaActivity.this, "Erro ao atualizar usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para reagendamento da consulta
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MARCAR_CONSULTA && resultCode == RESULT_OK) {
            if (data != null) {
                // Atualiza o usuário com os dados retornados da MarcarConsultaActivity
                usuario = (Usuario) data.getSerializableExtra("usuario_atualizado");
                carregarDadosConsulta(); // Recarrega as consultas atualizadas
            }
        }
    }
}