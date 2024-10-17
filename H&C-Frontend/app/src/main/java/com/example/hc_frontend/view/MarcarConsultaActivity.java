package com.example.hc_frontend.view;

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

import com.example.hc_frontend.R;
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
    private Usuario usuario;
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
        progressBar = findViewById(R.id.progressBar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicosRepository = retrofit.create(MedicosRepository.class);
        consultaRepository = retrofit.create(ConsultaRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);

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

        if (!data.matches("\\d{2}-\\d{2}-\\d{4}")) {
            Toast.makeText(MarcarConsultaActivity.this, "Data inválida!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Double.parseDouble(valorConsulta) <= 0) {
            Toast.makeText(MarcarConsultaActivity.this, "O valor da consulta deve ser maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (consultaSelecionada != null) {
            // Atualizar a consulta existente
            consultaSelecionada.setData(data);
            consultaSelecionada.setHora(hora);
            consultaSelecionada.setValor(Double.parseDouble(valorConsulta));
            atualizarConsultaNaAPI(consultaSelecionada, usuario);
        } else {
            // Criar uma nova consulta e vincular ao médico
            Consulta novaConsulta = new Consulta();
            novaConsulta.setData(data);
            novaConsulta.setHora(hora);
            novaConsulta.setValor(Double.parseDouble(valorConsulta));
            novaConsulta.setLocal("Consultório do Dr. " + medicoSelecionado.getNome());
            novaConsulta.setMedicos(new ArrayList<>());
            novaConsulta.getMedicos().add(medicoSelecionado);
            adicionarConsultaAoUsuario(novaConsulta);
        }
    }

    private void atualizarConsultaNaAPI(Consulta consultaAtualizada, Usuario usuario) {
        consultaRepository.atualizarConsulta(consultaAtualizada.getId(), consultaAtualizada).enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta atualizada com sucesso", Toast.LENGTH_SHORT).show();
                    salvarConsultaAtualizada(usuario, response.body()); // Chama o método corrigido
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

    private void adicionarConsultaAoUsuario(Consulta novaConsulta) {
        usuario.getConsultas().add(novaConsulta);
        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta adicionada com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("usuario_atualizado", usuario);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao adicionar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarConsultaAtualizada(Usuario usuario, Consulta consultaAtualizada) {
        boolean consultaExistente = false;
        for (int i = 0; i < usuario.getConsultas().size(); i++) {
            Consulta consulta = usuario.getConsultas().get(i);
            if (consulta.getId().equals(consultaAtualizada.getId())) {
                usuario.getConsultas().set(i, consultaAtualizada); // Atualiza a consulta existente
                consultaExistente = true;
                break;
            }
        }

        if (!consultaExistente) {
            // Se a consulta não existir, adicione-a
            usuario.getConsultas().add(consultaAtualizada);
        }

        // Persistir os dados atualizados do usuário
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
        consultaSelecionada = null;

        // Carrega todas as consultas do repositório de consultas
        consultaRepository.getConsultas().enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if (response.isSuccessful()) {
                    List<Consulta> consultas = response.body();
                    if (consultas != null && !consultas.isEmpty()) {
                        for (Consulta consulta : consultas) {
                            // Verifica se a especialidade do médico da consulta corresponde à especialidade selecionada
                            Medico medicoDaConsulta = consulta.getMedicos().get(0);
                            if (medicoDaConsulta.getEspecialidade().equals(especialidadeSelecionada)) {
                                consultaSelecionada = consulta;
                                medicoSelecionado = medicoDaConsulta;

                                // Atualiza os campos da UI com os dados da consulta encontrada
                                tvLocalConsulta.setText("Local: " + consulta.getLocal());
                                etData.setText(consulta.getData());
                                etHora.setText(consulta.getHora());
                                etValorConsulta.setText(String.valueOf(consulta.getValor()));

                                break;
                            }
                        }

                        if (consultaSelecionada == null) {
                            Toast.makeText(MarcarConsultaActivity.this, "Nenhuma consulta encontrada para essa especialidade", Toast.LENGTH_SHORT).show();
                            limparCamposConsulta();
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

    private void limparCamposConsulta() {
        etData.setText("");
        etHora.setText("");
        etValorConsulta.setText("");
        tvLocalConsulta.setText("");
    }
}