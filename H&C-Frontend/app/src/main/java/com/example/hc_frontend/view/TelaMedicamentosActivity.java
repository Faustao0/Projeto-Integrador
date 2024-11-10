package com.example.hc_frontend.view;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.MedicamentoRepository;
import com.example.hc_frontend.repositories.PacienteRepository;
import com.example.hc_frontend.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelaMedicamentosActivity extends AppCompatActivity {

    private EditText nomeRemedio, dosagemRemedio, frequencia, fabricante;
    private TextView horarioTomar, tvTituloMedicamento;
    private Button salvar;
    private MedicamentoRepository medicamentoRepository;
    private PacienteRepository pacienteRepository;
    private UsuarioRepository usuarioRepository;
    private Paciente paciente;
    private Usuario usuario;
    private Medicamento medicamentoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_medicamentos);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicamentoRepository = retrofit.create(MedicamentoRepository.class);
        pacienteRepository = retrofit.create(PacienteRepository.class);
        usuarioRepository = retrofit.create(UsuarioRepository.class);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("paciente")) {
                paciente = (Paciente) intent.getSerializableExtra("paciente");
            }
            if (intent.hasExtra("usuario")) {
                usuario = (Usuario) intent.getSerializableExtra("usuario");
            }
            if (intent.hasExtra("medicamento")) {
                medicamentoAtual = (Medicamento) intent.getSerializableExtra("medicamento");
            }
        }

        if (paciente == null || usuario == null) {
            Toast.makeText(this, "Paciente ou usuário não encontrado. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        nomeRemedio = findViewById(R.id.nome_remedio);
        dosagemRemedio = findViewById(R.id.dosagem_remedio);
        frequencia = findViewById(R.id.frequência);
        fabricante = findViewById(R.id.fabricante);
        horarioTomar = findViewById(R.id.horario_tomar);
        salvar = findViewById(R.id.salvar);
        tvTituloMedicamento = findViewById(R.id.tvTituloMedicamento);

        // Define o título com base no contexto
        if (medicamentoAtual != null) {
            tvTituloMedicamento.setText("Atualização do Medicamento");
            preencherCamposParaEdicao();
        } else {
            tvTituloMedicamento.setText("Cadastro do Medicamento");
        }

        horarioTomar.setOnClickListener(v -> showTimePickerDialog());

        salvar.setOnClickListener(v -> {
            String nome = nomeRemedio.getText().toString();
            String dosagem = dosagemRemedio.getText().toString();
            String freq = frequencia.getText().toString();
            String fabr = fabricante.getText().toString();
            String horario = horarioTomar.getText().toString();

            if (nome.isEmpty() || dosagem.isEmpty() || fabr.isEmpty() || horario.isEmpty()) {
                Toast.makeText(TelaMedicamentosActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (medicamentoAtual == null) {
                criarNovoMedicamento(nome, dosagem, freq, fabr, horario);
            } else {
                atualizarMedicamentoExistente(nome, dosagem, freq, fabr, horario);
            }
        });
    }

    private void preencherCamposParaEdicao() {
        nomeRemedio.setText(medicamentoAtual.getNome());
        dosagemRemedio.setText(medicamentoAtual.getDosagem());
        frequencia.setText(medicamentoAtual.getFrequencia());
        fabricante.setText(medicamentoAtual.getFabricante());
        horarioTomar.setText(medicamentoAtual.getHorarioTomar());
        salvar.setText("Atualizar");
    }

    private void criarNovoMedicamento(String nome, String dosagem, String freq, String fabr, String horario) {
        Medicamento novoMedicamento = new Medicamento(nome, dosagem, freq, fabr, horario);

        List<Medicamento> medicamentos = paciente.getMedicamentos();
        if (medicamentos == null) {
            medicamentos = new ArrayList<>();
        }
        medicamentos.add(novoMedicamento);
        paciente.setMedicamentos(medicamentos);

        atualizarPaciente(paciente);
    }

    private void atualizarMedicamentoExistente(String nome, String dosagem, String freq, String fabr, String horario) {
        medicamentoAtual.setNome(nome);
        medicamentoAtual.setDosagem(dosagem);
        medicamentoAtual.setFrequencia(freq);
        medicamentoAtual.setFabricante(fabr);
        medicamentoAtual.setHorarioTomar(horario);

        medicamentoRepository.updateMedicamento(medicamentoAtual.getId(), medicamentoAtual).enqueue(new Callback<Medicamento>() {
            @Override
            public void onResponse(Call<Medicamento> call, Response<Medicamento> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TelaMedicamentosActivity.this, "Medicamento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    voltarParaListaDeMedicamentos();
                } else {
                    Toast.makeText(TelaMedicamentosActivity.this, "Erro ao atualizar o medicamento!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Medicamento> call, Throwable t) {
                Toast.makeText(TelaMedicamentosActivity.this, "Erro de conexão ao atualizar o medicamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarPaciente(Paciente paciente) {
        List<Paciente> pacientes = usuario.getPacientes();
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId().equals(paciente.getId())) {
                pacientes.set(i, paciente);
                break;
            }
        }
        usuario.setPacientes(pacientes);

        usuarioRepository.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TelaMedicamentosActivity.this, "Paciente atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    voltarParaListaDeMedicamentos();
                } else {
                    Toast.makeText(TelaMedicamentosActivity.this, "Erro ao atualizar o paciente!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(TelaMedicamentosActivity.this, "Erro de conexão ao atualizar o paciente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void voltarParaListaDeMedicamentos() {
        Intent intentReturn = new Intent(TelaMedicamentosActivity.this, ListaMedicamentosActivity.class);
        intentReturn.putExtra("usuario", usuario);
        startActivity(intentReturn);
        finish();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
            horarioTomar.setText(time);
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
