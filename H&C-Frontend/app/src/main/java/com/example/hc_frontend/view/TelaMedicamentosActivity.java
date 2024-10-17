package com.example.hc_frontend.view;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hc_frontend.R;

public class TelaMedicamentosActivity extends AppCompatActivity {

    private EditText nomeRemedio, dosagemRemedio, frequencia, fabricante, horarioTomar;
    private Button salvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_medicamentos);

        nomeRemedio = findViewById(R.id.nome_remedio);
        dosagemRemedio = findViewById(R.id.dosagem_remedio);
        frequencia = findViewById(R.id.frequência);
        fabricante = findViewById(R.id.fabricante);
        horarioTomar = findViewById(R.id.horario_tomar);
        salvar = findViewById(R.id.salvar);

        salvar.setOnClickListener(v -> {
            String nome = nomeRemedio.getText().toString();
            String dosagem = dosagemRemedio.getText().toString();
            String freq = frequencia.getText().toString();
            String fabr = fabricante.getText().toString();
            String horario = horarioTomar.getText().toString();

            // Aqui você pode adicionar a lógica para salvar os dados ou o que precisar
            Toast.makeText(TelaMedicamentosActivity.this, "Dados salvos!", Toast.LENGTH_SHORT).show();
        });
    }
}