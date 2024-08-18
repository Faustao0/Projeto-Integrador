package com.example.hc_frontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import domain.Medicamento;
import domain.Paciente;
import domainViewModel.PacienteViewModel;
import domainViewModel.MedicamentoViewModel;

public class TelaMedicamentosActivity extends FragmentActivity {

    private EditText etPacienteName;
    private TextView tvNome, tvTelefone, tvEmail, tvCpf, tvIdade, tvHistorico;

    private EditText etMedicamentoName;
    private TextView tvMedicamentoNome, tvDosagem, tvFrequencia, tvValidade, tvFabricante;

    private PacienteViewModel pacienteViewModel;
    private MedicamentoViewModel medicamentoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_medicamentos);

        // Inicializando componentes de UI para Paciente
        etPacienteName = findViewById(R.id.etPatientName);
        Button botaoPesquisarPaciente = findViewById(R.id.BotaoPesquisarPaceinte);
        tvNome = findViewById(R.id.tvNome);
        tvTelefone = findViewById(R.id.tvTelefone);
        tvEmail = findViewById(R.id.tvEmail);
        tvCpf = findViewById(R.id.tvCpf);
        tvIdade = findViewById(R.id.tvIdade);
        tvHistorico = findViewById(R.id.tvHistorico);

        // Inicializando componentes de UI para Medicamento
        etMedicamentoName = findViewById(R.id.etMedicamrntoName);
        Button botaoPesquisarMedicamento = findViewById(R.id.BotaoPesquisarMedicamntos);
        tvMedicamentoNome = findViewById(R.id.tvMedicamentoNome);
        tvDosagem = findViewById(R.id.tvDosagem);
        tvFrequencia = findViewById(R.id.tvFrequencia);
        tvValidade = findViewById(R.id.tvValidade);
        tvFabricante = findViewById(R.id.tvFabricante);

        // Inicializando os ViewModels
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
        medicamentoViewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        // Configurando ação de buscar paciente
        botaoPesquisarPaciente.setOnClickListener(v -> {
            String nomePaciente = etPacienteName.getText().toString();
            if (!nomePaciente.isEmpty()) {
                pacienteViewModel.searchPatientByName(nomePaciente);
            } else {
                Toast.makeText(getApplicationContext(), "Por favor, digite o nome do paciente", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurando ação de buscar medicamento
        botaoPesquisarMedicamento.setOnClickListener(v -> {
            String nomeMedicamento = etMedicamentoName.getText().toString();
            if (!nomeMedicamento.isEmpty()) {
                medicamentoViewModel.searchMedicamentoByName(nomeMedicamento);
            } else {
                Toast.makeText(getApplicationContext(), "Por favor, digite o nome do medicamento", Toast.LENGTH_SHORT).show();
            }
        });

        // Observando as mudanças no paciente
        pacienteViewModel.getPaciente().observe(this, new Observer<Paciente>() {
            @Override
            public void onChanged(Paciente paciente) {
                if (paciente != null) {
                    tvNome.setText("Nome: " + paciente.getNome());
                    tvTelefone.setText("Telefone: " + paciente.getTelefone());
                    tvEmail.setText("Email: " + paciente.getEmail());
                    tvCpf.setText("CPF: " + paciente.getCpf());
                    tvIdade.setText("Idade: " + paciente.getIdade());
                    tvHistorico.setText("Histórico Médico: " + paciente.getHistoricoMedico());
                } else {
                    Toast.makeText(getApplicationContext(), "Paciente não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observando as mudanças no medicamento
        medicamentoViewModel.getMedicamento().observe(this, new Observer<Medicamento>() {
            @Override
            public void onChanged(Medicamento medicamento) {
                if (medicamento != null) {
                    tvMedicamentoNome.setText("Nome do Medicamento: " + medicamento.getNome());
                    tvDosagem.setText("Dosagem: " + medicamento.getDosagem());
                    tvFrequencia.setText("Frequência: " + medicamento.getFrequencia());
                    tvValidade.setText("Validade: " + medicamento.getValidade());
                    tvFabricante.setText("Fabricante: " + medicamento.getFabricante());
                } else {
                    Toast.makeText(getApplicationContext(), "Medicamento não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
