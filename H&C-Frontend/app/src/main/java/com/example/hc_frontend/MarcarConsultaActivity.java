package com.example.hc_frontend;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medico;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.MedicosRepository;
import com.example.hc_frontend.repositories.UsuarioRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarcarConsultaActivity extends AppCompatActivity {

    private TextInputEditText etData, etHora, etValorConsulta;
    private Spinner spinnerMedicos;
    private Button buttonConfirmarAgendamento;
    private ProgressBar progressBar;
    private MedicosRepository medicosRepository;
    private ConsultaRepository consultaRepository;
    private UsuarioRepository usuarioRepository;
    private Long consultaId;
    private Usuario usuario;
    private List<Consulta> consultasDisponiveis;
    private TextView tvLocalConsulta;
    private Consulta consultaSelecionada;
    private Medico medicoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_consulta);

        etData = findViewById(R.id.etData);
        etHora = findViewById(R.id.etHora);
        etValorConsulta = findViewById(R.id.etValorConsulta);
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        tvLocalConsulta = findViewById(R.id.tvLocalConsulta);
        buttonConfirmarAgendamento = findViewById(R.id.buttonConfirmarAgendamento);
        progressBar = findViewById(R.id.progressBar);  // Progress bar

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // URL base da  API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicosRepository = retrofit.create(MedicosRepository.class);
        consultaRepository = retrofit.create(ConsultaRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);

        consultaId = getIntent().getLongExtra("consultaId", -1L);
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuario == null) {
            Toast.makeText(this, "Erro: Usuário não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        etData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MarcarConsultaActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        etData.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    MarcarConsultaActivity.this,
                    (view, hourOfDay, minute) -> etHora.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        carregarMedicos();

        buttonConfirmarAgendamento.setOnClickListener(v -> {
            validarEConfirmarAgendamento();
        });
    }

    private void carregarMedicos() {
        progressBar.setVisibility(View.VISIBLE);
        medicosRepository.getMedicos().enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Medico> medicos = response.body();
                    if (medicos != null && !medicos.isEmpty()) {
                        List<String> especialidadesMedicos = new ArrayList<>();
                        for (Medico medico : medicos) {
                            especialidadesMedicos.add(medico.getEspecialidade());
                        }
                        ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(MarcarConsultaActivity.this, android.R.layout.simple_spinner_item, especialidadesMedicos);
                        adapterMedicos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerMedicos.setAdapter(adapterMedicos);
                        spinnerMedicos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                                String especialidadeSelecionada = parent.getItemAtPosition(position).toString();
                                atualizarConsultaPorEspecialidade(especialidadeSelecionada);
                            }

                            @Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                                // Ninguém selecionado
                            }
                        });
                    } else {
                        Toast.makeText(MarcarConsultaActivity.this, "Nenhum médico disponível", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao carregar médicos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validarEConfirmarAgendamento() {
        String data = etData.getText().toString();
        String hora = etHora.getText().toString();
        String valorConsulta = etValorConsulta.getText().toString();

        if (data.isEmpty() || hora.isEmpty() || valorConsulta.isEmpty()) {
            Toast.makeText(MarcarConsultaActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!data.matches("\\d{2}-\\d{2}-\\d{4}")) {  // Verifica se a data está no formato dd-MM-yyyy
            Toast.makeText(MarcarConsultaActivity.this, "Data inválida!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Double.parseDouble(valorConsulta) <= 0) {
            Toast.makeText(MarcarConsultaActivity.this, "O valor da consulta deve ser maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        consultaSelecionada.setData(data);
        consultaSelecionada.setHora(hora);
        consultaSelecionada.setValor(Double.parseDouble(valorConsulta));

        // Atualizar a consulta na API antes de vincular ao usuário
        atualizarConsultaNaAPI(consultaSelecionada, usuario);
    }

    private void atualizarConsultaNaAPI(Consulta consultaAtualizada, Usuario usuario) {
        if (medicoSelecionado != null) {
            consultaAtualizada.setMedicos(Collections.singletonList(medicoSelecionado));
        }

        progressBar.setVisibility(View.VISIBLE);

        consultaRepository.atualizarConsulta(consultaAtualizada.getId(), consultaAtualizada).enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    salvarConsultaAtualizada(usuario, response.body());
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao atualizar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao atualizar consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Salvar e vincular a consulta atualizada ao usuário
    private void salvarConsultaAtualizada(Usuario usuario, Consulta consulta) {
        if (usuario.getConsultas() == null) {
            usuario.setConsultas(new ArrayList<>());
        }

        usuario.getConsultas().clear();
        usuario.getConsultas().add(consulta);

        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta registrada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("usuario_atualizado", usuario);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao registrar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarConsultaPorEspecialidade(String especialidadeSelecionada) {
        consultaRepository.getConsultas().enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if (response.isSuccessful()) {
                    consultasDisponiveis = response.body();
                    if (consultasDisponiveis != null && !consultasDisponiveis.isEmpty()) {
                        for (Consulta consulta : consultasDisponiveis) {
                            if (consulta.getMedicos() != null && !consulta.getMedicos().isEmpty()) {
                                // Verifica se a especialidade do médico na consulta é igual à selecionada
                                if (consulta.getMedicos().get(0).getEspecialidade().equals(especialidadeSelecionada)) {
                                    consultaSelecionada = consulta;
                                    medicoSelecionado = consulta.getMedicos().get(0);

                                    // Atualiza os campos de texto na UI com os dados da nova consulta
                                    tvLocalConsulta.setText("Local: " + consulta.getLocal());
                                    etData.setText(consulta.getData());
                                    etHora.setText(consulta.getHora());
                                    etValorConsulta.setText(String.valueOf(consulta.getValor()));
                                    consultaId = consulta.getId(); // Atualiza o ID da consulta
                                    break;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(MarcarConsultaActivity.this, "Nenhuma consulta disponível", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar consultas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Consulta>> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao carregar consultas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}