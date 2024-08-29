package com.example.hc_frontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domainViewModel.PacienteViewModel;

public class TelaMedicamentosActivity extends FragmentActivity {

    private EditText etPacienteName;
    private TextView tvNome, tvTelefone, tvEmail, tvCpf, tvIdade, tvHistorico;
    private TextView tvMedicamentoNome, tvDosagem, tvFrequencia, tvValidade, tvFabricante;
    private PacienteViewModel pacienteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_medicamentos);

        etPacienteName = findViewById(R.id.etPatientName);
        Button botaoPesquisarPaciente = findViewById(R.id.BotaoPesquisarPaceinte);
        tvNome = findViewById(R.id.tvNome);
        tvTelefone = findViewById(R.id.tvTelefone);
        tvEmail = findViewById(R.id.tvEmail);
        tvCpf = findViewById(R.id.tvCpf);
        tvIdade = findViewById(R.id.tvIdade);
        tvHistorico = findViewById(R.id.tvHistorico);

        tvMedicamentoNome = findViewById(R.id.tvMedicamentoNome);
        tvDosagem = findViewById(R.id.tvDosagem);
        tvFrequencia = findViewById(R.id.tvFrequencia);
        tvValidade = findViewById(R.id.tvValidade);
        tvFabricante = findViewById(R.id.tvFabricante);

        // Inicializando o ViewModel
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);

        botaoPesquisarPaciente.setOnClickListener(v -> {
            String nomePaciente = etPacienteName.getText().toString();
            if (!nomePaciente.isEmpty()) {
                pacienteViewModel.searchPatientByName(nomePaciente);
            } else {
                Toast.makeText(getApplicationContext(), "Por favor, digite o nome do paciente", Toast.LENGTH_SHORT).show();
            }
        });

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

                    if (paciente.getMedicamentos() != null && !paciente.getMedicamentos().isEmpty()) {
                        Medicamento medicamento = paciente.getMedicamentos().get(0);  // Mostra o primeiro medicamento, se houver
                        tvMedicamentoNome.setText("Nome do Medicamento: " + medicamento.getNome());
                        tvDosagem.setText("Dosagem: " + medicamento.getDosagem());
                        tvFrequencia.setText("Frequência: " + medicamento.getFrequencia());
                        tvValidade.setText("Validade: " + medicamento.getValidade());
                        tvFabricante.setText("Fabricante: " + medicamento.getFabricante());
                    } else {
                        tvMedicamentoNome.setText("Nenhum medicamento vinculado");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Paciente não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}