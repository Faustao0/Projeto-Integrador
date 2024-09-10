package com.example.hc_frontend;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medico;
import com.example.hc_frontend.repositories.ConsultaRepository;
import com.example.hc_frontend.repositories.MedicosRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarcarConsultaActivity extends AppCompatActivity {

    private TextInputEditText etData, etHora, etValorConsulta;
    private Spinner spinnerMedicos, spinnerLocais;
    private Button buttonConfirmarAgendamento;
    private MedicosRepository medicosRepository;
    private ConsultaRepository consultaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_consulta);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Insira a URL base correta da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicosRepository = retrofit.create(MedicosRepository.class);
        consultaRepository = retrofit.create(ConsultaRepository.class);

        // Inicializar componentes
        etData = findViewById(R.id.etData);
        etHora = findViewById(R.id.etHora);
        etValorConsulta = findViewById(R.id.etValorConsulta);
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        spinnerLocais = findViewById(R.id.spinnerLocais);
        buttonConfirmarAgendamento = findViewById(R.id.buttonConfirmarAgendamento);

        // Exibir DatePicker ao clicar no campo de data
        etData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MarcarConsultaActivity.this,
                    (view, year, month, dayOfMonth) -> etData.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Exibir TimePicker ao clicar no campo de hora
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

        // Carregar médicos e locais da API
        carregarMedicos();
        carregarLocais();

        // Ação ao clicar no botão de confirmação
        buttonConfirmarAgendamento.setOnClickListener(v -> {
            String data = etData.getText().toString();
            String hora = etHora.getText().toString();
            String medicoSelecionado = spinnerMedicos.getSelectedItem().toString();
            String localSelecionado = spinnerLocais.getSelectedItem().toString();
            String valorConsulta = etValorConsulta.getText().toString();

            if (data.isEmpty() || hora.isEmpty() || valorConsulta.isEmpty()) {
                Toast.makeText(MarcarConsultaActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                Long consultaId = getIntent().getLongExtra("consultaId", -1L); // ID da consulta
                atualizarConsulta(consultaId, data, hora, medicoSelecionado, localSelecionado, valorConsulta);
            }
        });
    }

    private void carregarMedicos() {
        medicosRepository.getMedicos().enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {
                if (response.isSuccessful()) {
                    List<Medico> medicos = response.body();
                    List<String> nomesMedicos = new ArrayList<>();
                    for (Medico medico : medicos) {
                        nomesMedicos.add(medico.getEspecialidade());
                    }

                    ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(MarcarConsultaActivity.this, android.R.layout.simple_spinner_item, nomesMedicos);
                    adapterMedicos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMedicos.setAdapter(adapterMedicos);
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarLocais() {
        consultaRepository.getConsultas().enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if (response.isSuccessful()) {
                    List<Consulta> locais = response.body();
                    List<String> nomesLocais = new ArrayList<>();
                    for (Consulta local : locais) {
                        nomesLocais.add(local.getLocal());
                    }

                    ArrayAdapter<String> adapterLocais = new ArrayAdapter<>(MarcarConsultaActivity.this, android.R.layout.simple_spinner_item, nomesLocais);
                    adapterLocais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLocais.setAdapter(adapterLocais);
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar locais", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Consulta>> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarConsulta(Long consultaId, String data, String hora, String medicoSelecionado, String localSelecionado, String valorConsulta) {
        // Cria uma nova consulta com os dados atualizados
        Consulta consultaAtualizada = new Consulta();
        consultaAtualizada.setData(data);  // Usar diretamente a string
        consultaAtualizada.setHora(hora);  // Usar diretamente a string
        consultaAtualizada.setLocal(localSelecionado);
        consultaAtualizada.setValor(Double.parseDouble(valorConsulta));

        // Buscar o médico completo pelo nome ou outro identificador
        medicosRepository.getMedicos().enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {
                if (response.isSuccessful()) {
                    List<Medico> medicos = response.body();
                    for (Medico medico : medicos) {
                        if (medico.getEspecialidade().equals(medicoSelecionado)) {
                            consultaAtualizada.setMedicos(Collections.singletonList(medico)); // Define o médico completo na consulta

                            // Agora que temos a consulta atualizada, podemos enviá-la para o backend
                            enviarConsultaAtualizada(consultaId, consultaAtualizada);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarConsultaAtualizada(Long consultaId, Consulta consultaAtualizada) {
        consultaRepository.atualizarConsulta(consultaId, consultaAtualizada).enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta atualizada com sucesso!", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao atualizar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}