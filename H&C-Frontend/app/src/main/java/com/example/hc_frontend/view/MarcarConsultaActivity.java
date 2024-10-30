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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarcarConsultaActivity extends AppCompatActivity {

    private TextInputEditText etData, etHora, etValorConsulta;
    private Spinner spinnerMedicos, spinnerLocaisConsulta;
    private Button buttonConfirmarAgendamento;
    private ProgressBar progressBar;
    private MedicosRepository medicosRepository;
    private ConsultaRepository consultaRepository;
    private UsuarioRepository usuarioRepository;
    private Usuario usuario;
    private Medico medicoSelecionado;
    private String localSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_consulta);

        etData = findViewById(R.id.etData);
        etHora = findViewById(R.id.etHora);
        etValorConsulta = findViewById(R.id.etValorConsulta);
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        spinnerLocaisConsulta = findViewById(R.id.spinnerLocaisConsulta);
        buttonConfirmarAgendamento = findViewById(R.id.buttonConfirmarAgendamento);
        progressBar = findViewById(R.id.progressBar);

        // Inicializa Retrofit e repositórios
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

        // Campos de data e hora
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

        // Carrega os médicos no Spinner para selecionar a especialidade
        carregarMedicos();

        // Configura o Spinner de locais de consulta
        configurarSpinnerLocais();

        // Gera um valor aleatório para a consulta
        gerarValorAleatorioConsulta();

        buttonConfirmarAgendamento.setOnClickListener(v -> {
            validarEConfirmarAgendamento();
        });
    }

    // Método para configurar o Spinner dos locais de consulta
    private void configurarSpinnerLocais() {
        // Lista de locais predefinidos
        String[] locais = {
                "Clínica Viver Mais",
                "Posto de Saúde",
                "Imed",
                "Humana Clinic",
                "Centro de Saúde",
                "Popular Med Center",
                "Amor e Saúde"
        };

        // Configura o ArrayAdapter para o Spinner
        ArrayAdapter<String> adapterLocais = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        adapterLocais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocaisConsulta.setAdapter(adapterLocais);

        spinnerLocaisConsulta.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                localSelecionado = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Nenhuma ação necessária
            }
        });
    }

    // Método para gerar um valor aleatório para a consulta
    private void gerarValorAleatorioConsulta() {
        int[] valoresPermitidos = {30, 35, 40, 45, 50, 55, 60, 70, 80, 90};
        Random random = new Random();
        int valorAleatorio = valoresPermitidos[random.nextInt(valoresPermitidos.length)];
        etValorConsulta.setText(String.valueOf(valorAleatorio));
    }

    // Carrega os médicos e suas especialidades no Spinner
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

                        // Seleciona o médico quando a especialidade for escolhida
                        spinnerMedicos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                                String especialidadeSelecionada = parent.getItemAtPosition(position).toString();
                                selecionarMedicoPorEspecialidade(especialidadeSelecionada, medicos);
                            }

                            @Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                                medicoSelecionado = null;
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

    // Seleciona o médico com base na especialidade escolhida
    private void selecionarMedicoPorEspecialidade(String especialidade, List<Medico> medicos) {
        for (Medico medico : medicos) {
            if (medico.getEspecialidade().equals(especialidade)) {
                medicoSelecionado = medico;
                break;
            }
        }
    }

    // Valida os dados e confirma o agendamento
    private void validarEConfirmarAgendamento() {
        String data = etData.getText().toString();
        String hora = etHora.getText().toString();
        String valorConsulta = etValorConsulta.getText().toString();

        if (data.isEmpty() || hora.isEmpty() || valorConsulta.isEmpty() || localSelecionado == null || medicoSelecionado == null) {
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

        // Cria uma nova consulta e vincula ao médico e ao usuário
        criarConsultaNaAPI(data, hora, Double.parseDouble(valorConsulta), localSelecionado, medicoSelecionado, usuario);
    }

    // Cria uma nova consulta na API e vincula ao médico e ao usuário
    private void criarConsultaNaAPI(String data, String hora, double valor, String local, Medico medico, Usuario usuario) {
        Consulta novaConsulta = new Consulta();
        novaConsulta.setData(data);
        novaConsulta.setHora(hora);
        novaConsulta.setValor(valor);
        novaConsulta.setLocal(local);
        novaConsulta.setMedicos(new ArrayList<>());
        novaConsulta.getMedicos().add(medico);

        // Vincula o usuário à consulta antes de enviar para o backend
        novaConsulta.setUsuario(usuario);

        consultaRepository.marcarConsulta(novaConsulta).enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Consulta consultaCriada = response.body();
                    // Vincular a consulta ao usuário no backend
                    adicionarConsultaAoUsuario(consultaCriada, usuario);
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao criar consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão ao criar consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adiciona a consulta recém-criada ao usuário
    private void adicionarConsultaAoUsuario(Consulta consulta, Usuario usuario) {
        usuario.getConsultas().add(consulta);
        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarcarConsultaActivity.this, "Consulta marcada com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MarcarConsultaActivity.this, ConsultaActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MarcarConsultaActivity.this, "Erro ao vincular consulta ao usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MarcarConsultaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
