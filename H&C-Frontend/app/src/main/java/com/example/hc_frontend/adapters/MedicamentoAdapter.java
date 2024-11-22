package com.example.hc_frontend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.MedicamentoRepository;
import com.example.hc_frontend.view.ListaMedicamentosActivity;
import com.example.hc_frontend.view.TelaMedicamentosActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private List<Medicamento> medicamentos;
    private MedicamentoRepository medicamentoRepository;
    private Context context;
    private Usuario usuario;
    private Paciente paciente;
    private Set<Long> medicamentosExcluidosIds = new HashSet<>();

    // Construtor atualizado para aceitar paciente e usuário
    public MedicamentoAdapter(List<Medicamento> medicamentos, Paciente paciente, Usuario usuario, Context context) {
        this.medicamentos = medicamentos;
        this.context = context;
        this.usuario = usuario;
        this.paciente = paciente;

        // Inicializando Retrofit e o repositório para exclusão
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicamentoRepository = retrofit.create(MedicamentoRepository.class);
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
        notifyDataSetChanged();  // Notifica o adapter que os dados mudaram
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);
        holder.nomeRemedio.setText("Nome do medicamento: " + medicamento.getNome());
        holder.horarioRemedio.setText("Horário de tomar: " + medicamento.getHorarioTomar());
        holder.frequenciaRemedio.setText("Frequência do medicamento: " + medicamento.getFrequencia());
        holder.dosagem_medicamento.setText("Dosagem do medicamento: " + medicamento.getDosagem());
        holder.fabricante_medicamento.setText("Fabricante do medicamento: " + medicamento.getFabricante());

        // Botão de excluir o medicamento com confirmação
        holder.btnExcluir.setOnClickListener(v -> {
            // Exibir o AlertDialog de confirmação
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Medicamento")
                    .setMessage("Você deseja realmente excluir este medicamento?")
                    .setPositiveButton("Sim", (dialog, which) -> excluirMedicamento(medicamento, position))
                    .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Botão para editar o medicamento
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, TelaMedicamentosActivity.class);
            intent.putExtra("medicamento", medicamento);  // Passa o medicamento para edição
            intent.putExtra("paciente", paciente);        // Passa o paciente
            intent.putExtra("usuario", usuario);          // Passa o usuário
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicamentos != null ? medicamentos.size() : 0;
    }

    private void excluirMedicamento(Medicamento medicamento, int position) {
        Call<Void> call = medicamentoRepository.deleteMedicamento(medicamento.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Medicamento excluído com sucesso!", Toast.LENGTH_SHORT).show();

                    medicamentosExcluidosIds.add(medicamento.getId());

                    medicamentos.remove(position);
                    notifyItemRemoved(position);

                    ((ListaMedicamentosActivity) context).carregarMedicamentosDoUsuario();
                } else {
                    Toast.makeText(context, "Erro ao excluir o medicamento.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Erro de conexão ao tentar excluir o medicamento.", Toast.LENGTH_SHORT).show();
                Log.e("MedicamentoAdapter", "Erro ao excluir o medicamento: " + t.getMessage());
            }
        });
    }

    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeRemedio, horarioRemedio, frequenciaRemedio, dosagem_medicamento, fabricante_medicamento;
        Button btnExcluir, btnEditar;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeRemedio = itemView.findViewById(R.id.nome_medicamento);
            horarioRemedio = itemView.findViewById(R.id.horario_medicamento);
            frequenciaRemedio = itemView.findViewById(R.id.frequencia_medicamento);
            dosagem_medicamento = itemView.findViewById(R.id.dosagem_medicamento);
            fabricante_medicamento = itemView.findViewById(R.id.fabricante_medicamento);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);  // Botão de excluir
            btnEditar = itemView.findViewById(R.id.btnEditar);    // Botão de editar
        }
    }
}
