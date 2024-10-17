package com.example.hc_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Consulta;
import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder> {

    private final List<Consulta> consultas;
    private final OnConsultaClickListener listener;

    public interface OnConsultaClickListener {
        void onCancelarClick(Consulta consulta);
        void onReagendarClick(Consulta consulta);
    }

    public ConsultaAdapter(List<Consulta> consultas, OnConsultaClickListener listener) {
        this.consultas = consultas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta, parent, false);
        return new ConsultaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        Consulta consulta = consultas.get(position);
        holder.bind(consulta, listener);
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    static class ConsultaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDate, tvTime, tvLocation, tvValor, tvMedicoNome, tvMedicoTelefone,
                               tvMedicoEmail, tvMedicoCpf, tvMedicoCrm, tvMedicoEspecialidade;
        private final Button btnCancelar, btnReagendar;

        public ConsultaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvMedicoNome = itemView.findViewById(R.id.tvMedicoNome);
            tvMedicoTelefone = itemView.findViewById(R.id.tvMedicoTelefone);
            tvMedicoEmail = itemView.findViewById(R.id.tvMedicoEmail);
            tvMedicoCpf = itemView.findViewById(R.id.tvMedicoCpf);
            tvMedicoCrm = itemView.findViewById(R.id.tvMedicoCrm);
            tvMedicoEspecialidade = itemView.findViewById(R.id.tvMedicoEspecialidade);
            btnCancelar = itemView.findViewById(R.id.btnCancelarConsulta);
            btnReagendar = itemView.findViewById(R.id.btnReagendarConsulta);
        }

        public void bind(final Consulta consulta, final OnConsultaClickListener listener) {
            tvDate.setText("Data: " + consulta.getData());
            tvTime.setText("Hora: " + consulta.getHora());
            tvLocation.setText("Local: " + consulta.getLocal());
            tvValor.setText("Valor: " + consulta.getValor());

            if (!consulta.getMedicos().isEmpty()) {
                tvMedicoNome.setText("Nome do Médico: " + consulta.getMedicos().get(0).getNome());
                tvMedicoTelefone.setText("Telefone: " + consulta.getMedicos().get(0).getTelefone());
                tvMedicoEmail.setText("Email: " + consulta.getMedicos().get(0).getEmail());
                tvMedicoCpf.setText("CPF: " + consulta.getMedicos().get(0).getCpf());
                tvMedicoCrm.setText("CRM: " + consulta.getMedicos().get(0).getCrm());
                tvMedicoEspecialidade.setText("Especialidade do médico: " + consulta.getMedicos().get(0).getEspecialidade());
            }

            btnCancelar.setOnClickListener(v -> listener.onCancelarClick(consulta));
            btnReagendar.setOnClickListener(v -> listener.onReagendarClick(consulta));
        }
    }
}
