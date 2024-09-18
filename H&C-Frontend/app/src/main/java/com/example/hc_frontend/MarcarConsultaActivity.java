package com.example.hc_frontend;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Spinner spinnerMedicos;
    private Button buttonConfirmarAgendamento;
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

        // Inicializar componentes
        etData = findViewById(R.id.etData);
        etHora = findViewById(R.id.etHora);
        etValorConsulta = findViewById(R.id.etValorConsulta);
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        tvLocalConsulta = findViewById(R.id.tvLocalConsulta); // Local exibido
        buttonConfirmarAgendamento = findViewById(R.id.buttonConfirmarAgendamento);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // URL base correta da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicosRepository = retrofit.create(MedicosRepository.class);
        consultaRepository = retrofit.create(ConsultaRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);  // Inicializar repositório de usuários

        // Obter o ID da consulta e o usuário do Intent
        consultaId = getIntent().getLongExtra("consultaId", -1L); // ID da consulta recebido (se -1L, é novo)
        usuario = (Usuario) getIntent().getSerializableExtra("usuario"); // Obter o usuário do Intent

        if (usuario == null) {
            Toast.makeText(this, "Erro: Usuário não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Função para abrir o DatePickerDialog ao clicar no campo de data
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

        // Função para abrir o TimePickerDialog ao clicar no campo de hora
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
            String data = etData.getText().toString();
            String hora = etHora.getText().toString();
            String valorConsulta = etValorConsulta.getText().toString();

            if (data.isEmpty() || hora.isEmpty() || valorConsulta.isEmpty()) {
                Toast.makeText(MarcarConsultaActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                // Atualizar os dados da consulta antes de salvar
                if (consultaSelecionada != null) {
                    consultaSelecionada.setData(data);
                    consultaSelecionada.setHora(hora);
                    consultaSelecionada.setValor(Double.parseDouble(valorConsulta));

                    // Atualizar a consulta na API antes de vincular ao usuário
                    atualizarConsultaNaAPI(consultaSelecionada, usuario);
                }
            }
        });
    }

    private void carregarMedicos() {
        medicosRepository.getMedicos().enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {
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
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao carregar médicos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Atualizar o local e preencher dados da consulta ao selecionar a especialidade
    private void atualizarConsultaPorEspecialidade(String especialidadeSelecionada) {
        consultaRepository.getConsultas().enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if (response.isSuccessful()) {
                    consultasDisponiveis = response.body();
                    if (consultasDisponiveis != null && !consultasDisponiveis.isEmpty()) {
                        for (Consulta consulta : consultasDisponiveis) {
                            if (consulta.getMedicos() != null && !consulta.getMedicos().isEmpty()) {
                                if (consulta.getMedicos().get(0).getEspecialidade().equals(especialidadeSelecionada)) {
                                    // Substituir a consulta e médico atual por esta consulta
                                    consultaSelecionada = consulta;
                                    medicoSelecionado = consulta.getMedicos().get(0);

                                    // Atualizar os campos da UI com os dados da nova consulta e médico
                                    tvLocalConsulta.setText("Local: " + consulta.getLocal());
                                    etData.setText(consulta.getData());
                                    etHora.setText(consulta.getHora());
                                    etValorConsulta.setText(String.valueOf(consulta.getValor()));

                                    consultaId = consulta.getId(); // Atualizar o ID da consulta

                                    break;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(MarcarConsultaActivity.this, "Nenhuma consulta disponível", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Consulta>> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro ao carregar consultas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Atualizar a consulta na API com os novos dados antes de vincular ao usuário
    private void atualizarConsultaNaAPI(Consulta consultaAtualizada, Usuario usuario) {
        // Garanta que o médico selecionado seja mantido
        if (medicoSelecionado != null) {
            consultaAtualizada.setMedicos(Collections.singletonList(medicoSelecionado));
        }

        consultaRepository.atualizarConsulta(consultaAtualizada.getId(), consultaAtualizada).enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Consulta consultaAtualizadaResponse = response.body();
                    if (consultaAtualizadaResponse != null) {
                        salvarConsultaAtualizada(usuario, consultaAtualizadaResponse);
                    } else {
                        Toast.makeText(MarcarConsultaActivity.this, "Erro ao atualizar consulta: resposta vazia", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao atualizar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao atualizar consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Salvar e vincular a consulta atualizada ao usuário
    private void salvarConsultaAtualizada(Usuario usuario, Consulta consulta) {
        // Substituir a consulta do usuário em vez de adicionar uma nova
        if (usuario.getConsultas() == null) {
            usuario.setConsultas(new ArrayList<>());
        }
        // Remover qualquer consulta existente antes de adicionar a nova consulta
        usuario.getConsultas().clear();
        usuario.getConsultas().add(consulta);

        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta substituída com sucesso!", Toast.LENGTH_SHORT).show();

                    // Retornar o usuário atualizado à ConsultaActivity
                    Intent intent = new Intent();
                    intent.putExtra("usuario_atualizado", usuario);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao substituir consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}