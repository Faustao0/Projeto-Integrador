package com.example.hc_frontend.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Consulta;
import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Paciente;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Object> items;  // Lista contendo consultas e medicamentos
    private Paciente paciente;
    private Context context;
    private Set<String> medicamentoNomesSet;

    // Construtor atualizado para aceitar a lista de itens, o paciente e o contexto
    public MenuAdapter(List<Object> items, Paciente paciente, Context context) {
        this.items = items;
        this.paciente = paciente;
        this.context = context;
        this.medicamentoNomesSet = new HashSet<>();

        if (paciente == null) {
            Toast.makeText(context, "Nenhum paciente associado ao usuário.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_info, parent, false);
        return new MenuViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Object item = items.get(position);

        if (item instanceof Consulta) {
            // Configuração para a consulta
            Consulta consulta = (Consulta) item;
            holder.cardViewConsulta.setVisibility(View.VISIBLE);
            holder.cardViewMedicamento.setVisibility(View.GONE);

            String mensagemConsulta = "O paciente " + paciente.getNome() + " possui uma consulta marcada para a data "
                    + consulta.getData() + " no local " + consulta.getLocal();
            holder.tvConsultaMessage.setText(mensagemConsulta);
        } else if (item instanceof Medicamento) {
            // Configuração para o medicamento
            Medicamento medicamento = (Medicamento) item;
            holder.cardViewMedicamento.setVisibility(View.VISIBLE);
            holder.cardViewConsulta.setVisibility(View.GONE);

            String horarioTomar = medicamento.getHorarioTomar();
            LocalDateTime now = LocalDateTime.now();

            if (horarioTomar != null && !horarioTomar.isEmpty()) {
                try {
                    LocalTime horaMedicamento = LocalTime.parse(horarioTomar);
                    LocalDateTime proximaDose = now.with(horaMedicamento);

                    if (proximaDose.isBefore(now)) {
                        proximaDose = proximaDose.plusDays(1);
                    }

                    Duration duration = Duration.between(now, proximaDose);
                    long hours = duration.toHours();
                    long minutes = duration.toMinutes() % 60;

                    String mensagemMedicamento = hours > 0
                            ? "Faltam " + hours + " horas e " + minutes + " minutos para o/a paciente " + paciente.getNome() + " tomar o remédio " + medicamento.getNome() + "."
                            : "Faltam " + minutes + " minutos para tomar o remédio " + medicamento.getNome() + ".";
                    holder.tvMedicamentoMessage.setText(mensagemMedicamento);
                } catch (DateTimeParseException e) {
                    Log.e("MenuAdapter", "Erro ao parsear o horário do medicamento: " + e.getMessage());
                    holder.tvMedicamentoMessage.setText("Horário do medicamento indisponível.");
                }
            } else {
                holder.tvMedicamentoMessage.setText("Horário do medicamento não especificado.");
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Adiciona a consulta mais próxima à lista de items, mantendo a ordem
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void adicionarConsultaMaisProxima(List<Consulta> consultas) {
        if (consultas != null && !consultas.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            Consulta consultaMaisProxima = consultas.stream()
                    .filter(c -> {
                        try {
                            LocalDate dataConsulta = LocalDate.parse(c.getData(), DATE_FORMATTER);
                            LocalDateTime dataHoraConsulta = dataConsulta.atTime(LocalTime.MIDNIGHT);
                            return dataHoraConsulta.isAfter(now);
                        } catch (DateTimeParseException e) {
                            Log.e("MenuAdapter", "Erro ao parsear a data da consulta: " + e.getMessage());
                            return false;
                        }
                    })
                    .min((c1, c2) -> {
                        try {
                            LocalDate data1 = LocalDate.parse(c1.getData(), DATE_FORMATTER);
                            LocalDateTime dateTime1 = data1.atTime(LocalTime.MIDNIGHT);

                            LocalDate data2 = LocalDate.parse(c2.getData(), DATE_FORMATTER);
                            LocalDateTime dateTime2 = data2.atTime(LocalTime.MIDNIGHT);

                            return dateTime1.compareTo(dateTime2);
                        } catch (DateTimeParseException e) {
                            Log.e("MenuAdapter", "Erro ao parsear a data para comparação: " + e.getMessage());
                            return 0;
                        }
                    })
                    .orElse(null);

            if (consultaMaisProxima != null) {
                items.add(0, consultaMaisProxima);  // Insere a consulta no topo da lista
                notifyDataSetChanged();
            }
        }
    }

    // Adiciona medicamentos únicos à lista de items, preservando a ordem de exibição
    public void adicionarMedicamentos(List<Medicamento> medicamentos) {
        medicamentoNomesSet.clear();

        if (medicamentos != null && !medicamentos.isEmpty()) {
            for (Medicamento medicamento : medicamentos) {
                if (!medicamentoNomesSet.contains(medicamento.getNome())) {
                    medicamentoNomesSet.add(medicamento.getNome());
                    items.add(medicamento);  // Adiciona medicamentos após a consulta
                }
            }
            notifyDataSetChanged();
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewMedicamento, cardViewConsulta;
        TextView tvMedicamentoMessage, tvConsultaMessage;
        TextView tvMedicamentoTitulo, tvConsultaTitulo;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewMedicamento = itemView.findViewById(R.id.medicamentoMenu);
            cardViewConsulta = itemView.findViewById(R.id.consultaMenu);
            tvMedicamentoMessage = itemView.findViewById(R.id.tvMedicamentoMessage);
            tvConsultaMessage = itemView.findViewById(R.id.tvConsultaMessage);
            tvMedicamentoTitulo = itemView.findViewById(R.id.tvMedicamentoTitulo);
            tvConsultaTitulo = itemView.findViewById(R.id.tvConsultaTitulo);
        }
    }
}
